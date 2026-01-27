import json
import pytest
from restrictions import Restrictions


# ------------------------------------------------------------
# FIXTURE - reset singleton before each test
# ------------------------------------------------------------
@pytest.fixture(autouse=True)
def reset_singleton():
    # The class stores .instance, so reset it cleanly
    Restrictions.instance = Restrictions()
    Restrictions.instance._restriction_table = {}
    yield


# ------------------------------------------------------------
# SINGLETON BEHAVIOR
# ------------------------------------------------------------

def test_singleton_identity():
    a = Restrictions.getInstance()
    b = Restrictions.getInstance()
    assert a is b


# ------------------------------------------------------------
# NORMALIZATION TESTS
# ------------------------------------------------------------

def test_normalize_list():
    r = Restrictions.getInstance()
    r.setRestrictionTable({"HeadYaw": [-2.5, 2.5]})
    assert r.getRestriction("HeadYaw") == (-2.5, 2.5)


def test_normalize_min_max_dict():
    r = Restrictions.getInstance()
    r.setRestrictionTable({"HeadPitch": {"min": -1.0, "max": 1.0}})
    assert r.getRestriction("HeadPitch") == (-1.0, 1.0)


def test_normalize_range_dict():
    r = Restrictions.getInstance()
    r.setRestrictionTable({"RElbow": {"range": [-0.2, 0.8]}})
    assert r.getRestriction("RElbow") == (-0.2, 0.8)


def test_normalize_dict_two_numeric_values():
    r = Restrictions.getInstance()
    r.setRestrictionTable({"Test": {"a": -3, "b": 5}})
    assert r.getRestriction("Test") == (-3.0, 5.0)


def test_normalize_single_numeric():
    r = Restrictions.getInstance()
    r.setRestrictionTable({"Single": 1.2})
    assert r.getRestriction("Single") == (1.2, 1.2)


def test_invalid_entries_raise_valueerror():
    r = Restrictions.getInstance()

    bad_table = {
        "Good": [-1, 1],
        "Bad": {"min": "A", "max": "B"},       # invalid numeric -> should raise
        "AlsoBad": {"range": ["no", "yep"]},   # also invalid
    }

    with pytest.raises(ValueError):
        r.setRestrictionTable(bad_table)

    # Good entry should still be set before failure occurs
    assert r.getRestriction("Good") == (-1.0, 1.0)



# ------------------------------------------------------------
# GET / SET RESTRICTION
# ------------------------------------------------------------

def test_setRestriction_two_args():
    r = Restrictions.getInstance()
    r.setRestriction("HeadYaw", -2.0, 2.0)
    assert r.getRestriction("HeadYaw") == (-2.0, 2.0)


def test_setRestriction_list_arg():
    r = Restrictions.getInstance()
    r.setRestriction("LHand", [0.0, 1.0])
    assert r.getRestriction("LHand") == (0.0, 1.0)


def test_setRestriction_invalid():
    r = Restrictions.getInstance()
    with pytest.raises(ValueError):
        r.setRestriction("Head", [1])  # too few values


# ------------------------------------------------------------
# LOAD / SAVE
# ------------------------------------------------------------

def test_save_and_load(tmp_path):
    r = Restrictions.getInstance()

    # Set some restrictions
    r.setRestrictionTable({
        "A": [-1, 1],
        "B": {"min": 0, "max": 5}
    })

    # Save
    file_path = tmp_path / "restrictions.json"
    r.save(str(file_path))

    # Reset and load fresh
    Restrictions.instance = Restrictions()
    Restrictions.instance._restriction_table = {}
    r2 = Restrictions.getInstance()

    r2.load(str(file_path))

    assert r2.getRestriction("A") == (-1.0, 1.0)
    assert r2.getRestriction("B") == (0.0, 5.0)


def test_load_invalid_json_shape(tmp_path):
    # Save a non-dict json (invalid)
    p = tmp_path / "bad.json"
    p.write_text(json.dumps([1, 2, 3]))

    r = Restrictions.getInstance()
    with pytest.raises(ValueError):
        r.load(str(p))
