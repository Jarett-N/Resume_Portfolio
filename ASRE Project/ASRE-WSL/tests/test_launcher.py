import json
import pytest
import subprocess
from unittest.mock import MagicMock, patch
from pathlib import Path

import start_asre_launcher as launcher # rename to your actual file name


# ======================================================================
# run()
# ======================================================================

def test_run_invokes_subprocess(monkeypatch):
    calls = []

    def fake_run(cmd, check, text, capture_output, shell):
        calls.append(cmd)
        return MagicMock()

    monkeypatch.setattr(subprocess, "run", fake_run)

    launcher.run(["echo", "hello"])
    assert calls[0] == ["echo", "hello"]


# ======================================================================
# get_wsl_ip()
# ======================================================================

def test_get_wsl_ip_success(monkeypatch):
    monkeypatch.setattr(
        subprocess, "check_output",
        lambda args, text: "172.20.64.1\n"
    )

    assert launcher.get_wsl_ip("Ubuntu") == "172.20.64.1"


def test_get_wsl_ip_failure(monkeypatch):
    def fake_fail(*a, **k):
        raise subprocess.CalledProcessError(1, "cmd")

    monkeypatch.setattr(subprocess, "check_output", fake_fail)
    assert launcher.get_wsl_ip("Ubuntu") is None


# ======================================================================
# load_or_create_config()
# ======================================================================

def test_load_config_existing(tmp_path, monkeypatch):
    cfg_path = tmp_path / "config.json"
    cfg = {"PROJECT_PATH": "/test", "VENV_PATH": "env"}
    cfg_path.write_text(json.dumps(cfg))

    project, venv = launcher.load_or_create_config(str(cfg_path))
    assert project == "/test"
    assert venv == "env"


def test_load_config_create(tmp_path, monkeypatch):
    cfg_path = tmp_path / "missing.json"

    # Simulate user input
    monkeypatch.setattr("builtins.input", lambda prompt: {
        "Enter full WSL project path (e.g., /home/otjale/ASRE): ": "/myproj",
        "Enter virtual environment folder name (e.g., my-venv): ": "venvA"
    }[prompt])

    project, venv = launcher.load_or_create_config(str(cfg_path))
    saved = json.loads(cfg_path.read_text())

    assert project == "/myproj"
    assert venv == "venvA"
    assert saved["PROJECT_PATH"] == "/myproj"
    assert saved["VENV_PATH"] == "venvA"


# ======================================================================
# firewall_setup()
# ======================================================================

def test_firewall_setup_success(monkeypatch):
    # Track calls
    calls = {"run": 0, "get_ip": 0}

    monkeypatch.setattr(subprocess, "run", lambda *a, **k: calls.__setitem__("run", calls["run"] + 1))
    monkeypatch.setattr(launcher, "get_wsl_ip", lambda *a, **k: ("172.20.32.1" if not calls.__setitem__("get_ip", calls["get_ip"] + 1) else None))

    launcher.firewall_setup(5000)

    assert calls["run"] >= 2    # firewall add + portproxy add
    assert calls["get_ip"] >= 1


def test_firewall_setup_exception(monkeypatch):
    monkeypatch.setattr(subprocess, "run", lambda *a, **k: (_ for _ in ()).throw(Exception("boom")))
    launcher.firewall_setup(5000)  # Should not raise


# ======================================================================
# verify_venv()
# ======================================================================

def test_verify_venv_found(monkeypatch):
    monkeypatch.setattr(
        subprocess, "check_output",
        lambda *a, **k: "/home/user/venv/bin/python3\n"
    )

    assert launcher.verify_venv("/proj", "venv", "Ubuntu") is True


def test_verify_venv_no_python(monkeypatch):
    monkeypatch.setattr(
        subprocess, "check_output",
        lambda *a, **k: "NO_VENV_PYTHON"
    )
    assert launcher.verify_venv("/proj", "venv", "Ubuntu") is None


def test_verify_venv_failure(monkeypatch):
    def fail(*a, **k):
        raise subprocess.CalledProcessError(1, "cmd")

    monkeypatch.setattr(subprocess, "check_output", fail)
    assert launcher.verify_venv("/proj", "venv", "Ubuntu") is False


# ======================================================================
# launch_file_wsl()
# ======================================================================

def test_launch_file_wsl(monkeypatch):
    captured = {}

    class FakePopen:
        def __init__(self, cmd, stdout, stderr):
            captured["cmd"] = cmd

    monkeypatch.setattr(subprocess, "Popen", FakePopen)

    launcher.launch_file_wsl("/proj", "venv", "Ubuntu", "file.py")
    assert captured["cmd"][0:3] == ["wsl", "-d", "Ubuntu"]


# ======================================================================
# wait_for_process()
# ======================================================================

def test_wait_for_process_normal_exit(monkeypatch):
    class FakeProc:
        def __init__(self):
            self.counter = 0

        def poll(self):
            self.counter += 1
            return 0 if self.counter > 1 else None

    monkeypatch.setattr(launcher.time, "sleep", lambda x: None)

    p = FakeProc()
    assert launcher.wait_for_process(p, "Ubuntu", "file.py") is None


def test_wait_for_process_keyboardinterrupt(monkeypatch):
    class FakeProc:
        def poll(self):
            raise KeyboardInterrupt()

    called = {"run": 0}

    def fake_run(*a, **k):
        called["run"] += 1

    monkeypatch.setattr(subprocess, "run", fake_run)

    launcher.wait_for_process(FakeProc(), "Ubuntu", "file.py")
    assert called["run"] == 1


# ======================================================================
# main() integration (fully mocked)
# ======================================================================

def test_main(monkeypatch):
    # Mock all components of main()
    monkeypatch.setattr(launcher, "load_or_create_config", lambda *a: ("/proj", "env"))
    monkeypatch.setattr(launcher, "firewall_setup", lambda *a: None)
    monkeypatch.setattr(launcher, "verify_venv", lambda *a: True)

    fake_proc = MagicMock()
    monkeypatch.setattr(launcher, "launch_file_wsl", lambda *a: fake_proc)
    monkeypatch.setattr(launcher, "wait_for_process", lambda *a: None)
    monkeypatch.setattr(launcher, "firewall_shutdown", lambda *a: None)
    monkeypatch.setattr(subprocess, "run", lambda *a, **k: None)

    # Ensure no WSL termination
    monkeypatch.setattr(launcher, "subprocess", subprocess)

    launcher.main()  # Should not raise
