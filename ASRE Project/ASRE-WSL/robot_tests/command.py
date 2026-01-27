#!/usr/bin/env python3
"""
ASRE Command Module (Qi Integrated + Audio Fallback)
----------------------------------------------------
Singleton that manages robot connection parameters (IP, port)
and executes validated Action JSONs using the Qi framework.
Now supports fallback to text-to-speech (TTS) if no audio file exists.
"""

import os
import json
import time
import logging
import threading
import qi
import math
from pathlib import Path
from typing import Any, Dict, List # for type hints

# Setup logging
os.makedirs("logs", exist_ok=True)
logging.basicConfig(
    filename="logs/command_module.log",
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
)

# CommandModule Singleton
class CommandModule:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(CommandModule, cls).__new__(cls)
            cls._instance._ip = None
            cls._instance._port = None
            cls._instance._session = None
            cls._instance._motion = None
            cls._instance._audio = None
            cls._instance._tts = None
        return cls._instance

    # Initialization
    @staticmethod
    def getInstance():
        if CommandModule._instance is None:
            CommandModule()
        return CommandModule._instance

    def setIP(self, ip: str):
        if not ip or not isinstance(ip, str):
            raise ValueError("Invalid IP address.")
        self._ip = ip.strip()
        logging.info(f"Set robot IP to {self._ip}")

    def getIP(self) -> str:
        return self._ip

    def setPort(self, port: int):
        if not isinstance(port, int) or port <= 0:
            raise ValueError("Port must be a positive integer.")
        self._port = port
        logging.info(f"Set robot port to {self._port}")

    def getPort(self) -> int:
        return self._port

    def getVolume(self) -> int:
        if not self._audio:
            raise ConnectionError("Audio service not connected.")
        return self._audio.getVolume()
    
    def setVolume(self, volume: int):
        if not (0 <= volume <= 100):
            raise ValueError("Volume must be between 0 and 100.")
        audio_dev = self._session.service("ALAudioDevice")
        audio_dev.setOutputVolume(volume)

        logging.info(f"Set audio volume to {volume}")

    def _connect_qi(self):
        #Establish Qi session and obtain services.
        if not self._ip or not self._port:
            raise ConnectionError("IP and port must be set before connecting.")

        target = f"tcp://{self._ip}:{self._port}"
        logging.info(f"Connecting to Qi session at {target}")
        # Acquire Qi session and services
        try:
            self._session = qi.Session()
            self._session.connect(target)
            self._motion = self._session.service("ALMotion")
            self._audio = self._session.service("ALAudioPlayer")
            self._tts = self._session.service("ALTextToSpeech")
            logging.info(f"Connected to Qi framework at {target}")
            print(f"[ASRE] Connected to {target}")
        except Exception as e:
            logging.error(f"Failed to connect to Qi session: {e}")
            raise ConnectionError(f"Failed to connect to Qi framework: {e}")

    def _disconnect_qi(self):
        # Close the Qi session cleanly.
        try:
            if self._session:
                self._session.close()
                print("[ASRE] Qi session closed.")
                logging.info("Qi session closed.")
        except Exception as e:
            logging.warning(f"Error closing Qi session: {e}")

    def emergency_stop(self):
        print("[ASRE] EMERGENCY STOP ACTIVATED")

        try:
            motion = self._session.service("ALMotion")
            motion.stopMove()             # stop constant moves
            motion.killAll()              # kill all interpolations & joint tasks
            motion.setStiffnesses("Body", 1.0)  # ensure joints don't fall limp
            print("[ASRE] Movement force-stopped.")
        except Exception as e:
            print(f"[ASRE] Motion kill failed: {e}")

        try:
            audio = self._session.service("ALAudioDevice")
            audio.stopAll()               # stop tones, buffers, remote audio
            print("[ASRE] Audio stopped.")
        except Exception as e:
            print(f"[ASRE] Audio stop failed: {e}")


    def _json_to_interpolation_lists(self, action_data: dict):
        """
        Convert a joint-format JSON (Elephant-format) into:
        [names, angleLists, timeLists, isAbsolute]

        Example output:
            names      = ["HeadYaw", "HeadPitch", ...]
            angleLists = [[0.1, 0.2, ...], [1.4, 1.2, ...], ...]
            timeLists  = [[0.0, 0.5, 1.0], [0.0, 0.5, 1.0], ...]
            isAbsolute = True
        """

        seq = action_data.get("movementSequence", [])
        if not seq:
            raise ValueError("No movementSequence found in JSON")

        # --- Collect every joint and its timeline ---
        joint_angles = {}   # joint → [angles]
        joint_times  = {}   # joint → [times]

        for frame in seq:
            t = float(frame.get("time", 0.0))

            # only handle joint-frame format
            if "joints" not in frame:
                continue

            for joint, raw_deg in frame["joints"].items():

                # convert degrees → radians automatically
                angle_rad = raw_deg * math.pi / 180.0

                if joint not in joint_angles:
                    joint_angles[joint] = []
                    joint_times[joint] = []

                joint_angles[joint].append(angle_rad)
                joint_times[joint].append(t)

        # --- Sort joints alphabetically for stable ordering ---
        names = sorted(joint_angles.keys())

        # Build angleLists and timeLists in the same order
        angleLists = [joint_angles[joint] for joint in names]
        timeLists  = [joint_times[joint]  for joint in names]

        isAbsolute = True

        return [names, angleLists, timeLists, isAbsolute]

    def _play_streamed_audio(self, path):
        """
        MP3/OGG -> PCM16 stereo -> sendRemoteBufferToOutput using Qi Python (NOT naoqi).
        """
        from pydub import AudioSegment
        import struct

        print("[ASRE] Decoding audio for streaming...")

        audio = AudioSegment.from_file(path)
        audio = audio.set_frame_rate(48000).set_channels(2).set_sample_width(2)
        raw = audio.raw_data  # bytes

        print(f"[ASRE] PCM decoded: {len(raw)} bytes")

        audio_dev = self._session.service("ALAudioDevice")
        print("[ASRE] Obtained ALAudioDevice service.")

        audio_dev.muteAudioOut(False)

        print("[ASRE] Audio output ready.")

        MAX_FRAMES = 16384
        MAX_BYTES = MAX_FRAMES * 4  # 65536 bytes

        offset = 0
        chunk_id = 1

        while offset < len(raw):

            chunk = raw[offset:offset + MAX_BYTES]

            # Ensure whole frames
            valid_len = len(chunk) - (len(chunk) % 4)
            if valid_len <= 0:
                break

            chunk = chunk[:valid_len]
            nb_frames = valid_len // 4

            print(f"[ASRE] Sending chunk {chunk_id}: {nb_frames} frames ({valid_len} bytes)")
            chunk_id += 1

            # --- CRUCIAL FIX ---
            # Qi maps Python bytes directly → ALValue binary
            ok = audio_dev.sendRemoteBufferToOutput(nb_frames, chunk)

            print(f"[ASRE] -> sendRemoteBufferToOutput returned: {ok}")

            if not ok:
                print("[ASRE] ERROR: NAO rejected audio chunk.")
                break

            offset += valid_len

    def sendAction(self, action_path: str):
        """Executes a legacy or joint-format Action JSON using ALMotion interpolation."""
        if not os.path.exists(action_path):
            raise FileNotFoundError(f"Action file not found: {action_path}")

        # Load JSON
        with open(action_path, "r", encoding="utf-8") as f:
            action_data = json.load(f)

        if not action_data.get("valid", False):
            raise ValueError("Action JSON marked as invalid.")

        action_name = action_data.get("actionName", Path(action_path).stem)
        exec_cfg = action_data.get("execution", {})
        audio_cfg = action_data.get("audio", None)

        print(f"[ASRE] Preparing interpolation for '{action_name}'")

        # -------------------------------------------------------
        # 1) Connect to Qi services
        # -------------------------------------------------------
        self._connect_qi()

        # -------------------------------------------------------
        # 2) Pre-motion safety: stop autonomous life + StandInit
        # -------------------------------------------------------
        try:
            print("[ASRE] Stopping autonomous movements and resetting posture...")
            self._motion.wakeUp()
            self._motion.stopMove()
            self._motion.setStiffnesses("Body", 1.0)

            posture = self._session.service("ALRobotPosture")
            posture.goToPosture("StandInit", 0.5)

            print("[ASRE] Robot is now in standard standing position.")
        except Exception as e:
            print(f"[ASRE] Warning: posture reset failed: {e}")
            logging.warning(f"Failed to reset posture: {e}")

        # -------------------------------------------------------
        # 3) Schedule audio if needed
        # -------------------------------------------------------
        start_time = time.time()
        if audio_cfg:
            parent_path = str(Path(action_path).parent)
            self._schedule_audio_or_tts(audio_cfg, start_time, parent_path)

        # -------------------------------------------------------
        # 4) Determine motion format
        # -------------------------------------------------------
        seq = action_data.get("movementSequence", [])
        if not seq:
            print("[ASRE] No movementSequence found. Nothing to execute.")
            self._disconnect_qi()
            return

        joint_format = ("joints" in seq[0])  # Elephant / keyframe format

        # -------------------------------------------------------
        # 5) Build interpolation lists OR run legacy mode
        # -------------------------------------------------------
        if joint_format:
            # Build: names, angleLists, timeLists, isAbsolute
            names, angleLists, timeLists, isAbsolute = \
                self._json_to_interpolation_lists(action_data)

            print("[ASRE] Interpolation data prepared:")
            print(f"  Joints: {len(names)}")
            print(f"  Frames per first joint: {len(angleLists[0])}")

            # ---------------------------------------------------
            # 6) EXECUTE INTERPOLATION (full-motion)
            # ---------------------------------------------------
            print("[ASRE] Executing interpolation...")
            self._motion.angleInterpolation(names, angleLists, timeLists, isAbsolute)

        else:
            # ---------------------------------------------------
            # Legacy mode: execute frame-by-frame (your old format)
            # ---------------------------------------------------
            print("[ASRE] Legacy format detected. Executing per-frame moves.")
            interpolation = exec_cfg.get("interpolation", "linear")
            blocking = bool(exec_cfg.get("blocking", True))

            base_start = time.time()

            for event in seq:
                event_time = float(event.get("time", 0.0))
                delay = event_time - (time.time() - base_start)
                if delay > 0:
                    time.sleep(delay)

                for move in event.get("movements", []):
                    self._execute_move_qi(move, interpolation)

                if blocking:
                    time.sleep(0.02)

        # -------------------------------------------------------
        # 7) Clean disconnect
        # -------------------------------------------------------
        print(f"[ASRE] Action '{action_name}' completed.")
        logging.info(f"Action '{action_name}' completed successfully.")
        self._disconnect_qi()


    # Audio Scheduler
    def _schedule_audio_or_tts(self, audio_cfg: Dict[str, Any], start_time: float, path: str = ""):
        # Play an audio file if it exists, otherwise fall back to TTS if text is provided.
        file_path = path + "/" + audio_cfg.get("externalFile")
        print(f"[ASRE] Scheduling audio from: {file_path}")
        offset = float(audio_cfg.get("startTime", 0.0))
        text = audio_cfg.get("text")

        def play_audio():
            delay = (start_time + offset) - time.time()
            if delay > 0:
                time.sleep(delay)

            try:
                # If file_path is a local file path and exists
                if file_path and os.path.exists(file_path):
                    print(f"[ASRE] Playing local audio file: {file_path}")
                    self._play_streamed_audio(file_path)
                    logging.info(f"Local audio file played: {file_path}")

                # Fallback to TTS
                elif text:
                    print(f"[ASRE] Speaking text: '{text}'")
                    self._tts.say(text)
                    logging.info(f"TTS spoken: {text}")

                else:
                    print("[ASRE] No audio file or text available; skipping speech.")
                    logging.info("Skipped speech: no audio/text found.")

            except Exception as e:
                logging.warning(f"Audio/TTS playback failed: {e}")

        threading.Thread(target=play_audio, daemon=True).start()

    # Execute single movements from the actions
    def _execute_move_qi(self, move: Dict[str, Any], move_type: str, interpolation: str):
        # Send single or multi-joint motion commands to ALMotion
        try:
            # joint move
            # if all(isinstance(v, (int, float)) for v in move.values()):
            if move_type == "jointMove":
                print(f"[ASRE] Compound move with {len(move)} joints")
                joint_names = list(move.keys())
                angles_deg = list(move.values())
                angles_rad = [a * 3.14159 / 180.0 for a in angles_deg]  # convert degrees → radians
                speed = 0.2 if interpolation == "linear" else 0.4
                self._motion.setAngles(joint_names, angles_rad, speed)
                logging.info(f"Compound move executed: {move}")

            # velocity
            elif move_type == "jointVelocity":
                print(f"[ASRE] VelocityCommand detected.")
                print(move)
                joint = move.get("joint")
                vel = float(move.get("velocity", 0.0))
                dur = float(move.get("duration", 1.0))
                print(f"[ASRE] VelocityCommand: {joint} - {vel} for {dur} seconds")

                def apply_velocity(j=joint, v=vel, d=dur):
                    try:
                        self._motion.setAngles(j, v, 0.2)
                        time.sleep(d)
                        self._motion.stopMove()
                    except Exception as e:
                        logging.warning(f"Velocity command failed for {j}: {e}")

                threading.Thread(target=apply_velocity, daemon=True).start()

            else:
                logging.warning(f"Unrecognized move format: {move}")

        except Exception as e:
            logging.warning(f"Failed to send move: {move} | {e}")


    def start_motion(self, direction: str, speed: float = 0.3):
        """
        Begin continuous motion in a direction.
        Direction: 'forward', 'backward', 'left', or 'right'
        Speed: linear/angular velocity factor (0.0 - 1.0)
        Canceled by calling stop_motion()
        """
        try:
            if not self._motion:
                self._connect_qi()

            # Stop any current move first
            self._motion.stopMove()

            vx, vy, vtheta = 0.0, 0.0, 0.0

            if direction == "forward":
                vx = speed
            elif direction == "backward":
                vx = -speed
            elif direction == "rotate_left":
                vtheta = speed
            elif direction == "rotate_right":
                vtheta = -speed
            elif direction == "left":
                vy = speed
            elif direction == "right":
                vy = -speed
            else:
                logging.warning(f"Invalid direction: {direction}")
                return

            self._motion.moveToward(vx, vy, vtheta)
            print(f"[ASRE] Moving {direction} (vx={vx}, vy={vy}, vtheta={vtheta})")
            logging.info(f"D-Pad motion started: {direction}")

        except Exception as e:
            logging.error(f"Failed to start D-Pad motion {direction}: {e}")

    def stop_motion(self):
        #Stop any ongoing movement
        try:
            if self._motion:
                self._motion.stopMove()
                print("[ASRE] Stopped D-Pad motion.")
                logging.info("D-Pad motion stopped.")
        except Exception as e:
            logging.warning(f"Failed to stop D-Pad motion: {e}")
