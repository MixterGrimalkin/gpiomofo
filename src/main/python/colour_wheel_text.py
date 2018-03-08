from colour_wheel import ColourWheel

class ColourWheelText(ColourWheel):

    def render(self):
        for i in range(len(self.pixels)):
            print self.pixels[i]
        print

