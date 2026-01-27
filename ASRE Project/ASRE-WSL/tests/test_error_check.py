import pytest

class DummyErrorChecker:
    # Simulated Error Check for motion safety.
    def validate(self, motion):
        if motion.get("joint_angle", 0) > 180:
            raise ValueError("Hyperextension detected")
        return True

def test_safe_motion_passes():
    checker = DummyErrorChecker()
    assert checker.validate({"joint_angle": 90}) is True

def test_hyperextension_fails():
    checker = DummyErrorChecker()
    with pytest.raises(ValueError):
        checker.validate({"joint_angle": 200})
