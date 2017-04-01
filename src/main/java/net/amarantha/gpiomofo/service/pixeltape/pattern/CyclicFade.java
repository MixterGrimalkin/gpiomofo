package net.amarantha.gpiomofo.service.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.pixeltape.PixelTapeTarget;
import net.amarantha.utils.time.TimeGuard;

public class CyclicFade extends PixelTapeTarget {

    @Inject private NeoPixel tape;
    @Inject private TimeGuard guard;

    private double min = 0.0;
    private double max = 1.0;
    private double delta = 0.1;

    private double currentBrightness;
    private double currentDelta;

    @Override
    public void start() {
        super.start();
        currentBrightness = tape.getMasterBrightness();
        currentDelta = delta;
    }

    @Override
    public void stop() {
        super.stop();
        tape.setMasterBrightness(max);
    }

    @Override
    protected void update() {

        guard.every(100, "cyclicfade", ()->{
            if ( currentBrightness>max ) {
                currentBrightness = max;
                currentDelta = -delta;
            }
            if ( currentBrightness < min) {
                currentBrightness = min;
                currentDelta = delta;
            }
            currentBrightness += currentDelta;
            tape.setMasterBrightness(currentBrightness);
        });

    }

    public CyclicFade setMin(double min) {
        this.min = min;
        return this;
    }

    public CyclicFade setMax(double max) {
        this.max = max;
        return this;
    }

    public CyclicFade setDelta(double delta) {
        this.delta = delta;
        return this;
    }
}
