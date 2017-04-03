package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

public class ExampleScenario extends Scenario {

    @Named("TestTrigger") public Trigger testTrigger;
    @Named("TestTarget") public Target testTarget;

}
