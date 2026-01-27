"""
XAR to JSON Converter Module
Converts Choregraphe XAR files (NAOqi/SoftBank Robotics) to JSON format
Supports both individual XAR files and ZIP archives containing XAR files
"""

import json
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path
from typing import Dict, List, Union, Optional, Any
import random


class XARConverter:
    """Converts Choregraphe XAR files to JSON format with time-based structure"""
    
    def __init__(self, start_id: int = 1):
        self.namespace = {'xar': 'http://www.ald.softbankrobotics.com/schema/choregraphe/project.xsd'}
        self.current_id = start_id
    
    def _get_next_id(self) -> int:
        """Generate next unique ID"""
        next_id = self.current_id
        self.current_id += 1
        return next_id
    
    def _parse_box(self, box_element: ET.Element) -> Dict[str, Any]:
        """
        Parse a Box element with all its components
        
        Args:
            box_element: Box XML element
            
        Returns:
            Dictionary with box data in new format
        """
        # Extract basic info (IGNORE the box ID from XAR - we generate our own)
        action_name = box_element.get('name', '')
        tooltip = box_element.get('tooltip', '')
        
        # Parse tooltip for better label and actionName
        extracted_id = None
        tags = []
        
        if tooltip:
            lines = tooltip.split('\n')
            for i, line in enumerate(lines):
                line = line.strip()
                if 'ID :' in line or 'ID:' in line:
                    extracted_id = line.replace('ID :', '').replace('ID:', '').strip()
                elif 'Tags :' in line or 'Tags:' in line:
                    if i + 1 < len(lines):
                        tag_line = lines[i + 1].strip()
                        if tag_line and not tag_line.startswith('=') and tag_line.startswith('-'):
                            tag = tag_line.replace('-', '').strip()
                            tags.append(tag)
        
        # Create actionName from extracted data
        if extracted_id:
            action_name = extracted_id.replace('#', '').replace(' ', '_')
        elif tags:
            action_name = tags[0].replace(' ', '_')
        elif action_name == 'root':
            action_name = f"Action_{random.randint(1000, 9999)}"
        
        # Generate unique ID (INCREMENT, don't use XAR ID)
        unique_id = self._get_next_id()
        
        # Create label array from tags or use extracted_id
        label_array = tags if tags else ([extracted_id] if extracted_id else [action_name])
        
        # Determine icon based on tags
        icon = '🤖'
        if tags:
            tag_lower = tags[0].lower()
            if 'kiss' in tag_lower:
                icon = '💋'
            elif 'wave' in tag_lower:
                icon = '👋'
            elif 'dance' in tag_lower:
                icon = '💃'
            elif 'transition' in tag_lower or 'lean' in tag_lower:
                icon = '🙇'
            elif 'hello' in tag_lower or 'hi' in tag_lower:
                icon = '👋'
        
        # Check bitmap for icon hints
        bitmap = box_element.find('bitmap')
        if bitmap is not None and bitmap.text and icon == '🤖':
            icon_path = bitmap.text.strip().lower()
            if 'move' in icon_path or 'walk' in icon_path:
                icon = '🚶'
            elif 'turn' in icon_path:
                icon = '↪️'
            elif 'sit' in icon_path:
                icon = '💺'
            elif 'stand' in icon_path:
                icon = '🧍'
        
        box_data = {
            'actionName': action_name,
            'id': unique_id,
            'category': tags[0] if tags else 'Uncategorized',
            'label': label_array,
            'color': '#6366f1',
            'icon': icon,
            'valid': True,
            'totalTime': 0.0,
            'movementSequence': [],
            'execution': {
                'interpolation': 'linear',
                'blocking': False
            },
            'audio': {
                'text': '',
                'externalFile': '',
                'startTime': 0.0,
                'blocking': False
            }
        }
        
        # Extract timeline for movement sequence (TIME-BASED)
        timeline_elem = box_element.find('Timeline')
        if timeline_elem is not None:
            fps = float(timeline_elem.get('fps', '25'))  # Frames per second
            
            # Collect all joint movements by frame
            joint_data_by_frame = {}  # {frame: {joint_name: value}}
            
            for actuator_elem in timeline_elem.findall('.//ActuatorCurve'):
                joint_name = actuator_elem.get('actuator', '')
                
                for key_elem in actuator_elem.findall('Key'):
                    frame = int(key_elem.get('frame', '0'))
                    value = float(key_elem.get('value', '0'))
                    
                    if frame not in joint_data_by_frame:
                        joint_data_by_frame[frame] = {}
                    
                    joint_data_by_frame[frame][joint_name] = value
            
            # Convert frame-based data to time-based movement sequence
            movement_sequence = []
            sorted_frames = sorted(joint_data_by_frame.keys())
            
            for frame in sorted_frames:
                time_seconds = round(frame / fps, 3)
                joints = joint_data_by_frame[frame]
                
                movement_event = {
                    'type': 'jointMovement',
                    'time': time_seconds,
                    'joints': joints,
                    'velocity': None
                }
                movement_sequence.append(movement_event)
            
            box_data['movementSequence'] = movement_sequence
            
            # Calculate total time
            if sorted_frames:
                box_data['totalTime'] = round(sorted_frames[-1] / fps, 3)
        
        # Extract audio resources
        for resource_elem in box_element.findall('Resource'):
            resource_type = resource_elem.get('type', '')
            if resource_type and 'audio' in resource_type.lower():
                if resource_elem.text:
                    box_data['audio']['externalFile'] = resource_elem.text.strip()
                break
        
        return box_data
    
    def parse_xar(self, xar_content: Union[str, bytes]) -> Dict:
        """
        Parse XAR file content and extract all information
        
        Args:
            xar_content: XAR file content as string or bytes
            
        Returns:
            Dictionary with complete extracted information
        """
        if isinstance(xar_content, bytes):
            xar_content = xar_content.decode('utf-8')
        
        root = ET.fromstring(xar_content)
        
        # Find and parse the main Box element
        box = root.find('.//Box[@name="root"]')
        if box is None:
            box = root.find('Box')
        
        if box is not None:
            return self._parse_box(box)
        
        return {}
    
    def xar_to_json(self, xar_path: Union[str, Path]) -> str:
        """
        Convert XAR file to JSON string
        
        Args:
            xar_path: Path to XAR file
            
        Returns:
            JSON string
        """
        xar_path = Path(xar_path)
        
        with open(xar_path, 'r', encoding='utf-8') as f:
            xar_content = f.read()
        
        data = self.parse_xar(xar_content)
        return json.dumps(data, indent=2)
    
    def xar_to_json_file(self, xar_path: Union[str, Path], output_path: Optional[Union[str, Path]] = None) -> Path:
        """
        Convert XAR file to JSON file
        
        Args:
            xar_path: Path to XAR file
            output_path: Path for output JSON file
            
        Returns:
            Path to created JSON file
        """
        xar_path = Path(xar_path)
        
        # Get the original filename (without extension) to use as actionName
        original_filename = xar_path.stem  # e.g., "elephant" from "elephant.xar"
        
        with open(xar_path, 'r', encoding='utf-8') as f:
            xar_content = f.read()
        
        data = self.parse_xar(xar_content)
        
        # Override actionName with the actual filename if it's more descriptive
        if data.get('actionName', '').startswith('Action_') or data.get('actionName', '').startswith('root'):
            # Use the filename instead of generic name
            data['actionName'] = original_filename
        elif original_filename not in ['box', 'behavior', 'root']:
            # If the filename is descriptive (not generic), use it
            data['actionName'] = original_filename
        
        if output_path is None:
            # Use actionName_ID.json format
            action_name = data.get('actionName', original_filename)
            action_id = data.get('id', 1)
            output_path = xar_path.parent / f"{action_name}_{action_id}.json"
        else:
            output_path = Path(output_path)
        
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2)
        
        return output_path
    
    

    def process_zip(self, zip_path: Union[str, Path], output_dir: Optional[Union[str, Path]] = None) -> List[Path]:
        """
        Process ZIP file containing XAR files and convert them to individual JSON files
        
        Args:
            zip_path: Path to ZIP file
            output_dir: Directory for output JSON files
            
        Returns:
            List of paths to created JSON files
        """
        zip_path = Path(zip_path)
        
        if output_dir is None:
            output_dir = zip_path.parent / zip_path.stem
        else:
            output_dir = Path(output_dir)
        
        output_dir.mkdir(parents=True, exist_ok=True)
        
        created_files = []
        
        with zipfile.ZipFile(zip_path, 'r') as zf:
            for file_info in zf.namelist():
                if file_info.lower().endswith('.xar'):
                    xar_content = zf.read(file_info)
                    
                    # Get the original filename from the ZIP
                    original_filename = Path(file_info).stem
                    
                    data = self.parse_xar(xar_content)
                    
                    # Override actionName with filename if more descriptive
                    if data.get('actionName', '').startswith('Action_') or data.get('actionName', '').startswith('root'):
                        data['actionName'] = original_filename
                    elif original_filename not in ['box', 'behavior', 'root']:
                        data['actionName'] = original_filename
                    
                    # Create filename from actionName and ID
                    action_name = data.get('actionName', original_filename)
                    action_id = data.get('id', 0)
                    filename = f"{action_name}_{action_id}.json"
                    
                    output_path = output_dir / filename
                    output_path.parent.mkdir(parents=True, exist_ok=True)
                    
                    with open(output_path, 'w', encoding='utf-8') as f:
                        json.dump(data, f, indent=2)
                    
                    created_files.append(output_path)
        
        return created_files
    
    def process(self, input_path: Union[str, Path], output_path: Optional[Union[str, Path]] = None) -> Union[Path, List[Path]]:
        """
        Process either a single XAR file or a ZIP file containing XAR files
        
        Args:
            input_path: Path to XAR or ZIP file
            output_path: Path for output
            
        Returns:
            Path to created JSON file or list of paths for ZIP
        """
        input_path = Path(input_path)
        
        if input_path.suffix.lower() == '.zip':
            return self.process_zip(input_path, output_path)
        elif input_path.suffix.lower() == '.xar':
            return self.xar_to_json_file(input_path, output_path)
        else:
            raise ValueError(f"Unsupported file type: {input_path.suffix}")


