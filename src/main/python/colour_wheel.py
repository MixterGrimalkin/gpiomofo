import abc
import time
from threading import Thread
from multiprocessing import Process

class ColourWheel:

    def __init__(self, wheel_size):
        self.wheel_size = wheel_size
        self.pixels = [(0,0,0)] * wheel_size
        self.palettes = []
        self.centres = []
        self.sleep_time = 0.1
        self.running = False

    @abc.abstractmethod
    def render(self):
        """Render pixels"""

    def tick(self):
        self.pixels.append(self.pixels.pop(0))
        self.render()


    def run(self):
        print "Run"
        while self.running:
            self.tick()
            time.sleep(self.sleep_time)
            print self.running


    def start(self):
        self.running = True
        print "Start"
        process = Process(target=self.run)
        process.start()

    def stop(self):
        print "Stop\n"
        self.running = False

