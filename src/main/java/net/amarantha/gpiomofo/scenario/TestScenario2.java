package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.factory.Named;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;

public class TestScenario2 extends Scenario {

    @Named("Button") private Trigger button;
    @Named("LED") private Target led;

    @Override
    public void setupTriggers() {

        triggers.gpio("Button", 1, PULL_DOWN, true);

    }

    @Override
    public void setupTargets() {

        targets.gpio("LED", 0, null).oneShot(true);

    }

    @Override
    public void setupLinks() {

        links.link("Button", "LED");

    }

}
