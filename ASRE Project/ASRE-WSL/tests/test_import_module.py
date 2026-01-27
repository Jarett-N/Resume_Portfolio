import json
import zipfile
from pathlib import Path
import pytest
from xar_converter import XARConverter, xar_to_json, xar_to_json_file, process_zip, process


# -------------------------------------------------------------------
# Helpers
# -------------------------------------------------------------------

def minimal_xar_xml():
    """Return a small valid XAR containing a root Box and timeline."""
    return """
<Project>
  <Box name="root" tooltip="ID: TEST_01
Tags:
- wave">
    <bitmap>move_icon.png</bitmap>
    <Timeline fps="25">
      <ActuatorCurve actuator="HeadYaw">
        <Key frame="0" value="0.0"/>
        <Key frame="25" value="10.0"/>
      </ActuatorCurve>
      <ActuatorCurve actuator="HeadPitch">
        <Key frame="10" value="1.5"/>
      </ActuatorCurve>
    </Timeline>
    <Resource type="audio">sound.wav</Resource>
  </Box>
</Project>
"""


def minimal_xar_without_root():
    """Used to test fallback Box detection."""
    return """
<Project>
  <Box name="OtherBox">
    <Timeline fps="25">
      <ActuatorCurve actuator="HeadYaw">
        <Key frame="0" value="0.0"/>
      </ActuatorCurve>
    </Timeline>
  </Box>
</Project>
"""


# -------------------------------------------------------------------
# BASIC PARSE TEST
# -------------------------------------------------------------------

def test_parse_xar_basic():
    conv = XARConverter(start_id=100)
    data = conv.parse_xar(minimal_xar_xml())

    assert data["id"] == 100
    assert data["actionName"] == "TEST_01"
    assert data["label"] == ["wave"]
    assert data["category"] == "wave"
    assert data["icon"] == "👋"  # wave tag
    assert data["audio"]["externalFile"] == "sound.wav"

    # Timeline → 3 movement events (0, 10, 25)
    seq = data["movementSequence"]
    assert len(seq) == 3
    assert seq[0]["time"] == 0.0
    assert seq[1]["time"] == 0.4   # 10 / 25 fps
    assert seq[2]["time"] == 1.0   # 25 / 25 fps

    assert data["totalTime"] == 1.0


# -------------------------------------------------------------------
# ICON FROM BITMAP FALLBACK
# -------------------------------------------------------------------

def test_parse_xar_bitmap_icon():
    # wave tag → 👋 but remove tags so icon is based on bitmap
    xml = """
    <Project>
      <Box name="root" tooltip="">
        <bitmap>turn_icon.png</bitmap>
      </Box>
    </Project>
    """
    conv = XARConverter()
    data = conv.parse_xar(xml)

    assert data["icon"] == "↪️"  # matched "turn"


# -------------------------------------------------------------------
# NO ROOT BOX → FALLBACK
# -------------------------------------------------------------------

def test_parse_xar_no_root():
    conv = XARConverter()
    data = conv.parse_xar(minimal_xar_without_root())

    # Should parse the non-root Box
    assert data["movementSequence"][0]["time"] == 0.0
    assert data["totalTime"] == 0.0


# -------------------------------------------------------------------
# JSON FILE CREATION
# -------------------------------------------------------------------

def test_xar_to_json(tmp_path):
    p = tmp_path / "test.xar"
    p.write_text(minimal_xar_xml())

    json_str = xar_to_json(str(p), start_id=50)
    data = json.loads(json_str)

    assert data["id"] == 50
    assert data["label"] == ["wave"]


def test_xar_to_json_file(tmp_path):
    p = tmp_path / "test.xar"
    p.write_text(minimal_xar_xml())

    out = xar_to_json_file(str(p), start_id=40)
    assert out.exists()

    data = json.loads(out.read_text())
    assert data["id"] == 40


# -------------------------------------------------------------------
# ZIP ARCHIVE PROCESSING
# -------------------------------------------------------------------

def test_process_zip(tmp_path):
    xar1 = minimal_xar_xml()
    xar2 = minimal_xar_without_root()

    zip_path = tmp_path / "actions.zip"
    with zipfile.ZipFile(zip_path, "w") as zf:
        zf.writestr("a.xar", xar1)
        zf.writestr("b.xar", xar2)

    out_dir = tmp_path / "out"
    created = process_zip(zip_path, out_dir, start_id=1)

    assert len(created) == 2
    for c in created:
        assert c.exists()
        d = json.loads(c.read_text())
        assert "id" in d
        assert "actionName" in d


# -------------------------------------------------------------------
# PROCESS FUNCTION DISPATCH
# -------------------------------------------------------------------

def test_process_xar_dispatch(tmp_path):
    p = tmp_path / "test.xar"
    p.write_text(minimal_xar_xml())

    result = process(p)
    assert isinstance(result, Path)
    assert result.exists()


def test_process_zip_dispatch(tmp_path):
    zip_path = tmp_path / "test.zip"
    with zipfile.ZipFile(zip_path, "w") as zf:
        zf.writestr("test.xar", minimal_xar_xml())

    results = process(zip_path)
    assert isinstance(results, list)
    assert results[0].exists()


def test_process_unsupported(tmp_path):
    bad = tmp_path / "file.txt"
    bad.write_text("hello")

    with pytest.raises(ValueError):
        process(bad)


# -------------------------------------------------------------------
# START ID INCREMENTING
# -------------------------------------------------------------------

def test_start_id_increments():
    conv = XARConverter(start_id=10)
    d1 = conv.parse_xar(minimal_xar_xml())
    d2 = conv.parse_xar(minimal_xar_xml())

    assert d1["id"] == 10
    assert d2["id"] == 11


# -------------------------------------------------------------------
# EMPTY PROJECT RETURNS {}
# -------------------------------------------------------------------

def test_parse_empty_returns_empty():
    conv = XARConverter()
    assert conv.parse_xar("<Project></Project>") == {}
