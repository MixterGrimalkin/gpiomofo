from pygame import mixer
from threading import Thread


class AudioPlayer:

    stop_callback = None

    def __init__(self):
        mixer.init()

    def play(self, filename, callback = None):
        mixer.music.load(filename)
        mixer.music.play()
        self.stop_callback = callback
        if callback is not None:
            t = Thread(target=self.wait_for_stop())
            t.daemon = True
            t.start()

    def stop(self):
        mixer.music.stop()
        if self.stop_callback is not None:
            self.stop_callback()

    def wait_for_stop(self):
        while mixer.music.get_busy():
            continue
        if self.stop_callback is not None:
            self.stop_callback()

    def is_playing(self):
        return mixer.music.get_busy()

