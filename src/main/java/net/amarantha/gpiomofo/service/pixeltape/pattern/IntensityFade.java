package net.amarantha.gpiomofo.service.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.time.TimeGuard;

public class IntensityFade extends PixelTapeTarget {

    @Inject private TimeGuard guard;

    @Inject private NeoPixel tape;

    private double intensity = 1.0;
    private double intensityDelta = 0.1;

    private double max = 1.0;
    private double min = 0.0;

    private int pause;

    private int minPause;
    private int maxPause;

    @Override
    public void start() {
        super.start();
        intensity = min;
        intensityDelta = Math.abs(intensityDelta);
        pause = 0;
    }

    @Override
    public void stop() {
        super.stop();
        tape.setMasterBrightness(1.0);
    }

    @Override
    protected void update() {

        if ( pause<=0 ) {

            guard.every(100, this, () -> {
                intensity += intensityDelta;
                if (intensity >= max) {
                    intensity = max;
                    intensityDelta = -intensityDelta;
                    pause = maxPause;
                }
                if (intensity < min) {
                    intensity = min;
                    intensityDelta = -intensityDelta;
                    pause = minPause;
                }
                tape.setMasterBrightness(intensity);
            });
        } else {
            pause--;
        }

    }

    public IntensityFade setMax(double max) {
        this.max = max;
        return this;
    }

    public IntensityFade setMin(double min) {
        this.min = min;
        return this;
    }

    public IntensityFade setIntensityDelta(double intensityDelta) {
        this.intensityDelta = intensityDelta;
        return this;
    }

    public IntensityFade setMinPause(int minPause) {
        this.minPause = minPause;
        return this;
    }

    public IntensityFade setMaxPause(int maxPause) {
        this.maxPause = maxPause;
        return this;
    }
}
