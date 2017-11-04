# NeoPixel library strandtest example
# Author: Tony DiCola (tony@tonydicola.com)
#
# Direct port of the Arduino NeoPixel library strandtest example.  Showcases
# various animations on a strip of NeoPixels.
import random
# import RPi.GPIO as gpio
import time

from neopixel import *

# LED strip configuration:
LED_COUNT = 25  # Number of LED pixels.
LED_PIN = 18  # GPIO pin connected to the pixels (must support PWM!).
LED_FREQ_HZ = 800000  # LED signal frequency in hertz (usually 800khz)
LED_DMA = 5  # DMA channel to use for generating signal (try 5)
LED_BRIGHTNESS = 255  # Set to 0 for darkest and 255 for brightest
LED_INVERT = False  # True to invert the signal (when using NPN transistor level shift)


# COMM_PIN = 18

class PixelTape:
    strip = Adafruit_NeoPixel(LED_COUNT, LED_PIN, LED_FREQ_HZ, LED_DMA, LED_INVERT, LED_BRIGHTNESS)

    cancelPattern = False

    def __init__(self):
        self.strip.begin()

    def time_travel_pattern(self):

        time.sleep(0.3)

        self.flash(1, 0.05, 0.15, 100, 90, 200)

        self.fade_in(0.2, 0, 200, 255)
        time.sleep(0.1)
        self.fade_out(0.3, 0, 255, 200)

        self.theater_chase(duration=4.95, fade_in=2.5)

        self.flash(1, 0.1, 0, 100, 100, 150)

        self.theater_chase(duration=0.35, fade_in=0.02)

        self.flash(1, 0.1, 0, 180, 180, 230)

        self.theater_chase(duration=0.38, fade_in=0.02)

        self.fade_out(3.3, 255, 255, 255)

        time.sleep(1)

    def clear(self):
        for i in range(LED_COUNT):
            self.strip.setPixelColor(i, Color(0,0,0))

    def draw(self, pixel, colour):
        self.strip.setPixelColor(pixel, Color(int(colour[1]), int(colour[0]), int(colour[2])))

    def render(self):
        self.strip.show()

    def all_on(self, colour):
        for i in range(LED_COUNT):
            self.strip.setPixelColor(i, colour)

        self.strip.show()

    def clear(self):
        self.all_on(Color(0, 0, 0))

    def cancel(self):
        print "Cancel"
        self.cancelPattern = True

    def flash(self, times, on_time, off_time, green, red, blue):
        for i in range(times):
            self.all_on(Color(green, red, blue))
            time.sleep(on_time)
            self.clear()
            time.sleep(off_time)

    def fade_in(self, duration, green, red, blue):
        self.clear()
        start_time = time.time()
        while time.time() - start_time <= duration:
            progress = (time.time() - start_time) / duration
            g = int(progress * green)
            r = int(progress * red)
            b = int(progress * blue)
            self.all_on(Color(g, r, b))

    def fade_out(self, duration, green, red, blue):
        self.all_on(Color(green, red, blue))
        start_time = time.time()
        while time.time() - start_time <= duration:
            progress = 1 - ((time.time() - start_time) / duration)
            g = int(progress * green)
            r = int(progress * red)
            b = int(progress * blue)
            self.all_on(Color(g, r, b))

    def theater_chase(self, duration=11, fade_in=0, wait_ms=40):
        g = 255
        r = 255
        b = 100
        d = 50

        self.cancelPattern = False

        start_time = time.time()

        while not self.cancelPattern and time.time() - start_time <= duration:
            if time.time() - start_time <= fade_in:
                brightness = (time.time() - start_time) / fade_in
            else:
                brightness = 1
            for q in range(3):
                for i in range(0, self.strip.numPixels(), 3):
                    self.strip.setPixelColor(i + q,
                                             Color(int(g * brightness), int(r * brightness), int(b * brightness)))
                self.strip.show()
                time.sleep(wait_ms / 1000.0)
                for i in range(0, self.strip.numPixels(), 3):
                    self.strip.setPixelColor(i + q, 0)

            g = g + d
            b = b + d
            if g >= 255 or b >= 255:
                g = 255
                b = 100
                d = -d
            if g <= 0 or b <= 0:
                g = 0
                b = 0
                d = -d

        self.clear()

    def twinkle(self, duration, min_green, max_green, delta_g, min_red, max_red, delta_r, min_b, max_b, delta_b, fade):
        green = []
        green_delta = []
        red = []
        red_delta = []
        blue = []
        blue_delta = []

        start_time = time.time()

        brightness = 0

        for i in range(self.strip.numPixels()):
            g = int(random.random() * (max_green - min_green)) + min_green
            r = int(random.random() * (max_red - min_red)) + min_red
            b = int(random.random() * (max_b - min_b)) + min_b
            green.append(g)
            red.append(r)
            blue.append(b)
            if random.random() < 0.5:
                green_delta.append(delta_g)
                red_delta.append(delta_r)
                blue_delta.append(delta_b)
            else:
                green_delta.append(-delta_g)
                red_delta.append(-delta_r)
                blue_delta.append(-delta_b)

            self.strip.setPixelColor(i, Color(int(g * brightness), int(r * brightness), int(b * brightness)))
            self.strip.show()

        while time.time() - start_time <= duration:

            if time.time() - start_time <= fade:
                brightness = (time.time() - start_time) / fade
            elif duration - (time.time() - start_time) <= fade:
                brightness = (duration - (time.time() - start_time)) / fade
            else:
                brightness = 1

            for i in range(self.strip.numPixels()):

                g = green[i] + green_delta[i]
                if g >= max_green:
                    g = max_green
                    green_delta[i] = -green_delta[i]
                elif g <= min_green:
                    g = min_green
                    green_delta[i] = -green_delta[i]
                green[i] = g

                r = red[i] + red_delta[i]
                if r >= max_red:
                    r = max_red
                    red_delta[i] = -red_delta[i]
                elif r <= min_red:
                    r = min_red
                    red_delta[i] = -red_delta[i]
                red[i] = r

                b = blue[i] + blue_delta[i]
                if b >= max_b:
                    b = max_b
                    blue_delta[i] = -blue_delta[i]
                elif b <= min_b:
                    b = min_b
                    blue_delta[i] = -blue_delta[i]
                blue[i] = b

                self.strip.setPixelColor(i, Color(int(g * brightness), int(r * brightness), int(b * brightness)))
                self.strip.show()

    def bound(self, i):
        if i < 0:
            return 0
        if i > 255:
            return 255
        return i


if __name__ == '__main__':
    PixelTape().time_travel_pattern()
