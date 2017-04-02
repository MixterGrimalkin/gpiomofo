package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.core.annotation.Named;
import net.amarantha.gpiomofo.core.scenario.Scenario;
import net.amarantha.gpiomofo.core.target.Target;

public class YamlTest extends Scenario {

    @Named("Lamp2") private Target lamp;

    @Override
    protected void startup() {
        lamp.activate();
    }

}
