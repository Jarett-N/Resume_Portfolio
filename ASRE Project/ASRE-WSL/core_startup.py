#!/usr/bin/env python3
"""
ASRE Core Startup with Flask Integration
-----------------------------------------
Flask middleware that bridges frontend (home.html) with CommandModule
"""

from flask import Flask, render_template, jsonify, request, send_from_directory
import os
import threading
import time
import socket
import json
import logging
import qi
from pathlib import Path
from command import CommandModule

# ========== DIRECTORY SETUP ==========
BASE_DIR = os.path.abspath(os.path.dirname(__file__))
FRONTEND_DIR = os.path.join(BASE_DIR, "ASRE-Frontend")
TEMPLATES_DIR = os.path.join(FRONTEND_DIR, "templates")
STATIC_DIR = os.path.join(FRONTEND_DIR, "static")
ACTIONS_DIR = os.path.join(BASE_DIR, "actions")

# ========== LOGGING SETUP ==========
os.makedirs("logs", exist_ok=True)
logging.basicConfig(
    filename="logs/core_startup.log",
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
)

# ========== FLASK APP ==========
app = Flask(__name__, 
            template_folder=TEMPLATES_DIR,
            static_folder=STATIC_DIR)

app.config["HOST"] = os.getenv("ASRE_HOST", "0.0.0.0")
app.config["PORT"] = int(os.getenv("ASRE_PORT", 5001))

shutdown_flag = threading.Event()
server_thread = None

