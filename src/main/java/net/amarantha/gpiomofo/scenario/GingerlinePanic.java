package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.utility.GpioMofoProperties;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

public class GingerlinePanic extends Scenario {

    @Inject private GpioMofoProperties props;

    public static final String PANIC_IP = "192.168.42.105";
    public static final int PANIC_PORT = 8001;

    public static final HttpCommand PANIC = new HttpCommand(POST, PANIC_IP, PANIC_PORT, "gpiomofo/trigger", "", "");

    public static final String URL_PANIC_BRIEFING = "panic-briefing";
    public static final String URL_PANIC_GAMESHOW = "panic-gameshow";
    public static final String URL_PANIC_UNDERWATER = "panic-underwater";
    public static final String URL_PANIC_BIKES = "panic-bikes";
    public static final String URL_PANIC_KITCHEN = "panic-kitchen";
    public static final String URL_PANIC_TOYBOX = "panic-toybox";

    private Trigger panicReset;
    private Trigger panicResetHold;
    private Trigger briefingRoom;
    private Trigger gameShowRoom;
    private Trigger underwaterRoom;
    private Trigger bikeRoom;
    private Trigger kitchenRoom;
    private Trigger toyBoxRoom;

    @Override
    public void setupTriggers() {

        panicReset =     triggers.gpio("reset",      0, PULL_UP, false);
        panicResetHold = triggers.gpio("reset-hold", 0, PULL_UP, false).setHoldTime(5000);

        briefingRoom =   triggers.http(URL_PANIC_BRIEFING);
        gameShowRoom =   triggers.http(URL_PANIC_GAMESHOW);
        underwaterRoom = triggers.http(URL_PANIC_UNDERWATER);
        bikeRoom =       triggers.http(URL_PANIC_BIKES);
        kitchenRoom =    triggers.http(URL_PANIC_KITCHEN);
        toyBoxRoom =     triggers.http(URL_PANIC_TOYBOX);

    }

    private Target lightsOn;

    @Override
    public void setupTargets() {

        lightsOn = targets.osc(new OscCommand(props.lightingIp(), props.lightingOscPort(), "alarm/kitchenhold", 255));

    }

    @Override
    public void setupLinks() {

        panicReset.onFire(s->{
            briefingRoom.fire(false);
            gameShowRoom.fire(false);
            underwaterRoom.fire(false);
            bikeRoom.fire(false);
            kitchenRoom.fire(false);
            toyBoxRoom.fire(false);
        });

        links.link(panicResetHold, lightsOn);

    }

}
