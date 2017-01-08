package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.properties.Property;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.scenario.GingerlinePanic.URL_PANIC_GAMESHOW;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

public class GingerlineGameShowRoom extends Scenario {

    @Property("PanicIP")                private String  panicIp;
    @Property("PanicPort")              private int     panicPort;
    @Property("ButtonHoldTime")         public int     holdTime;
    @Property("LightingServerIP")       private String  lightingIp;
    @Property("LightingServerOscPort")  private int     lightingPort;
    @Property("MediaServerIP")          private String  mediaIp;
    @Property("MediaServerOscPort")     private int     mediaPort;

    private Trigger panicButton;
    private Trigger panicButtonHold;
    private Trigger podiumButton1;
    private Trigger podiumButton2;
    private Trigger podiumButton3;
    private Trigger podiumButton4;
    private Trigger effectButton01;
    private Trigger effectButton02;
    private Trigger effectButton03;
    private Trigger effectButton04;
    private Trigger effectButton05;

    @Override
    public void setupTriggers() {

        panicButton =       triggers.gpio("Panic",      0, PULL_UP, false);
        panicButtonHold =   triggers.gpio("Panic-Hold", 0, PULL_UP, false).setHoldTime(1000);

        podiumButton1 =     triggers.gpio("Podium-1",   2, PULL_UP, true).setHoldTime(holdTime);
        podiumButton2 =     triggers.gpio("Podium-2",   3, PULL_UP, true).setHoldTime(holdTime);
        podiumButton3 =     triggers.gpio("Podium-3",   4, PULL_UP, true).setHoldTime(holdTime);
        podiumButton4 =     triggers.gpio("Podium-4",   5, PULL_UP, true).setHoldTime(holdTime);

        effectButton01 =    triggers.gpio("FX01",       6, PULL_UP, false).setHoldTime(holdTime);
        effectButton02 =    triggers.gpio("FX02",       7, PULL_UP, false).setHoldTime(holdTime);
        effectButton03 =    triggers.gpio("FX03",       8, PULL_UP, false).setHoldTime(holdTime);
        effectButton04 =    triggers.gpio("FX04",       9, PULL_UP, false).setHoldTime(holdTime);
        effectButton05 =    triggers.gpio("FX05",       10, PULL_UP, false).setHoldTime(holdTime);

    }

    private Target panicTarget;
    private Target panicHoldTarget;
    private Target podiumTarget1;
    private Target podiumTarget2;
    private Target podiumTarget3;
    private Target podiumTarget4;
    private Target effectTarget01;
    private Target effectTarget02;
    private Target effectTarget03;
    private Target effectTarget04;
    private Target effectTarget05;

    @Override
    public void setupTargets() {

        panicTarget =       targets.osc(new OscCommand(lightingIp, lightingPort, "alarm/c1", 255));
        panicHoldTarget =   targets.http(new HttpCommand(POST, panicIp, panicPort, "gpiomofo/trigger", URL_PANIC_GAMESHOW+"/fire", ""));

        podiumTarget1 =     targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1101/start", 255));
        podiumTarget2 =     targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1102/start", 255));
        podiumTarget3 =     targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1103/start", 255));
        podiumTarget4 =     targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1104/start", 255));

        effectTarget01 =    targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1105/start", 255));
        effectTarget02 =    targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1106/start", 255));
        effectTarget03 =    targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1107/start", 255));
        effectTarget04 =    targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1108/start", 255));
        effectTarget05 =    targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1109/start", 255));

    }

    @Override
    public void setupLinks() {

        links
            .link(panicButton,      panicTarget)
            .link(panicButtonHold,  panicHoldTarget)

            .link(podiumButton1,    podiumTarget1)
            .link(podiumButton2,    podiumTarget2)
            .link(podiumButton3,    podiumTarget3)
            .link(podiumButton4,    podiumTarget4)
            .lock(2000, podiumTarget1, podiumTarget2, podiumTarget3, podiumTarget4)

            .link(effectButton01,   effectTarget01)
            .link(effectButton02,   effectTarget02)
            .link(effectButton03,   effectTarget03)
            .link(effectButton04,   effectTarget04)
            .link(effectButton05,   effectTarget05)
        ;

    }

}
