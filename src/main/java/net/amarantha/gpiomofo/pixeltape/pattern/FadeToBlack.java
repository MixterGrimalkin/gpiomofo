package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.target.PixelTapeTarget;

public class FadeToBlack extends PixelTapeTarget {

    @Inject private NeoPixel neoPixel;
    @Inject private PixelTapeController pixelTapeController;

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
            pixelTapeController.stopAll();
        } else {
//            for (int i = 0; i < getPixelCount(); i++) {
//                RGB colour = getPixel(i);
//                if ( colour==null ) {
//                    colour = new RGB(255,0,0);
//                }
//                setPixel(i, colour.withBrightness(intensity));
//            }
            neoPixel.setMasterBrightness(intensity);
            intensity -= intensityDelta;
        }
    }

    public FadeToBlack setFadeTime(long fadeTime) {
        this.fadeTime = fadeTime;
        return this;
    }
}
