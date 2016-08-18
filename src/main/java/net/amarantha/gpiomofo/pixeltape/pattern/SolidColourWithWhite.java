package net.amarantha.gpiomofo.pixeltape.pattern;

import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.RGBW;

public class SolidColourWithWhite extends PixelTapePattern {

    private RGBW colour;

    private int realPixelCount;

    @Override
    public void init(int startPixel, int pixelCount) {
        super.init(startPixel, ((pixelCount/3)*4));
        realPixelCount = pixelCount;
        setForceRGB(true);
    }

    public SolidColourWithWhite setColour(RGBW rgbw) {
        colour = rgbw;
        return this;
    }


    @Override
    protected void update() {
        RGBW[] input = new RGBW[getPixelCount()];
        for ( int i=0; i<realPixelCount; i++ ) {
            input[i] = colour;
//            setPixel(i, colour);
        }

        RGB[] output = RGBW.convertToRGB(input);
        for ( int i=0; i<output.length; i++ ) {
            if ( i<getPixelCount() ) {
//                System.out.println(output[i]);
                setPixel(i, output[i]);
            }
        }
    }
}
