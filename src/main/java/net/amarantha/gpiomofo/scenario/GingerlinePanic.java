package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.target.QueuedTarget;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static net.amarantha.gpiomofo.scenario.GingerLineSetup.*;

public class GingerlinePanic extends Scenario {

    private Trigger panicReset;
    private Trigger briefingRoom;
    private Trigger gameShowRoom;
    private Trigger underwaterRoom;
    private Trigger bikeRoom;
    private Trigger kitchenRoom;
    private Trigger toyBoxRoom;

    public static final String URL_PANIC_BRIEFING = "panic-briefing";
    public static final String URL_PANIC_GAMESHOW = "panic-gameshow";
    public static final String URL_PANIC_UNDERWATER = "panic-underwater";
    public static final String URL_PANIC_BIKES = "panic-bikes";
    public static final String URL_PANIC_KITCHEN = "panic-kitchen";
    public static final String URL_PANIC_TOYBOX = "panic-toybox";

    @Override
    public void setupTriggers() {

        panicReset =   triggers.http("reset");
        panicReset.onFire(s->{
            briefingRoom.fire(false);
            gameShowRoom.fire(false);
            underwaterRoom.fire(false);
            bikeRoom.fire(false);
            kitchenRoom.fire(false);
            toyBoxRoom.fire(false);
        });

        briefingRoom =   triggers.http(URL_PANIC_BRIEFING);
        gameShowRoom =   triggers.http(URL_PANIC_GAMESHOW);
        underwaterRoom = triggers.http(URL_PANIC_UNDERWATER);
        bikeRoom =       triggers.http(URL_PANIC_BIKES);
        kitchenRoom =    triggers.http(URL_PANIC_KITCHEN);
        toyBoxRoom =     triggers.http(URL_PANIC_TOYBOX);

    }

    @Override
    public void setupTargets() {

    }

    @Override
    public void setupLinks() {

    }

}
