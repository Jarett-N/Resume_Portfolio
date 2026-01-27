from command import CommandModule
import time
import qi
import threading
cmd = CommandModule.getInstance()
print("CommandModule instance created.")
cmd.setIP("10.35.53.242")
# cmd.setIP("172.24.10.107")
print("Set IP")
cmd.setPort(9559)
print("Set Port")
cmd._connect_qi()

#audio_dev = cmd._session.service("ALAudioDevice")
#print("Accessed ALAudioDevice service.")
#ok = audio_dev.sendRemoteBufferToOutput(1, b'\x00\x00\x00\x00')
#print(ok)


# cmd.setVolume(50)
# cmd.sendAction("arm_wave_demo.json")
# cmd.sendAction("head_turn_velocity_test.json")
# cmd.sendAction("dual_arm_velocity_test.json")
# actionthread = threading.Thread(target=cmd.sendAction, args=("/home/otjale/ASRE/actions/Tai Chi/Action_5475_1.json",))
# actionthread.start()
# time.sleep(5)
# cmd.emergency_stop()
cmd.sendAction("../actions/Tai Chi/Action_5475_1.json")
# cmd.start_motion("forward", speed=0.3)
# time.sleep(15)
# cmd.stop_motion()
