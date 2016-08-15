package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.ChasePattern;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.UltrasonicSensor;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static net.amarantha.gpiomofo.scenario.GingerLineSetup.*;

public class GingerlineBikeRoom extends Scenario {

    private Trigger panicButton;
    private Target panicTarget;

    @Inject private ChasePattern pattern;
    @Inject private UltrasonicSensor sensor;

    @Override
    public void setupTriggers() {

        panicButton = triggers.gpio("Panic", 3, PULL_DOWN, true);

    }

    @Override
    public void setupTargets() {

        panicTarget = targets.osc("Panic", new OscCommand(PANIC_IP, PANIC_OSC_PORT, PANIC_BIKE_ROOM));

    }

    @Override
    public void setupLinks() {

        links.link(panicButton,   panicTarget);

        pattern
            .setBlockWidth(30)
            .setMovement(10)
            .setMinColour(30, 10, 255)
            .setMaxColour(200, 100, 10)
            .setDelayRange(10, 50)
//            .setBounce(true)
            .setPixelCount(150)
        .start();

        sensor.onReadSensor((value) -> {
            pattern.setSpeed(value);
            pattern.setIntensity(value);
//            pattern.setBlockWidth(10+(int)Math.round(value*19));
//            pattern.setMovement(1+(int)Math.round(value*4));
        }).start();

    }

    @Override
    public void stop() {
        pattern.stop();
    }
}