# ========== ASRE PIPELINE CLASS ==========
class ASREPipeline:
    """Central pipeline managing robot interaction"""
    _instance = None
    _lock = threading.Lock()

    def __new__(cls):
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super(ASREPipeline, cls).__new__(cls)
                    cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return
        
        self.cmd_module = CommandModule.getInstance()
        self.robot_connected = False
        self.current_action = None
        self.action_thread = None
        self._initialized = True
        logging.info("ASRE Pipeline initialized")

    def configure_robot(self, ip: str, port: int = 9559):
        """Configure robot connection parameters"""
        try:
            self.cmd_module.setIP(ip)
            self.cmd_module.setPort(port)
            logging.info(f"Robot configured: {ip}:{port}")
            return {"success": True, "message": f"Robot configured at {ip}:{port}"}
        except Exception as e:
            logging.error(f"Failed to configure robot: {e}")
            return {"success": False, "message": str(e)}

    def test_connection(self):
        """Test connection to robot"""
        try:
            self.cmd_module._connect_qi()
            volume = self.cmd_module.getVolume()
            self.cmd_module._disconnect_qi()
            self.robot_connected = True
            logging.info("Robot connection test successful")
            return {"success": True, "message": "Connected", "volume": volume}
        except Exception as e:
            self.robot_connected = False
            logging.error(f"Connection test failed: {e}")
            return {"success": False, "message": str(e)}

    def get_volume(self):
        """Get current robot volume"""
        try:
            self.cmd_module._connect_qi()
            volume = self.cmd_module.getVolume()
            self.cmd_module._disconnect_qi()
            return {"success": True, "volume": volume}
        except Exception as e:
            return {"success": False, "message": str(e)}

    def set_volume(self, volume: int):
        """Set robot volume (0-100)"""
        try:
            self.cmd_module._connect_qi()
            self.cmd_module.setVolume(volume)
            self.cmd_module._disconnect_qi()
            logging.info(f"Volume set to {volume}")
            return {"success": True, "volume": volume}
        except Exception as e:
            logging.error(f"Failed to set volume: {e}")
            return {"success": False, "message": str(e)}

    def load_action(self, action_path: str):
        """Load and validate action file"""
        try:
            if not os.path.exists(action_path):
                return {"success": False, "message": "Action file not found"}
            
            with open(action_path, 'r', encoding='utf-8') as f:
                action_data = json.load(f)
            
            if not action_data.get("valid", False):
                return {"success": False, "message": "Action marked as invalid"}
            
            self.current_action = action_path
            logging.info(f"Action loaded: {action_path}")
            
            return {
                "success": True,
                "action": {
                    "name": action_data.get("actionName", "Unknown"),
                    "category": action_data.get("category", "Uncategorized"),
                    "totalTime": action_data.get("totalTime", 0),
                    "hasAudio": bool(action_data.get("audio")),
                    "frameCount": len(action_data.get("movementSequence", []))
                }
            }
        except Exception as e:
            logging.error(f"Failed to load action: {e}")
            return {"success": False, "message": str(e)}

    def execute_action(self, action_path=None, blocking=False):
        """Execute action on robot"""
        if action_path is None:
            action_path = self.current_action
        
        if action_path is None:
            return {"success": False, "message": "No action loaded"}
        
        try:
            if blocking:
                self.cmd_module.sendAction(action_path)
                logging.info(f"Action executed (blocking): {action_path}")
                return {"success": True, "message": "Action completed", "blocking": True}
            else:
                def run_action():
                    try:
                        self.cmd_module.sendAction(action_path)
                        logging.info(f"Action executed (async): {action_path}")
                    except Exception as e:
                        logging.error(f"Action execution error: {e}")
                
                self.action_thread = threading.Thread(target=run_action, daemon=True)
                self.action_thread.start()
                return {"success": True, "message": "Action started", "blocking": False}
        except Exception as e:
            logging.error(f"Failed to execute action: {e}")
            return {"success": False, "message": str(e)}

    def stop_action(self):
        """Stop current action"""
        try:
            self.cmd_module.stop_motion()
            logging.info("Action stopped")
            return {"success": True, "message": "Action stopped"}
        except Exception as e:
            return {"success": False, "message": str(e)}

    def start_motion(self, direction: str, speed: float = 0.3):
        """Start continuous motion"""
        try:
            self.cmd_module.start_motion(direction, speed)
            logging.info(f"Motion started: {direction} at {speed}")
            return {"success": True, "message": f"Moving {direction}", "direction": direction}
        except Exception as e:
            logging.error(f"Failed to start motion: {e}")
            return {"success": False, "message": str(e)}

    def stop_motion(self):
        """Stop motion"""
        try:
            self.cmd_module.stop_motion()
            logging.info("Motion stopped")
            return {"success": True, "message": "Motion stopped"}
        except Exception as e:
            return {"success": False, "message": str(e)}

    def scan_actions(self, actions_dir=None):
        """Scan directory for available actions"""
        if actions_dir is None:
            actions_dir = ACTIONS_DIR
            
        try:
            actions = []
            actions_path = Path(actions_dir)
            
            if not actions_path.exists():
                return {"success": False, "message": "Actions directory not found"}
            
            for json_file in actions_path.rglob("*.json"):
                try:
                    with open(json_file, 'r', encoding='utf-8') as f:
                        action_data = json.load(f)
                    
                    if action_data.get("valid", False):
                        actions.append({
                            "path": str(json_file),
                            "name": action_data.get("actionName", json_file.stem),
                            "category": action_data.get("category", "Uncategorized"),
                            "totalTime": action_data.get("totalTime", 0),
                            "hasAudio": bool(action_data.get("audio")),
                            "icon": action_data.get("icon", "🤖")
                        })
                except Exception as e:
                    logging.warning(f"Failed to read action {json_file}: {e}")
            
            logging.info(f"Scanned {len(actions)} valid actions")
            return {"success": True, "count": len(actions), "actions": actions}
        except Exception as e:
            logging.error(f"Failed to scan actions: {e}")
            return {"success": False, "message": str(e)}

    def get_status(self):
        """Get current pipeline status"""
        return {
            "connected": self.robot_connected,
            "ip": self.cmd_module.getIP(),
            "port": self.cmd_module.getPort(),
            "current_action": self.current_action,
            "action_running": self.action_thread is not None and self.action_thread.is_alive()
        }

# ========== PIPELINE SINGLETON ==========
_pipeline = None

def get_pipeline():
    """Get singleton pipeline instance"""
    global _pipeline
    if _pipeline is None:
        _pipeline = ASREPipeline()
    return _pipeline

# ========== FLASK ROUTES ==========

@app.route("/")
def home():
    """Serve home.html"""
    return render_template("home.html")

@app.route("/health", methods=["GET"])
def health_check():
    """Health check endpoint"""
    return jsonify({
        "status": "ok",
        "timestamp": time.time()
    }), 200

@app.route("/shutdown", methods=["GET", "POST"])
def shutdown():
    """Shutdown server"""
    print("[ASRE] Shutdown requested.")
    shutdown_flag.set()
    return "Server shutting down..."

# ========== ROBOT CONNECTION ROUTES ==========

@app.route("/api/robot/configure", methods=["POST"])
def configure_robot():
    """Configure robot IP and port"""
    data = request.get_json()
    ip = data.get("ip")
    port = data.get("port", 9559)
    
    result = get_pipeline().configure_robot(ip, port)
    return jsonify(result)

