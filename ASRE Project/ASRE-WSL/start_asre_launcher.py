"""
ASRE Launcher (Python + config-based paths)
-------------------------------------------
Reads project and venv paths from an external JSON config file.
If the config file does not exist, prompts the user for values,
writes them to disk, then launches the core program inside WSL
using the specified virtual environment.
"""

import subprocess # For running system commands
import time
import sys
import os
import json

# Run a system command
def run(cmd, check=False, capture=False):
    print(f"[ASRE] > {' '.join(cmd)}")
    return subprocess.run(cmd, check=check, text=True,
                          capture_output=capture, shell=False)

# Return current WSL IP address.
def get_wsl_ip(distro):
    try:
        result = subprocess.check_output(
            ["wsl", "-d", distro, "--", "bash", "-lc", "hostname -I | awk '{print $1}'"],
            text=True
        ).strip()
        return result if result else None
    except subprocess.CalledProcessError:
        return None

# Return the Windows host LAN IP.
def get_wifi_ipv4():
    try:
        output = subprocess.check_output(
            ["ipconfig"], 
            text=True, 
            encoding="utf-8", 
            errors="ignore"
        )
    except Exception:
        return None

    lines = output.splitlines()

    inside_wifi_block = False

    for line in lines:
        # Detect start of Wi-Fi block (covers Unicode dash variants)
        if "Wireless LAN adapter Wi" in line:
            inside_wifi_block = True
            continue

        # If we were inside Wi-Fi block and now see a new adapter header → exit
        if inside_wifi_block and ":" in line and not line.lstrip().startswith("IPv4") and "adapter" in line:
            inside_wifi_block = False

        # Extract IPv4 line ONLY while inside Wi-Fi block
        if inside_wifi_block and "IPv4 Address" in line:
            # Split after colon and trim whitespace
            return line.split(":")[-1].strip()

    return None


# Load config file or prompt for values and create one.
def load_or_create_config(config_file):
    if os.path.exists(config_file):
        with open(config_file, "r") as f:
            cfg = json.load(f)
        print(f"[ASRE] Loaded config file: {config_file}")
        return cfg.get("PROJECT_PATH"), cfg.get("VENV_PATH")
    
    else:
        print("[ASRE] Configuration file not found.")
        project_path = input("Enter full WSL project path (e.g., /home/otjale/ASRE): ").strip()
        venv_path = input("Enter virtual environment folder name (e.g., my-venv): ").strip()

        cfg = {"PROJECT_PATH": project_path, "VENV_PATH": venv_path}
        
        with open(config_file, "w") as f:
            json.dump(cfg, f, indent=4)
        print(f"[ASRE] Saved configuration to {config_file}")
        return project_path, venv_path

# Sets up a proxy port between the WSL and the main system
def firewall_setup(port, distro="Ubuntu"):
    print(f"[ASRE] Configuring firewall and portproxy for {port}...")
    
    try:
        device_ip = get_wifi_ipv4()
        print(f"[ASRE] Device LAN IP (Wi-Fi): {device_ip}")

        
        # Add firewall rule
        subprocess.run(
            ["netsh", "advfirewall", "firewall", "add", "rule",
                f"name=ASRE_{port}", "dir=in", "action=allow",
                "protocol=TCP", f"localport={port}"],
            capture_output=True
        )

        ip = None
        
        # Try to get WSL IP address 10 times
        for _ in range(10):
            ip = get_wsl_ip(distro)
            if ip:
                break
            time.sleep(2)
            
        # Set up port proxy if we got an IP
        if ip:
            print(f"[ASRE] WSL IP: {ip}")
            subprocess.run(
                ["netsh", "interface", "portproxy", "add", "v4tov4",
                    f"listenport={port}", "listenaddress=0.0.0.0",
                    f"connectport={port}", f"connectaddress={ip}"],
                capture_output=True
            )
        
        # Warn if we couldn't get an IP
        else:
            print("[ASRE] Could not determine WSL IP. Continuing without proxy.")
    except Exception as e:
        print(f"[ASRE] Warning: firewall setup failed ({e})")
        
    return None

