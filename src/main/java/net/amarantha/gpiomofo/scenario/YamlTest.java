package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.factory.Named;
import net.amarantha.gpiomofo.target.Target;

public class YamlTest extends Scenario {

    @Named("Lamp2") private Target lamp;

    @Override
    public void start() {

        lamp.activate();

    }

}
