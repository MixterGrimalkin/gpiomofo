package net.amarantha.gpiomofo.pixeltape.pattern;

import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.target.PixelTapeTarget;

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
            System.out.println("Updating RGB");
            for (int i = 0; i < getPixelCount(); i++) {
                setPixel(i, colour);
            }
            done = true;
        }
    }
}
