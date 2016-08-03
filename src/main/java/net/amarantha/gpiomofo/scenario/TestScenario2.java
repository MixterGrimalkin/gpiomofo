package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.target.Target;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;

public class TestScenario2 extends Scenario {

    @Override
    public void setupTriggers() {
        triggers.gpio("Button", 0, PULL_DOWN, true);
    }

    @Override
    public void setupTargets() {
        Target red = targets.gpio("Red", 1, true);
        Target blue = targets.gpio("Blue", 2, true);
        Target both = targets.chain("Both").add(red, blue).build();
        targets.queue("Queue", red, blue, both);
    }

    @Override
    public void setupLinks() {
        links.link("Button", "Queue");
    }
}
