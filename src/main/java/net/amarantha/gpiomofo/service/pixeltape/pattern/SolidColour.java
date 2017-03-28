package net.amarantha.gpiomofo.service.pixeltape.pattern;

import net.amarantha.gpiomofo.service.pixeltape.PixelTapeTarget;
import net.amarantha.utils.colour.RGB;

public class SolidColour extends PixelTapeTarget {

    private RGB colour;

    private boolean done;

    @Override
    public void start() {
        super.start();
        done = false;
    }

    public SolidColour setColour(RGB rgb) {
        colour = rgb;
        return this;
    }

    @Override
    protected void update() {
        if ( !done ) {
            for (int i = 0; i < getPixelCount(); i++) {
                setPixel(i, colour);
            }
            done = true;
        }
    }
}
