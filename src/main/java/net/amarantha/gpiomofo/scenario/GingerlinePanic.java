package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;

@PropertyGroup("Gingerline")
public class GingerlinePanic extends Scenario {

    public static final String URL_PANIC_BRIEFING = "panic-briefing";
    public static final String URL_PANIC_GAMESHOW = "panic-gameshow";
    public static final String URL_PANIC_UNDERWATER = "panic-underwater";
    public static final String URL_PANIC_BIKES = "panic-bikes";
    public static final String URL_PANIC_KITCHEN = "panic-kitchen";
    public static final String URL_PANIC_TOYBOX = "panic-toybox";

    @Property("ButtonHoldTime")         private int     holdTime;
    @Property("LightingServerIP")       private String  lightingIp;
    @Property("LightingServerOscPort")  private int     lightingPort;

    private Trigger panicReset;
    private Trigger panicResetHold;
    private Trigger briefingRoom;
    private Trigger gameShowRoom;
    private Trigger underwaterRoom;
    private Trigger bikeRoom;
    private Trigger kitchenRoom;
    private Trigger toyBoxRoom;

    private Target lightsOn;
    private Target alarm;

    @Override
    public void setup() {

        panicReset =     triggers.gpio("reset",      0, PULL_UP, false).setHoldTime(holdTime);
        panicResetHold = triggers.gpio("reset-hold", 0, PULL_UP, false).setHoldTime(5000);

        briefingRoom =   triggers.http(URL_PANIC_BRIEFING);
        gameShowRoom =   triggers.http(URL_PANIC_GAMESHOW);
        underwaterRoom = triggers.http(URL_PANIC_UNDERWATER);
        bikeRoom =       triggers.http(URL_PANIC_BIKES);
        kitchenRoom =    triggers.http(URL_PANIC_KITCHEN);
        toyBoxRoom =     triggers.http(URL_PANIC_TOYBOX);

        lightsOn = targets.osc(new OscCommand(lightingIp, lightingPort, "alarm/kitchenhold", 255));

        alarm = targets.audio("audio/alarm.mp3", true);//.followTrigger(false);

        links
            .link(briefingRoom, alarm)
            .link(gameShowRoom, alarm)
            .link(underwaterRoom, alarm)
            .link(bikeRoom, alarm)
            .link(kitchenRoom, alarm)
            .link(toyBoxRoom, alarm)
//            .link(panicReset, alarm.cancel())
        ;

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
