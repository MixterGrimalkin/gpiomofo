package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.target.QueuedTarget;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static net.amarantha.gpiomofo.scenario.GingerLineSetup.*;

public class GingerlinePanic extends Scenario {

    private Trigger resetButton;
    private Trigger briefingRoom;
    private Trigger gameShowRoom;
    private Trigger underwaterRoom;
    private Trigger bikeRoom;
    private Trigger kitchenRoom;
    private Trigger toyBoxRoom;

    private Target alarm;
    private Target light1;
    private Target light2;
    private Target light3;
    private Target light4;
    private Target light5;
    private Target light6;
    private QueuedTarget reset;
    private Target resetQueue;

    @Override
    public void setupTriggers() {

        resetButton =    triggers.gpio(0, PULL_DOWN, true);
        briefingRoom =   triggers.osc(PANIC_OSC_PORT, PANIC_BRIEFING_ROOM);
        gameShowRoom =   triggers.osc(PANIC_OSC_PORT, PANIC_GAME_SHOW);
        underwaterRoom = triggers.osc(PANIC_OSC_PORT, PANIC_UNDERWATER);
        bikeRoom =       triggers.osc(PANIC_OSC_PORT, PANIC_BIKE_ROOM);
        kitchenRoom =    triggers.osc(PANIC_OSC_PORT, PANIC_KITCHEN_ROOM);
        toyBoxRoom =     triggers.osc(PANIC_OSC_PORT, PANIC_TOY_BOX);

    }

    @Override
    public void setupTargets() {

        alarm =     targets.audio("audio/alarm.mp3").loop(true).followTrigger(false);
        light1 =    gpioChain(1, 200);
        light2 =    gpioChain(2, 200);
        light3 =    gpioChain(3, 200);
        light4 =    gpioChain(4, 200);
        light5 =    gpioChain(5, 200);
        light6 =    gpioChain(6, 200);
        reset =     targets.queue("Reset",
                        targets.cancel(alarm),
                        targets.chain()
                            .add(targets.cancel(light1))
                            .add(targets.cancel(light2))
                            .add(targets.cancel(light3))
                            .add(targets.cancel(light4))
                            .add(targets.cancel(light5))
                            .add(targets.cancel(light6))
                        .build()
                    );
        resetQueue = targets.queueReset(reset);

    }

    @Override
    public void setupLinks() {

        links
            .link(briefingRoom,    alarm, light1, resetQueue)
            .link(gameShowRoom,    alarm, light2, resetQueue)
            .link(underwaterRoom,  alarm, light3, resetQueue)
            .link(bikeRoom,        alarm, light4, resetQueue)
            .link(kitchenRoom,     alarm, light5, resetQueue)
            .link(toyBoxRoom,      alarm, light6, resetQueue)
            .link(resetButton,     reset)
        ;

    }

    private Target gpioChain(int pin, int delay) {
        return targets.chain()
            .add(delay, targets.gpio(pin, true).oneShot(true))
            .add(delay, targets.gpio(pin, false).oneShot(true))
        .build().repeat(true, targets.gpio(pin, false));
    }

}