@app.route("/api/robot/connect", methods=["POST"])
def test_connection():
    """Test robot connection"""
    result = get_pipeline().test_connection()
    return jsonify(result)

@app.route("/api/robot/status", methods=["GET"])
def robot_status():
    """Get robot status"""
    status = get_pipeline().get_status()
    return jsonify(status)

# ========== VOLUME ROUTES ==========

@app.route("/api/volume", methods=["GET"])
def get_volume():
    """Get current volume"""
    result = get_pipeline().get_volume()
    return jsonify(result)

@app.route("/api/volume", methods=["POST"])
def set_volume():
    """Set volume"""
    data = request.get_json()
    volume = data.get("volume", 50)
    result = get_pipeline().set_volume(volume)
    return jsonify(result)

# ========== ACTION ROUTES ==========

@app.route("/api/actions/scan", methods=["GET"])
def scan_actions():
    """Scan for available actions"""
    result = get_pipeline().scan_actions()
    return jsonify(result)

@app.route("/api/actions/load", methods=["POST"])
def load_action():
    """Load an action"""
    data = request.get_json()
    action_path = data.get("path")
    result = get_pipeline().load_action(action_path)
    return jsonify(result)

@app.route("/api/actions/execute", methods=["POST"])
def execute_action():
    """Execute an action"""
    data = request.get_json()
    action_path = data.get("path")
    blocking = data.get("blocking", False)
    result = get_pipeline().execute_action(action_path, blocking)
    return jsonify(result)

@app.route("/api/actions/stop", methods=["POST"])
def stop_action():
    """Stop current action"""
    result = get_pipeline().stop_action()
    return jsonify(result)

# Legacy route for compatibility
@app.route("/execute/<action_id>", methods=["POST"])
def execute_action_legacy(action_id):
    """Execute action by ID (legacy)"""
    print(f"[ASRE] Executing action: {action_id}")
    action_path = os.path.join(ACTIONS_DIR, f"{action_id}.json")
    result = get_pipeline().execute_action(action_path)
    return jsonify(result)

# ========== MOTION CONTROL ROUTES ==========

@app.route("/api/motion/start", methods=["POST"])
def start_motion():
    """Start continuous motion"""
    data = request.get_json()
    direction = data.get("direction", "forward")
    speed = data.get("speed", 0.3)
    result = get_pipeline().start_motion(direction, speed)
    return jsonify(result)

@app.route("/api/motion/stop", methods=["POST"])
def stop_motion():
    """Stop motion"""
    result = get_pipeline().stop_motion()
    return jsonify(result)

# ========== UTILITY FUNCTIONS ==========

def check_port_free(host, port, timeout=2.0):
    """Check if port is available"""
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(timeout)
    try:
        if s.connect_ex((host, port)) == 0:
            raise RuntimeError(f"Port {port} is already in use on {host}")
    finally:
        s.close()

def run_flask():
    """Run Flask server"""
    host = app.config["HOST"]
    port = app.config["PORT"]
    print(f"[ASRE] Serving from {FRONTEND_DIR}")
    app.run(host=host, port=port, use_reloader=False, threaded=True)

# ========== MAIN ==========

def main():
    """Main startup routine"""
    global server_thread

    print(f"[ASRE] BASE_DIR: {BASE_DIR}")
    print(f"[ASRE] FRONTEND_DIR: {FRONTEND_DIR}")
    print(f"[ASRE] ACTIONS_DIR: {ACTIONS_DIR}")
    print(f"[ASRE] TEMPLATES_DIR exists: {os.path.exists(TEMPLATES_DIR)}")
    print(f"[ASRE] STATIC_DIR exists: {os.path.exists(STATIC_DIR)}")
    print(f"[ASRE] home.html exists: {os.path.exists(os.path.join(TEMPLATES_DIR, 'home.html'))}")

    print(f"[ASRE] Starting Flask Core on {app.config['HOST']}:{app.config['PORT']}")
    print("[ASRE] Press CTRL+C or visit /shutdown to stop.")

    # Initialize pipeline
    print("[ASRE] Initializing ASRE Pipeline...")
    get_pipeline()

    # Check port availability
    check_port_free(app.config['HOST'], app.config['PORT'])

    # Start Flask in separate thread
    server_thread = threading.Thread(target=run_flask)
    server_thread.start()

    # Main loop
    while not shutdown_flag.is_set():
        time.sleep(0.25)

    print("[ASRE] Shutdown signal received, stopping Flask thread...")
    return

if __name__ == "__main__":
    main()