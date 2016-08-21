package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static net.amarantha.gpiomofo.scenario.GingerlineSetup.*;

public class GingerlineKitchenRoom extends Scenario {

    private Trigger panicButton;
    private Target panicTarget;

    @Override
    public void setupTriggers() {

        panicButton = triggers.gpio("Panic", 0, PULL_DOWN, true);

    }

    @Override
    public void setupTargets() {

        panicTarget = targets.osc("Panic", new OscCommand(PANIC_IP, PANIC_OSC_PORT, PANIC_KITCHEN_ROOM));

    }

    @Override
    public void setupLinks() {

        links.link(panicButton,   panicTarget);

    }

}
