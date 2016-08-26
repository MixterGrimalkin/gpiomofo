package net.amarantha.gpiomofo.pixeltape.pattern;

import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.RGBW;
import net.amarantha.gpiomofo.target.PixelTapeTarget;

public class SolidColourWithWhite extends PixelTapeTarget {

    private RGBW colour;

    private int realPixelCount;

    private boolean done;

    @Override
    public void start() {
        super.start();
        done = false;
    }


    @Override
    public SolidColourWithWhite init(int startPixel, int pixelCount) {
        super.init(startPixel, ((pixelCount/3)*4));
        realPixelCount = pixelCount;
        setForceRGB(true);
        return this;
    }

    public SolidColourWithWhite setColour(RGBW rgbw) {
        colour = rgbw;
        return this;
    }


    @Override
    protected void update() {
        if ( !done ) {
            System.out.println("Updating RGBW");
            RGBW[] input = new RGBW[getPixelCount()];
            for (int i = 0; i < realPixelCount; i++) {
                input[i] = colour;
//            setPixel(i, colour);
            }

            RGB[] output = RGBW.convertToRGB(input);
            for (int i = 0; i < output.length; i++) {
                if (i < getPixelCount()) {
//                System.out.println(output[i]);
                    setPixel(i, output[i]);
                }
            }
            done = true;
        }
    }
}
