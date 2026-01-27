import pytest
import socket
import threading
import time
from unittest.mock import MagicMock

# Import your script as a module. Change name if file name differs.
import core_startup
from core_startup import app, check_port_free, shutdown_flag, main


# ---------------------------------------------------------------------
# FIXTURES
# ---------------------------------------------------------------------

@pytest.fixture(autouse=True)
def reset_shutdown_flag():
    """Reset shutdown flag between tests."""
    shutdown_flag.clear()
    yield
    shutdown_flag.clear()


@pytest.fixture
def client():
    """Flask test client."""
    app.config["TESTING"] = True
    return app.test_client()


# ---------------------------------------------------------------------
# check_port_free() TESTS
# ---------------------------------------------------------------------

def test_check_port_free_allows_free_port():
    # Pick an OS-assigned free port
    s = socket.socket()
    s.bind(("", 0))
    free_port = s.getsockname()[1]
    s.close()

    # Should not raise anything
    check_port_free("127.0.0.1", free_port, timeout=0.1)


def test_check_port_free_raises_on_used_port():
    # Reserve a port
    s = socket.socket()
    s.bind(("127.0.0.1", 0))
    used_port = s.getsockname()[1]

    # Leave socket open → port is in use
    with pytest.raises(RuntimeError):
        check_port_free("127.0.0.1", used_port, timeout=0.1)

    s.close()


# ---------------------------------------------------------------------
# ROUTES
# ---------------------------------------------------------------------

def test_index_route(client, monkeypatch):
    # Prevent actual template loading
    monkeypatch.setattr("flask.templating._render", lambda *a, **k: "HOME PAGE")

    resp = client.get("/")
    assert resp.status_code == 200
    assert resp.data == b"HOME PAGE"


def test_shutdown_route(client):
    assert not shutdown_flag.is_set()
    resp = client.post("/shutdown")
    assert resp.status_code == 200
    assert b"Server shutting down" in resp.data
    assert shutdown_flag.is_set()


# ---------------------------------------------------------------------
# run_flask() does not run real server
# ---------------------------------------------------------------------

def test_run_flask_monkeypatched(monkeypatch):
    called = {}

    def fake_run(*args, **kwargs):
        called["ran"] = True

    monkeypatch.setattr(asre_server.app, "run", fake_run)

    asre_server.run_flask()
    assert called.get("ran") is True


# ---------------------------------------------------------------------
# main() LOOP TEST
# ---------------------------------------------------------------------

def test_main_runs_and_exits(monkeypatch):
    """
    Test that main():
    - checks the port
    - starts a thread
    - loops until shutdown_flag is set
    - cleanly returns
    """

    # Fake check_port_free to skip real networking
    monkeypatch.setattr(asre_server, "check_port_free", lambda *a, **k: None)

    # Capture created thread without starting a real Flask server
    started_thread = {"thread": None}

    def fake_thread(target, *a, **k):
        # Return a dummy thread-like object
        class DummyThread:
            def start(self_inner):
                started_thread["thread"] = True
        return DummyThread()

    monkeypatch.setattr(asre_server.threading, "Thread", fake_thread)

    # Stop loop immediately
    def fake_sleep(t):
        shutdown_flag.set()

    monkeypatch.setattr(asre_server.time, "sleep", fake_sleep)

    result = asre_server.main()

    assert started_thread["thread"] is True
    assert result is None
    assert shutdown_flag.is_set()


# ---------------------------------------------------------------------
# direct check: /shutdown GET also works
# ---------------------------------------------------------------------

def test_shutdown_get(client):
    resp = client.get("/shutdown")
    assert resp.status_code == 200
    assert shutdown_flag.is_set()