# Convenience functions
def xar_to_json(xar_path: Union[str, Path], start_id: int = 1) -> str:
    """Convert XAR file to JSON string"""
    converter = XARConverter(start_id=start_id)
    return converter.xar_to_json(xar_path)


def xar_to_json_file(xar_path: Union[str, Path], output_path: Optional[Union[str, Path]] = None, start_id: int = 1) -> Path:
    """Convert XAR file to JSON file"""
    converter = XARConverter(start_id=start_id)
    return converter.xar_to_json_file(xar_path, output_path)


def process_zip(zip_path: Union[str, Path], output_dir: Optional[Union[str, Path]] = None, start_id: int = 1) -> List[Path]:
    """Process ZIP file containing XAR files"""
    converter = XARConverter(start_id=start_id)
    return converter.process_zip(zip_path, output_dir)


def process(input_path: Union[str, Path], output_path: Optional[Union[str, Path]] = None, start_id: int = 1) -> Union[Path, List[Path]]:
    """Process either XAR or ZIP file"""
    converter = XARConverter(start_id=start_id)
    return converter.process(input_path, output_path)


if __name__ == "__main__":
    import sys
    
    if len(sys.argv) < 2:
        print("Usage: python xar_converter.py <input_file.xar|input_file.zip> [output_path]")
        sys.exit(1)
    
    input_file = sys.argv[1]
    output = sys.argv[2] if len(sys.argv) > 2 else None
    
    result = process(input_file, output)
    
    if isinstance(result, list):
        print(f"✓ Processed {len(result)} XAR files")
    else:
        print(f"✓ Created: {result}")
