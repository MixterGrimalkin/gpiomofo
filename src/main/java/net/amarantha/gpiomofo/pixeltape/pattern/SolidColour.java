package net.amarantha.gpiomofo.pixeltape.pattern;

import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.target.PixelTapeTarget;

public class SolidColour extends PixelTapeTarget {

    private RGB colour;

    public SolidColour setColour(RGB rgb) {
        colour = rgb;
        return this;
    }

    @Override
    protected void update() {
        for ( int i=0; i<getPixelCount(); i++ ) {
            setPixel(i, colour);
        }
    }
}
