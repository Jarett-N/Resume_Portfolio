print("Starting server...")

from flask import Flask, render_template, request, jsonify
import os
import json
from pathlib import Path
from werkzeug.utils import secure_filename
from xar_converter import XARConverter

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = 'uploads'
app.config['ACTIONS_FOLDER'] = 'actions'
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB max file size
app.config['ALLOWED_EXTENSIONS'] = {'xar', 'zip'}

# Create necessary folders
os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)
os.makedirs(app.config['ACTIONS_FOLDER'], exist_ok=True)

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in app.config['ALLOWED_EXTENSIONS']

def get_next_action_id():
    """Get the next available action ID by checking existing actions"""
    actions = load_actions()
    if actions:
        max_id = max(int(a.get('id', 0)) for a in actions)
        return max_id + 1
    return 1

def load_actions():
    """Load all action JSON files from the actions folder"""
    actions = []
    actions_path = Path(app.config['ACTIONS_FOLDER'])
    
    for json_file in actions_path.glob('**/*.json'):
        try:
            with open(json_file, 'r', encoding='utf-8') as f:
                action_data = json.load(f)
                # Ensure the action has required fields
                if 'actionName' in action_data:
                    # Normalize to match frontend expectations
                    normalized = {
                        'id': str(action_data.get('id', '')),
                        'name': action_data.get('actionName', ''),
                        'label': action_data.get('label', [action_data.get('actionName', '')])[0] if isinstance(action_data.get('label'), list) else action_data.get('label', ''),
                        'category': action_data.get('category', ''),
                        'color': action_data.get('color', '#4f46e5'),
                        'icon': action_data.get('icon', '🤖'),
                        'movementSequence': action_data.get('movementSequence', []),
                        'totalTime': action_data.get('totalTime', 0.0),
                        'execution': action_data.get('execution', {}),
                        'audio': action_data.get('audio', {}),
                        'visible': True
                    }
                    actions.append(normalized)
        except (json.JSONDecodeError, Exception) as e:
            print(f"Error loading {json_file}: {e}")
    
    return actions

# ========== Routes ==========

@app.route('/')
def home():
    return render_template('home.html')

@app.route('/api/actions')
def get_actions():
    actions = load_actions()
    return jsonify(actions)

@app.route('/api/actions/<action_id>')
def get_action(action_id):
    actions = load_actions()
    action = next((a for a in actions if str(a['id']) == str(action_id)), None)
    
    if action:
        return jsonify(action)
    else:
        return jsonify({"error": "Action not found"}), 404

@app.route('/api/actions/<action_id>/run', methods=['POST'])
def run_action(action_id):
    actions = load_actions()
    action = next((a for a in actions if str(a['id']) == str(action_id)), None)
    
    if action:
        print(f"[RUN] Action {action_id}: {action.get('name', 'Unknown')}")
        print(f"      Total Time: {action.get('totalTime', 0)} seconds")
        print(f"      Movement Sequence: {len(action.get('movementSequence', []))} movements")
        
        # TODO: Replace with actual NAOqi code
        # Example: Execute each movement in the sequence
        # for movement in action['movementSequence']:
        #     time = movement['time']
        #     joints = movement['joints']
        #     for joint_name, value in joints.items():
        #         motion_proxy.setAngles(joint_name, value, movement.get('velocity', 0.5))
        
        return jsonify({
            "success": True, 
            "message": f"Action {action_id} executed",
            "action": action
        })
    else:
        return jsonify({"error": "Action not found"}), 404

@app.route('/upload_xar', methods=['POST'])
def upload_xar():
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400
    
    file = request.files['file']
    
    if file.filename == '':
        return jsonify({"error": "No file selected"}), 400
    
    if not allowed_file(file.filename):
        return jsonify({"error": "Invalid file type. Only .xar and .zip files allowed"}), 400
    
    try:
        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)
        
        # Get next ID for unique identification
        start_id = get_next_action_id()
        converter = XARConverter(start_id=start_id)
        
        converted_files = []
        
        # Process the file
        if filename.endswith('.zip'):
            json_files = converter.process_zip(filepath, app.config['ACTIONS_FOLDER'])
            converted_files = [str(f) for f in json_files]
        else:
            # Get action data first to create proper filename
            with open(filepath, 'r', encoding='utf-8') as f:
                xar_content = f.read()
            action_data = converter.parse_xar(xar_content)
            
            # Create filename from actionName and ID
            action_name = action_data.get('actionName', 'action')
            action_id = action_data.get('id', start_id)
            output_filename = f"{action_name}_{action_id}.json"
            output_path = Path(app.config['ACTIONS_FOLDER']) / output_filename
            
            with open(output_path, 'w', encoding='utf-8') as f:
                json.dump(action_data, f, indent=2)
            
            converted_files = [str(output_path)]
        
        # Clean up uploaded file
        os.remove(filepath)
        
        # Load and return the converted actions
        actions = load_actions()
        
        return jsonify({
            "success": True,
            "message": f"Converted {len(converted_files)} file(s)",
            "files": converted_files,
            "actions": actions
        })
        
    except Exception as e:
        import traceback
        traceback.print_exc()
        print(f"ERROR TYPE: {type(e).__name__}")
        print(f"ERROR MESSAGE: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/movement', methods=['POST'])
def movement():
    direction = request.json.get('direction')
    
    if not direction:
        return jsonify({"error": "No direction provided"}), 400
    
    print(f"[MOVEMENT] Direction: {direction}")
    
    # TODO: Replace with NAOqi motion commands
    # Examples:
    # motion_proxy.moveToward(1, 0, 0)  # forward
    # motion_proxy.moveToward(-1, 0, 0) # backward
    # motion_proxy.moveToward(0, 1, 0)  # left
    # motion_proxy.moveToward(0, -1, 0) # right
    # motion_proxy.stopMove()           # stop
    
    return jsonify({
        "success": True,
        "message": f"Moving {direction}"
    })

@app.route('/run_custom', methods=['POST'])
def run_custom():
    try:
        custom_action = request.json
        action_name = custom_action.get('name', 'custom_action')
        
        print(f"[CUSTOM] Running: {action_name}")
        print(f"         Data: {json.dumps(custom_action, indent=2)}")
        
        # TODO: Execute custom action with NAOqi
        
        return jsonify({
            "success": True,
            "message": f"Custom action '{action_name}' executed"
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
