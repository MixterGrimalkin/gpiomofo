package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static net.amarantha.gpiomofo.scenario.GingerlinePanic.OSC_PORT;
import static net.amarantha.gpiomofo.scenario.GingerlinePanic.PANIC_BRIEFING_ROOM;
import static net.amarantha.gpiomofo.scenario.GingerlinePanic.PANIC_IP;

public class GingerlineBriefingRoom extends Scenario {

    public static final String BRIEFING_ROOM_IP = "192.168.1.80";

    private Trigger panicButton;
    private Trigger buttonA1;
    private Trigger buttonA2;
    private Trigger buttonB1;
    private Trigger buttonB2;

    private Target panicTarget;
    private Target oscA1;
    private Target oscA2;
    private Target oscB1;
    private Target oscB2;
    private Target pixelTape;

    @Override
    public void setupTriggers() {
        panicButton = triggers.gpio("Panic", 0, PULL_DOWN, true);
    }

    @Override
    public void setupTargets() {
        panicTarget = targets.osc("Panic", new OscCommand(PANIC_IP, OSC_PORT, PANIC_BRIEFING_ROOM));
    }

    @Override
    public void setupLinks() {

        links.
            link(panicButton,   panicTarget)
        ;

    }
}
