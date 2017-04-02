package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.core.annotation.Named;
import net.amarantha.gpiomofo.core.scenario.Scenario;
import net.amarantha.gpiomofo.core.target.Target;
import net.amarantha.gpiomofo.core.trigger.Trigger;

public class ExampleScenario extends Scenario {

    @Named("TestTrigger") public Trigger testTrigger;
    @Named("TestTarget") public Target testTarget;

}
