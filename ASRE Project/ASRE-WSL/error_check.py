"""
Error Check Module
Validates action JSONs against joint movement restrictions
Ensures robot safety by checking if movements are within allowed ranges
"""

import json
from pathlib import Path
from typing import Dict, List, Tuple, Optional, Any
from restrictions import Restrictions

print("=== Error Check Module Running ===")


class ErrorChecker:
    """
    Validates action movements against joint restrictions
    Flags actions as safe (valid=True) or unsafe (valid=False)
    """

    def __init__(self, restrictions_path: Optional[str] = None):
        """
        Initialize error checker with restrictions

        Args:
            restrictions_path: Path to restrictions JSON file
        """
        self.restrictions = Restrictions()
        if restrictions_path:
            self.restrictions.load(restrictions_path)

    def _num(self, v):
        """Safely cast a value to float when possible (leave unchanged if not numeric)."""
        try:
            return float(v)
        except (TypeError, ValueError):
            return v

    def _convert_numbers_in_joints(self, joints: Dict[str, Any]) -> Dict[str, Any]:
        """Convert joint values (which may be strings) to floats where possible."""
        converted = {}
        for j, val in joints.items():
            if isinstance(val, (int, float)):
                converted[j] = float(val)
            else:
                try:
                    converted[j] = float(val)
                except (TypeError, ValueError):
                    # keep original if it isn't numeric
                    converted[j] = val
        return converted

    def _normalize_restriction(self, restriction: Any) -> Optional[Tuple[float, float]]:
        """
        Accepts restriction in many shapes and returns (min, max) floats or None.
        This mirrors logic in restrictions.py but kept here as a safeguard.
        """
        if restriction is None:
            return None
        if isinstance(restriction, (list, tuple)) and len(restriction) >= 2:
            try:
                return (float(restriction[0]), float(restriction[1]))
            except (TypeError, ValueError):
                return None
        if isinstance(restriction, dict):
            if 'min' in restriction and 'max' in restriction:
                try:
                    return (float(restriction['min']), float(restriction['max']))
                except (TypeError, ValueError):
                    return None
            if 'range' in restriction and isinstance(restriction['range'], (list, tuple)) and len(restriction['range']) >= 2:
                try:
                    return (float(restriction['range'][0]), float(restriction['range'][1]))
                except (TypeError, ValueError):
                    return None
        # single numeric value
        try:
            v = float(restriction)
            return (v, v)
        except (TypeError, ValueError):
            return None

    def check_action(self, action_data: Dict) -> Tuple[bool, List[str]]:
        """
        Check if an action is safe according to restrictions

        Args:
            action_data: Action JSON dictionary

        Returns:
            Tuple of (is_valid, error_messages)
        """
        errors: List[str] = []
        is_valid = True

        # If movementSequence exists, ensure numeric values are floats
        movement_sequence = action_data.get('movementSequence', [])
        if not movement_sequence:
            return True, []  # No movements = safe

        action_name = action_data.get('actionName', action_data.get('label', ['Unknown'])[0] if isinstance(action_data.get('label'), list) else action_data.get('label', 'Unknown'))

        for movement in movement_sequence:
            if movement.get('type') != 'jointMovement':
                continue

            time = movement.get('time', 0)
            joints_raw = movement.get('joints', {})
            joints = self._convert_numbers_in_joints(joints_raw)

            for joint_name, value in joints.items():
                # get normalized restriction from Restrictions singleton
                raw_restr = self.restrictions.getRestriction(joint_name)
                restr = self._normalize_restriction(raw_restr)

                if restr is None:
                    errors.append(f"[WARNING] No restriction found for joint '{joint_name}' at time {time}s in action '{action_name}'")
                    continue

                min_val, max_val = restr

                # ensure value is numeric
                try:
                    val_num = float(value)
                except (TypeError, ValueError):
                    errors.append(f"[WARNING] Non-numeric joint value for '{joint_name}' at time {time}s in action '{action_name}': {value!r}")
                    continue

                # comparison
                if val_num < min_val or val_num > max_val:
                    is_valid = False
                    errors.append(f"[ERROR] Joint '{joint_name}' value {val_num} is out of bounds [{min_val}, {max_val}] at time {time}s in action '{action_name}'")

        # audio file note (no filesystem check here)
        audio = action_data.get('audio', {})
        if audio.get('externalFile'):
            pass

        return is_valid, errors

    def check_action_file(self, action_path: str) -> Tuple[bool, List[str]]:
        """
        Check an action JSON file

        Args:
            action_path: Path to action JSON file

        Returns:
            Tuple of (is_valid, error_messages)
        """
        try:
            with open(action_path, 'r', encoding='utf-8') as f:
                action_data = json.load(f)
        except FileNotFoundError:
            return False, [f"[ERROR] File not found: {action_path}"]
        except json.JSONDecodeError as e:
            return False, [f"[ERROR] Invalid JSON in {action_path}: {e}"]
        except Exception as e:
            return False, [f"[ERROR] Unexpected error reading {action_path}: {e}"]

        return self.check_action(action_data)

    def validate_and_update_action(self, action_path: str) -> bool:
        """
        Validate action and update its 'valid' flag in the JSON file

        Args:
            action_path: Path to action JSON file

        Returns:
            True if action is valid, False otherwise
        """
        try:
            with open(action_path, 'r', encoding='utf-8') as f:
                action_data = json.load(f)
        except Exception as e:
            print(f"✗ Error processing {action_path}: {e}")
            return False

        is_valid, errors = self.check_action(action_data)

        # Update and write back
        action_data['valid'] = is_valid
        if errors:
            action_data['errorLog'] = errors
        else:
            action_data.pop('errorLog', None)

        try:
            with open(action_path, 'w', encoding='utf-8') as f:
                json.dump(action_data, f, indent=2)
        except Exception as e:
            print(f"✗ Could not write updated action file {action_path}: {e}")
            # still continue to return validity

        action_name = action_data.get('actionName', action_path)
        if is_valid:
            print(f"✓ Action '{action_name}' is SAFE")
        else:
            print(f"✗ Action '{action_name}' is UNSAFE")
            for error in errors:
                print(f"  {error}")

        return is_valid

    def check_all_actions(self, actions_dir: str) -> Dict[str, bool]:
        actions_path = Path(actions_dir)
        results: Dict[str, bool] = {}

        print(f"\n=== Checking Actions in {actions_dir} ===\n")

        for json_file in sorted(actions_path.glob('*.json')):
            action_name = json_file.stem
            is_valid = self.validate_and_update_action(str(json_file))
            results[action_name] = is_valid

        print(f"\n=== Summary ===")
        print(f"Total actions: {len(results)}")
        print(f"Safe actions: {sum(results.values())}")
        print(f"Unsafe actions: {len(results) - sum(results.values())}")

        return results


# Convenience functions and CLI
def check_action(action_path: str, restrictions_path: str) -> Tuple[bool, List[str]]:
    checker = ErrorChecker(restrictions_path)
    return checker.check_action_file(action_path)


def validate_all_actions(actions_dir: str, restrictions_path: str) -> Dict[str, bool]:
    checker = ErrorChecker(restrictions_path)
    return checker.check_all_actions(actions_dir)


if __name__ == "__main__":
    import sys

    if len(sys.argv) < 3:
        print("Usage:")
        print("  Check single action: python error_check.py <action.json> <restrictions.json>")
        print("  Check all actions:   python error_check.py <actions_dir> <restrictions.json> --all")
        sys.exit(1)

    action_or_dir = sys.argv[1]
    restrictions_path = sys.argv[2]
    check_all = len(sys.argv) > 3 and sys.argv[3] == '--all'

    if check_all:
        validate_all_actions(action_or_dir, restrictions_path)
    else:
        checker = ErrorChecker(restrictions_path)
        ok = checker.validate_and_update_action(action_or_dir)
        sys.exit(0 if ok else 1)


 # to run it, paste this in your terminal. it will tell you whats safe and what's not. python error_check.py actions/ restrictions.json --all
