package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.gpiomofo.utility.TimeGuard;
import net.amarantha.utils.colour.RGB;

public class Pulse extends PixelTapeTarget {

    double intensity = 1.0;
    double intensityDelta = 0.3;
    long pulseInterval = 30;

    @Inject
    TimeGuard guard;

    @Override
    protected void update() {

        guard.every(pulseInterval, "pulse", ()->{
            intensity += intensityDelta;
            if ( intensity >= 1.0 ) {
                intensity = 1.0;
                intensityDelta = -intensityDelta;
            } else if ( intensity <= 0.0 ) {
                intensity = 0.0;
                intensityDelta = -intensityDelta;
            }
            setAll(new RGB(255,255,255).withBrightness(intensity));
        });

    }

}
