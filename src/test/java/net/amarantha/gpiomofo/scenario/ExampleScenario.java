package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.colour.RGB;

public class ExampleScenario extends Scenario {

    @Parameter("Colour") private RGB colour;
    @Parameter("Style") private String style;

    @Named("TestTrigger") public Trigger testTrigger;
    @Named("TestTarget") public Target testTarget;

}
