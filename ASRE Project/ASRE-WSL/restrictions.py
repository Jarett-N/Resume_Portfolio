import json
from typing import Optional, Tuple, Dict, Any


class Restrictions:
    """
    Singleton class to manage joint movement restrictions for the robot.

    Normalizes loaded JSON into a mapping:
      joint_name -> (min_value: float, max_value: float)

    Accepts JSON entries in either of these shapes:
      "Joint": {"min": -2.0, "max": 2.0}
      "Joint": [-2.0, 2.0]
      "Joint": {"range": [-2.0, 2.0]}
    """

    def __new__(cls):
        if not hasattr(cls, 'instance'):
            cls.instance = super(Restrictions, cls).__new__(cls)
            cls.instance._restriction_table = {}
        return cls.instance

    @staticmethod
    def getInstance():
        """Return the singleton instance."""
        return Restrictions.instance

    def getRestrictionTable(self) -> Dict[str, Tuple[float, float]]:
        """Return the normalized restriction dictionary."""
        return self._restriction_table

    def setRestrictionTable(self, restriction_table: Dict[str, Any]):
        """Set the restriction table (will normalize entries)."""
        self._restriction_table = {}
        self._normalize_and_set(restriction_table)

    def _to_float(self, v):
        """Try to convert value to float if possible."""
        try:
            return float(v)
        except (TypeError, ValueError):
            raise ValueError(f"Invalid numeric value: {v!r}")

    def _normalize_entry(self, entry: Any) -> Optional[Tuple[float, float]]:
        """
        Normalize a single restriction entry into (min, max) floats.
        Accepts lists/tuples/dicts with keys 'min'/'max' or 'range'.
        """
        if entry is None:
            return None

        # list/tuple: [min, max]
        if isinstance(entry, (list, tuple)) and len(entry) >= 2:
            return (self._to_float(entry[0]), self._to_float(entry[1]))

        # dict with min/max
        if isinstance(entry, dict):
            # common shapes: {"min": x, "max": y} or {"range": [x,y]}
            if 'min' in entry and 'max' in entry:
                return (self._to_float(entry['min']), self._to_float(entry['max']))
            if 'range' in entry and isinstance(entry['range'], (list, tuple)) and len(entry['range']) >= 2:
                return (self._to_float(entry['range'][0]), self._to_float(entry['range'][1]))

            # If dict directly maps to two numeric-like values (rare), attempt two values
            vals = [v for v in entry.values()]
            if len(vals) >= 2:
                try:
                    return (self._to_float(vals[0]), self._to_float(vals[1]))
                except ValueError:
                    pass

        # single numeric -> treat as (value, value)
        try:
            num = self._to_float(entry)
            return (num, num)
        except ValueError:
            return None

    def _normalize_and_set(self, table: Dict[str, Any]):
        for joint, entry in table.items():
            normalized = self._normalize_entry(entry)
            if normalized is not None:
                self._restriction_table[str(joint)] = normalized
            else:
                # skip invalid entries but warn (no printing here - keep library quiet)
                pass

    def load(self, path: str):
        """Load restrictions from a JSON file and normalize them."""
        with open(path, 'r', encoding='utf-8') as f:
            raw = json.load(f)
        if not isinstance(raw, dict):
            raise ValueError("Restrictions JSON must be an object mapping joint names to ranges.")
        self._restriction_table = {}
        self._normalize_and_set(raw)

    def save(self, path: str):
        """Save restrictions to a JSON file in normalized format (min/max)."""
        # Save as dict with min/max keys for readability
        out = {}
        for joint, (minv, maxv) in self._restriction_table.items():
            out[joint] = {"min": float(minv), "max": float(maxv)}
        with open(path, 'w', encoding='utf-8') as f:
            json.dump(out, f, indent=2)

    def getRestriction(self, joint_name: str) -> Optional[Tuple[float, float]]:
        """Return (min, max) tuple if present, else None."""
        return self._restriction_table.get(joint_name, None)

    def setRestriction(self, joint_name: str, *args):
        """
        Set restriction for a specific joint.
        Call patterns:
            setRestriction('HeadYaw', -2.0, 2.0)
            setRestriction('LHand', [0.0, 1.0])
        """
        if len(args) == 1 and isinstance(args[0], (list, tuple)) and len(args[0]) >= 2:
            self._restriction_table[joint_name] = (float(args[0][0]), float(args[0][1]))
        elif len(args) == 2:
            self._restriction_table[joint_name] = (float(args[0]), float(args[1]))
        else:
            raise ValueError("Invalid arguments for setRestriction: expected setRestriction(name, min, max) or setRestriction(name, [min,max])")
