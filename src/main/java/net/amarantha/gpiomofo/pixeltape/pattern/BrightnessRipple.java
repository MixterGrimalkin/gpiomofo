package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.gpiomofo.utility.TimeGuard;

public class BrightnessRipple extends PixelTapeTarget {

    @Inject private PixelTapeController tape;
    @Inject private TimeGuard guard;

    private int rippleWidth = 14;
    private int rippleMove = 4;

    private int currentStart;

    @Override
    public void start() {
        super.start();
        currentStart = -rippleWidth;
        originalPixels = new RGB[currentPattern.length];
        for ( int p=0; p<currentPattern.length; p++ ) {
            originalPixels[p] = getPixel(p);
        }
    }

    private RGB[] originalPixels;

    @Override
    protected void update() {

        guard.every(100, this, ()->{

            for ( int p=0; p<getPixelCount(); p++ ) {
                if ( p>=currentStart && p<getPixelCount() && p<currentStart+rippleWidth ) {
                    currentPattern[p] = getPixel(p).withBrightness(0.25);
                } else {
                    currentPattern[p] = originalPixels[p];
                }
            }
            currentStart += rippleMove;
            if ( currentStart>=getPixelCount() ) {
                currentStart = -rippleWidth;
            }





        });

    }
}
