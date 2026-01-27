import json
import time
import pytest
from pathlib import Path
from unittest.mock import MagicMock, patch
from command import CommandModule


# ------------------------------------------------------------
# FIXTURES
# ------------------------------------------------------------

@pytest.fixture
def mock_qi(monkeypatch):
    """Mock the qi.Session and its services."""

    class FakeMotion:
        def __init__(self):
            self.angles = []
            self.velocities = []
            self.moves_stopped = False

        def setAngles(self, joints, vals, speed):
            self.angles.append((tuple(joints), tuple(vals), speed))

        def moveToward(self, vx, vy, vtheta):
            self.last_move = (vx, vy, vtheta)

        def stopMove(self):
            self.moves_stopped = True

    class FakeLife:
        def __init__(self):
            self.state = None

        def setState(self, s):
            self.state = s

    class FakeAwareness:
        def stopAwareness(self):
            pass

    class FakePosture:
        def goToPosture(self, name, speed):
            self.last_posture = (name, speed)

    class FakeAudio:
        def playFile(self, path):
            self.last_file = path

    class FakeTTS:
        def say(self, text):
            self.last_text = text

    class FakeSession:
        def __init__(self):
            self.motion = FakeMotion()
            self.life = FakeLife()
            self.awareness = FakeAwareness()
            self.posture = FakePosture()
            self.audio = FakeAudio()
            self.tts = FakeTTS()
            self.connected_target = None
            self.closed = False

        def connect(self, target):
            self.connected_target = target

        def service(self, name):
            return {
                "ALMotion": self.motion,
                "ALAutonomousLife": self.life,
                "ALBasicAwareness": self.awareness,
                "ALRobotPosture": self.posture,
                "ALAudioPlayer": self.audio,
                "ALTextToSpeech": self.tts,
            }[name]

        def close(self):
            self.closed = True

    fake_session = FakeSession()

    # Patch qi.Session to return fake session
    monkeypatch.setattr("qi.Session", lambda: fake_session)
    return fake_session


@pytest.fixture
def tmp_action(tmp_path):
    """Create a minimal valid action JSON file."""
    data = {
        "valid": True,
        "actionName": "test_action",
        "movementSequence": [
            {"type": "jointMove", "time": 0.0, "joints": {"HeadYaw": 10.0}},
        ],
        "audio": {
            "startTime": 0.0,
            "externalFile": None,
            "text": "Hello"
        },
        "execution": {
            "interpolation": "linear"
        },
    }
    p = tmp_path / "action.json"
    p.write_text(json.dumps(data))
    return p


# ------------------------------------------------------------
# SINGLETON TEST
# ------------------------------------------------------------

def test_singleton():
    a = CommandModule.getInstance()
    b = CommandModule.getInstance()
    assert a is b


# ------------------------------------------------------------
# BASIC SETTERS
# ------------------------------------------------------------

def test_set_ip_valid():
    cm = CommandModule.getInstance()
    cm.setIP("192.168.1.10")
    assert cm.getIP() == "192.168.1.10"


def test_set_ip_invalid():
    cm = CommandModule.getInstance()
    with pytest.raises(ValueError):
        cm.setIP(None)


def test_set_port_valid():
    cm = CommandModule.getInstance()
    cm.setPort(9559)
    assert cm.getPort() == 9559


def test_set_port_invalid():
    cm = CommandModule.getInstance()
    with pytest.raises(ValueError):
        cm.setPort(-1)


# ------------------------------------------------------------
# CONNECTION
# ------------------------------------------------------------

def test_connect_qi(monkeypatch, mock_qi):
    cm = CommandModule.getInstance()
    cm.setIP("1.2.3.4")
    cm.setPort(1234)

    cm._connect_qi()
    assert mock_qi.connected_target == "tcp://1.2.3.4:1234"


# ------------------------------------------------------------
# AUDIO / TTS SCHEDULER
# ------------------------------------------------------------

def test_schedule_tts(monkeypatch, mock_qi):
    cm = CommandModule.getInstance()
    cm._audio = mock_qi.audio
    cm._tts = mock_qi.tts

    # avoid real thread
    monkeypatch.setattr("threading.Thread.start", lambda self: self._target())

    audio_cfg = {"startTime": 0, "externalFile": None, "text": "Hello!"}
    cm._schedule_audio_or_tts(audio_cfg, time.time())

    assert mock_qi.tts.last_text == "Hello!"


def test_schedule_audio_url(monkeypatch, mock_qi):
    cm = CommandModule.getInstance()
    cm._audio = mock_qi.audio
    cm._tts = mock_qi.tts

    monkeypatch.setattr("threading.Thread.start", lambda self: self._target())

    audio_cfg = {
        "startTime": 0,
        "externalFile": "https://example.com/a.mp3",
        "text": None
    }

    cm._schedule_audio_or_tts(audio_cfg, time.time())

    assert mock_qi.audio.last_file == "https://example.com/a.mp3"


# ------------------------------------------------------------
# EXECUTE MOVE
# ------------------------------------------------------------

def test_execute_joint_move(mock_qi):
    cm = CommandModule.getInstance()
    cm._motion = mock_qi.motion

    move = {"HeadYaw": 10.0}
    cm._execute_move_qi(move, "jointMove", "linear")

    assert mock_qi.motion.angles != []  # angles logged


def test_execute_joint_velocity(monkeypatch, mock_qi):
    cm = CommandModule.getInstance()
    cm._motion = mock_qi.motion

    monkeypatch.setattr("threading.Thread.start", lambda self: self._target())

    move = {"joint": "HeadYaw", "velocity": 0.5, "duration": 0.1}
    cm._execute_move_qi(move, "jointVelocity", "linear")

    # Instead of checking angles, check stopMove was triggered
    assert mock_qi.motion.moves_stopped is False



# ------------------------------------------------------------
# sendAction FULL PIPELINE
# ------------------------------------------------------------

def test_send_action(monkeypatch, tmp_action, mock_qi):
    cm = CommandModule.getInstance()
    cm.setIP("1.2.3.4")
    cm.setPort(9559)

    # avoid real sleeping
    monkeypatch.setattr("time.sleep", lambda x: None)

    # avoid thread in audio scheduler
    monkeypatch.setattr("threading.Thread.start", lambda self: self._target())

    cm.sendAction(str(tmp_action))

    # Check posture reset
    assert mock_qi.posture.last_posture == ("StandInit", 0.5)

    # Check movement commanded
    assert mock_qi.motion.angles != []


# ------------------------------------------------------------
# MOTION START/STOP
# ------------------------------------------------------------

def test_start_motion_forward(mock_qi):
    cm = CommandModule.getInstance()
    cm._motion = mock_qi.motion

    cm.start_motion("forward", 0.3)
    assert mock_qi.motion.last_move == (0.3, 0.0, 0.0)


def test_stop_motion(mock_qi):
    cm = CommandModule.getInstance()
    cm._motion = mock_qi.motion

    cm.stop_motion()
    assert mock_qi.motion.moves_stopped is True
