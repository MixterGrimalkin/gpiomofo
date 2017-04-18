package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.service.pixeltape.pattern.SlidingBars;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.utils.colour.RGB;

public class PixelTestScenario extends Scenario {

    @Parameter("Colour") private RGB colour;
    @Parameter("PixelCount") private int pixelCount;

    @Override
    public void setup() {

        PixelTapeTarget target = targets.pixelTape(SlidingBars.class)
            .setColour(colour)
                .init(0, pixelCount);

        target.activate();

    }

}