def firewall_shutdown(port):
    subprocess.run(
        ["netsh", "interface", "portproxy", "delete", "v4tov4",
        f"listenport={port}", "listenaddress=0.0.0.0"],
        capture_output=True
    )
    subprocess.run(
        ["netsh", "advfirewall", "firewall", "delete", "rule",
        f"name=ASRE_{port}"],
        capture_output=True
    )
            
# Verify that the virtual environment exists and can be 
def verify_venv(p_path, v_path, distro):
    print("[ASRE] Verifying virtual environment inside WSL...")

    # Bash command to check if venv python exists
    check_venv_cmd = (
        f"cd '{p_path}' && "
        f"if [ -x '{v_path}/bin/python3' ]; then "
        f"  '{v_path}/bin/python3' -c \"import sys; print(sys.executable)\"; "
        f"else "
        f"  echo 'NO_VENV_PYTHON'; "
        f"fi"
    )

    try:
        # Run the check command inside WSL and store echoed output
        result = subprocess.check_output(
            ["wsl", "-d", distro, "--", "bash", "-lc", check_venv_cmd],
            text=True
        ).strip()

        if "NO_VENV_PYTHON" in result:
            print(f"[ASRE] Virtual environment Python not found at {v_path}/bin/python3")
            return
        elif result.startswith("/usr"):
            print(f"[ASRE] Using system Python: {result}")
        else:
            print(f"[ASRE] Virtual environment active: {result}")
            
    except subprocess.CalledProcessError as e:
        print("[ASRE] Could not verify virtual environment:", e)
        return False
    
    return True

# Launches the specified core_file within the WSL
def launch_file_wsl(p_path, v_path, distro, file_to_launch):
    print("[ASRE] Launching Flask Core...")

    bash_command = (
        f"cd '{p_path}' && "
        f"'{v_path}/bin/python3' '{file_to_launch}'"
    )

    cmd = ["wsl", "-d", distro, "--", "bash", "-lc", bash_command]
    proc = subprocess.Popen(cmd, stdout=sys.stdout, stderr=sys.stderr)

    return proc

# Waits for the launched process to complete, handling Ctrl+C
def wait_for_process(proc, distro, file_to_launch):
    print("[ASRE] Press Ctrl+C to stop.")
    try:
        while proc.poll() is None:
            time.sleep(1)
    except KeyboardInterrupt: # Grab a ctrl-C to stop process in WSL instead
        print("\n[ASRE] Ctrl+C received, stopping Flask...")
        subprocess.run(
            ["wsl", "-d", distro, "--", "bash", "-lc", f"pkill -f {file_to_launch}"],
            capture_output=True
        )
    
    return None

def main():
    DISTRO = "Ubuntu"
    CONFIG_FILE = "asre_config.json"
    CORE_FILE = "app.py"
    PORT = 5000
    EXPOSE_EXTERNALLY = True
    TERMINATE_WSL_ON_EXIT = False
    
    print("=== ASRE Launcher ===")

    # Load or create a config file
    project_path, venv_path = load_or_create_config(CONFIG_FILE)

    # Conditional firewall setup
    if EXPOSE_EXTERNALLY:
        firewall_setup(PORT, DISTRO)

    # venv verification
    if verify_venv(project_path, venv_path, DISTRO):

        # Launch main program
        proc = launch_file_wsl(project_path, venv_path, DISTRO, CORE_FILE)        

        # Wait for process to complete
        wait_for_process(proc, DISTRO, CORE_FILE)

        print("[ASRE] Cleaning up...")
        
        # Conditional firewall shutdown
        if EXPOSE_EXTERNALLY:
            firewall_shutdown(PORT)

        # Conditional WSL shutdown
        if TERMINATE_WSL_ON_EXIT:
            print("[ASRE] Terminating WSL distro as requested...")
            subprocess.run(["wsl", "--terminate", DISTRO], capture_output=True)
        else:
            print("[ASRE] Leaving WSL running (TERMINATE_WSL_ON_EXIT=False).")

        print("[ASRE] Done.")

if __name__ == "__main__":
    main()
    