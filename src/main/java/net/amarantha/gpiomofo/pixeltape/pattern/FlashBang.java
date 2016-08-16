package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.utility.Now;

public class FlashBang extends PixelTapePattern {

    int totalFlashes = 5;
    int flashCount;
    boolean inFlash;
    long lastStart;
    long flashDuration = 50;
    long darkDuration = 500;

    private RGB flashColour = new RGB(255,255,255);
    private RGB darkColour = new RGB(0,0,0);
    private RGB sparkColour = new RGB(150,150,150);

    @Inject private Now now;

    @Override
    public void start() {
        super.start();
        lastStart = 0;
        intensity = 1.0;
        flashCount = 0;
        inFlash = false;
    }

    private int offset;
    private int sparkSpacing = 10;
    private boolean reverseSpark;
    private long lastMove;
    private int moveAmount = 3;
    private long moveInterval = 60;

    public FlashBang setReverse(boolean reverse) {
        this.reverseSpark = reverse;

        return this;
    }

    @Override
    protected void update() {

        if ( flashCount < totalFlashes ) {

            if (inFlash) {
                if (now.epochMilli() - lastStart >= flashDuration) {
                    setAll(darkColour);
                    inFlash = false;
                    lastStart = now.epochMilli();
                    flashCount++;
                }
            } else {
                if (now.epochMilli() - lastStart >= darkDuration) {
                    setAll(flashColour);
                    inFlash = true;
                    lastStart = now.epochMilli();
                } else if (now.epochMilli() - lastMove >= moveInterval) {
                    for (int i = 0; i < getPixelCount(); i += sparkSpacing) {
                        int p = (i + offset) % getPixelCount();
                        setPixel(p, sparkColour);
                        for (int j = 1; j < sparkSpacing; j++) {
                            int q = (i + j + offset) % getPixelCount();
                            setPixel(q, darkColour);
                        }
                    }
                    if (reverseSpark) {
                        offset -= moveAmount;
                        if (offset < 0) {
                            offset = getPixelCount() - 1;
                        }
                    } else {
                        offset += moveAmount;
                        if (offset >= getPixelCount()) {
                            offset = 0;
                        }
                    }
                    lastMove = now.epochMilli();
                }

            }
        } else if (intensity>0) {
            if ( now.epochMilli() - lastChangedIntensity > fadeOutInterval ) {
                setAll(flashColour.withBrightness(intensity));
                intensity -= intensityDelta;
                lastChangedIntensity = now.epochMilli();
            }
        }

    }

    private double intensityDelta = 0.1;
    private long lastChangedIntensity;
    private long fadeOutInterval = 200;
    private double intensity = 1.0;


}
