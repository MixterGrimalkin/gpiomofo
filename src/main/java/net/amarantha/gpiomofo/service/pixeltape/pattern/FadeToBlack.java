package net.amarantha.gpiomofo.service.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.pixeltape.PixelTape;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.utils.service.Service;

public class FadeToBlack extends PixelTapeTarget {

    @Inject private NeoPixel neoPixel;

    private double intensity;
    private long fadeTime = 1000;
    private double intensityDelta;

    @Override
    public void start() {
        super.start();
        intensity = 1.0;
        intensityDelta = 1.0 / (fadeTime / getRefreshInterval());
    }

    @Override
    public void stop() {
        super.stop();
        neoPixel.setMasterBrightness(1.0);
    }

    @Override
    protected void update() {
        if ( intensity<=0.0 ) {
            pixelTape.stopAll();
        } else {
            neoPixel.setMasterBrightness(intensity);
            intensity -= intensityDelta;
        }
    }

    public FadeToBlack setFadeTime(long fadeTime) {
        this.fadeTime = fadeTime;
        return this;
    }
}
