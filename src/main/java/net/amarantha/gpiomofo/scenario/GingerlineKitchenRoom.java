package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static net.amarantha.gpiomofo.scenario.GingerlinePanic.*;

public class GingerlineKitchenRoom extends Scenario {

    public static final String KITCHEN_ROOM_IP = "192.168.1.84";

    private Trigger panicButton;
    private Target panicTarget;

    @Override
    public void setupTriggers() {
        panicButton = triggers.gpio("Panic", 0, PULL_DOWN, true);
    }

    @Override
    public void setupTargets() {
        panicTarget = targets.osc("Panic", new OscCommand(PANIC_IP, OSC_PORT, PANIC_KITCHEN_ROOM));
    }

    @Override
    public void setupLinks() {
        links.link(panicButton,   panicTarget);
    }

}
