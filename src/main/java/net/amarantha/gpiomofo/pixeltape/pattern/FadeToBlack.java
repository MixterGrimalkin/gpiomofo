package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTape;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;

public class FadeToBlack extends PixelTapePattern {

    @Inject private PixelTapeController pixelTape;

    private double intensity;
    private long fadeTime = 1000;
    private double intensityDelta;

    private RGB[] lastPattern;

    @Override
    public void start() {
        pixelTape.stopAll(false);
        super.start();
        intensity = 1.0;
        intensityDelta = 1.0 / (fadeTime / getRefreshInterval());
    }

    @Override
    protected void update() {
        if ( intensity<=0.0 ) {
            stop();
        } else {
            for (int i = 0; i < getPixelCount(); i++) {
                RGB colour = getPixel(i);
                if ( colour==null ) {
                    colour = new RGB(255,0,0);
                }
                setPixel(i, colour.withBrightness(intensity));
            }
            intensity -= intensityDelta;
        }
    }

    public FadeToBlack setFadeTime(long fadeTime) {
        this.fadeTime = fadeTime;
        return this;
    }
}
