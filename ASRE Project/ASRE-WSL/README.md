<<<<<<< HEAD
# ASRE
CS 497 Capstone project working with the NAO robot.

## Overview
`start_asre_launcher.py` launches the ASRE Flask Core inside WSL using a Python virtual environment.  
It reads project settings from an external config file, verifies the venv, optionally opens a Windows firewall rule and port proxy, and runs until you stop it.

---

## 1. Initial Setup

```bash
# In WSL
cd ~/ASRE
python3 -m venv "YOUR_VENV_NAME"
source YOUR_VENV_NAME/bin/activate
pip install -r requirements.txt
```

## 2. Configuration
The launcher looks for `asre_config.json` in the same directory as `start_asre_launcher.py`.
If it does not exist, the launcher will prompt for values and create it.

Example `asre_config.json`:
```json
{
  "PROJECT_PATH": "/home/otjale/ASRE",
  "VENV_PATH": "my-venv"
}
```
To change paths later, modify `asre-config.json`, or delete it and reopen `start_asre_launcher.py`

## 3. Running the Launcher from Windows

In PowerShell or Command Prompt, run:
```nginx
python start_asre_launcher.py
```
What it does:

1. Loads or creates `asre_config.json`.
2. Verifies the virtual environment’s Python at `<VENV_PATH>/bin/python3`.
3. Launches the Flask Core inside WSL.
4. If enabled, opens a firewall rule and port proxy for port 5000.
5. Streams logs until you press Ctrl+C.

## 4. Shutdown Behavior

By default, WSL stays running after Flask stops.
To terminate the WSL distro automatically, set in the script:
```python
TERMINATE_WSL_ON_EXIT = True
```
