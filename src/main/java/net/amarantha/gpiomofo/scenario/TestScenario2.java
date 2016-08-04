package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;

public class TestScenario2 extends Scenario {

    @Override
    public void setupTriggers() {
        triggers.gpio("Button", 0, PULL_DOWN, true);
        triggers.osc("Osc", 55000, "gpiomofo");
    }

    @Override
    public void setupTargets() {
        Target red = targets.gpio("Red", 1, true).clearDelay(2000L);
        Target blue = targets.gpio("Blue", 2, true).clearDelay(2000L);
        Target both = targets.chain("Both").add(null, red, blue).build().clearDelay(2000L);
        targets.queue("Queue", red, blue, both).clearDelay(2000L);
        targets.osc("Osc",
                new OscCommand("192.168.1.70", 53000, "hello", "one", "two"),
                new OscCommand("192.168.1.70", 53000, "goodbye", "three", "fout")
                );
    }

    @Override
    public void setupLinks() {
        links.link("Osc", "Queue").link("Button", "Osc");

    }
}
