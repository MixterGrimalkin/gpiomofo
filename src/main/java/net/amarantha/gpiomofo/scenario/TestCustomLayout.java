package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.pixeltape.pattern.ChasePattern;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.utils.colour.RGB;

public class TestCustomLayout extends Scenario {

    @Inject private NeoPixel neoPixel;

    private Target pattern;

    @Override
    public void setup() {

        pattern = targets.pixelTape(ChasePattern.class)
                .setBlockWidth(1)
                .setMovement(1)
                .setColour(RGB.WHITE).setReverse(true).init(0, 7);

    }

    @Override
    public void startup() {
        pattern.activate();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
