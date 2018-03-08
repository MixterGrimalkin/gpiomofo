from colour_wheel_text import ColourWheelText
import time

wheel = ColourWheelText(10)
wheel.sleep_time = 2
wheel.start()
print "GO!"
time.sleep(5)
wheel.stop()


