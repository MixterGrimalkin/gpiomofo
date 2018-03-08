import random
# import RPi.GPIO as gpio
import time

from neopixel import *

MODE = "GRBW"

WARM_WHITE = True

# LED strip configuration:
LED_PIN = 18                # GPIO pin connected to the pixels (must support PWM!).
LED_FREQ_HZ = 800000        # LED signal frequency in hertz (usually 800khz)
LED_DMA = 5                 # DMA channel to use for generating signal (try 5)
LED_BRIGHTNESS = 50        # Set to 0 for darkest and 255 for brightest
LED_INVERT = False          # True to invert the signal (when using NPN transistor level shift)

class PixelTape:

    strip = None
    pixel_count = 0
    output_pixel_count = 0

    pixels = []

    def __init__(self, pixel_count):
        self.pixel_count = pixel_count
        if self.use_w():
            self.output_pixel_count = int(self.pixel_count * (4.0/3.0))
        else:
            self.output_pixel_count = self.pixel_count
        self.strip = Adafruit_NeoPixel(self.output_pixel_count, LED_PIN, LED_FREQ_HZ, LED_DMA, LED_INVERT, LED_BRIGHTNESS)
        self.pixels = [[0,0,0] for i in range(pixel_count)]
        self.strip.begin()

    def use_w(self):
        return MODE=="RGBW" or MODE=="GRBW"

    def clear(self):
        for i in range(self.pixel_count):
            self.draw(i, [0,0,0])

    def draw(self, pixel, colour):
        if MODE=="RGB":
            self.pixels[pixel] = [int(colour[0]), int(colour[1]), int(colour[2])]
        elif MODE=="GRB":
            self.pixels[pixel] = [int(colour[1]), int(colour[0]), int(colour[2])]
        elif MODE=="RGBW":
            self.pixels[pixel] = [int(colour[0]), int(colour[1]), int(colour[2]), 0]
        elif MODE=="GRBW":
            self.pixels[pixel] = [int(colour[1]), int(colour[0]), int(colour[2]), 0]

    def render(self):
        if self.use_w():
            pixel = 0
            for i in range((self.pixel_count/3)):
                p = i*3
                a = self.pixels[p]
                b = self.pixels[p+1]
                c = self.pixels[p+2]
                self.strip.setPixelColor(pixel, Color(a[0], a[1], a[2]))
                self.strip.setPixelColor(pixel+1, Color(0, b[0], b[1]))
                self.strip.setPixelColor(pixel+2, Color(b[2], 0, c[0]))
                self.strip.setPixelColor(pixel+3, Color(c[1], c[2], 0))
                pixel += 4
        else:
            for i in range(self.pixel_count):
                colour = self.pixels[i]
                self.strip.setPixelColor(i, Color(int(colour[0]), int(colour[1]), int(colour[2])))

        self.strip.show()

    def bound(self, i):
        if i < 0:
            return 0
        if i > 255:
            return 255
        return i


