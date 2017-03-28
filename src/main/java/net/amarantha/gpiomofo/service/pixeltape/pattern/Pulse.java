package net.amarantha.gpiomofo.service.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.pixeltape.PixelTapeTarget;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.time.TimeGuard;

public class Pulse extends PixelTapeTarget {

    double intensity = 1.0;
    double intensityDelta = 0.3;
    long pulseInterval = 30;

    @Inject private TimeGuard guard;

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
