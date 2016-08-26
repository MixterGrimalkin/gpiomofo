package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

public class GingerlineBikeRoom extends Scenario {

    private Trigger underwaterButton;
    private Trigger panicUnderwater;
    private Trigger panicUnderwaterHold;
    private Trigger panicBikes;
    private Trigger panicBikesHold;
    private Trigger panicKitchen;
    private Trigger panicKitchenHold;

    private Target underwaterControl;
    private Target panicUnderwaterLights;
    private Target panicUnderwaterPi;
    private Target panicBikesLights;
    private Target panicBikesPi;
    private Target panicKitchenLights;
    private Target panicKitchenPi;

    @Override
    public void setupTriggers() {

        underwaterButton =      triggers.gpio("Underwater-Button", 5, PULL_UP, false);
        panicUnderwater =       triggers.gpio("Panic-2", 2, PULL_UP, false);
        panicUnderwaterHold =   triggers.gpio("Panic-2-Hold", 2, PULL_UP, false).setHoldTime(1000);
        panicBikes =            triggers.gpio("Panic-3", 3, PULL_UP, false);
        panicBikesHold =        triggers.gpio("Panic-3-Hold", 3, PULL_UP, false).setHoldTime(1000);
        panicKitchen =          triggers.gpio("Panic-4", 4, PULL_UP, false);
        panicKitchenHold =      triggers.gpio("Panic-4-Hold", 4, PULL_UP, false).setHoldTime(1000);

    }

    @Override
    public void setupTargets() {

        String ipBen = "192.168.42.100";
        int portBen = 7700;

        underwaterControl = targets.osc(new OscCommand(ipBen, portBen, "alarm/c2slide", 255));

        panicUnderwaterLights = targets.osc(new OscCommand(ipBen, portBen, "alarm/c2", 255));
        panicUnderwaterPi = targets.http(
                new HttpCommand(POST, "192.168.42.105", 8001, "gpiomofo/trigger/panic-underwater/fire", "", "")
        );

        panicBikesLights = targets.osc(new OscCommand(ipBen, portBen, "alarm/c3", 255));
        panicBikesPi = targets.http(
                new HttpCommand(POST, "192.168.42.105", 8001, "gpiomofo/trigger/panic-bikes/fire", "", "")
        );

        panicKitchenLights = targets.osc(new OscCommand(ipBen, portBen, "alarm/c4"));
        panicKitchenPi = targets.http(
                new HttpCommand(POST, "192.168.42.105", 8001, "gpiomofo/trigger/panic-kitchen/fire", "", "")
        );

    }

    @Override
    public void setupLinks() {

        links
            .link(underwaterButton,     underwaterControl)
            .link(panicUnderwater,      panicUnderwaterLights)
            .link(panicUnderwaterHold,  panicUnderwaterPi)
            .link(panicBikes,           panicBikesLights)
            .link(panicBikesHold,       panicBikesPi)
            .link(panicKitchen,         panicKitchenLights)
            .link(panicKitchenHold,     panicKitchenPi)
        ;

    }

}