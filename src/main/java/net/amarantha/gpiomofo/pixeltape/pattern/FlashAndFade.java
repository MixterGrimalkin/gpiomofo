package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.gpiomofo.utility.Now;
import net.amarantha.gpiomofo.utility.TimeGuard;

public class FlashAndFade extends PixelTapeTarget {

    private RGB flashColour = new RGB(255,255,255);
    private RGB darkColour = new RGB(0,0,0);
    private RGB sparkColour = new RGB(150,150,150);
    int totalFlashes = 5;
    long flashDuration = 500;
    long darkDuration = 500;
    private int sparkSpacing = 8;
    private boolean reverseSpark;
    private int moveAmount = 3;
    private long moveInterval = 60;
    private boolean useSpark = true;
    private boolean fadeOut = true;
    private long fadeOutInterval = 200;
    private double intensityDelta = 0.1;

    int flashCount;
    boolean inFlash;
    long lastStart;
    private int offset;
    private long lastMove;
    private long lastChangedIntensity;
    private double intensity = 1.0;


    @Inject private Now now;

    @Inject private TimeGuard guard;

    @Override
    public void start() {
        super.start();
        lastStart = 0;
        intensity = 1.0;
        flashCount = 0;
        inFlash = false;
        guard.reset();
    }

    @Override
    protected void update() {

        if ( flashCount < totalFlashes ) {

            if (inFlash) {
                guard.every(flashDuration, "endFlash", ()->{
//                    System.out.println("End Flash " + this);
                    setAll(darkColour);
                    inFlash = false;
                    flashCount++;
                });
            } else {
                if ( guard.every(darkDuration, "startFlash", ()->{
//                    System.out.println("Start Flash " + this);
                    setAll(flashColour);
                    inFlash = true;
                })); else if ( useSpark ) {
                    guard.every(moveInterval, "drawSpark", ()->{
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
                    });
                }

            }
        } else if (fadeOut && intensity>0) {
            guard.every(fadeOutInterval, "fadeOut", ()->{
                setAll(flashColour.withBrightness(intensity));
                intensity -= intensityDelta;
            });
        }

    }

    public FlashAndFade setReverse(boolean reverse) {
        this.reverseSpark = reverse;
        return this;
    }

    public FlashAndFade setUseSpark(boolean useSpark) {
        this.useSpark = useSpark;
        return this;
    }

    public FlashAndFade setFadeOut(boolean fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public FlashAndFade setFlashColour(RGB flashColour) {
        this.flashColour = flashColour;
        return this;
    }

    public FlashAndFade setDarkColour(RGB darkColour) {
        this.darkColour = darkColour;
        return this;
    }

    public FlashAndFade setSparkColour(RGB sparkColour) {
        this.sparkColour = sparkColour;
        return this;
    }

}
