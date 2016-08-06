package net.amarantha.gpiomofo.scenario;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;

public class TestScenario2 extends Scenario {

    @Override
    public void setupTriggers() {
        triggers.gpio("Button", 0, PULL_DOWN, true);
    }

    @Override
    public void setupTargets() {
        targets.gpio("Red", 1, true).clearDelay(1000L).followTrigger(false);
    }

    @Override
    public void setupLinks() {
        links.link("Button", "Red");
    }

}
