package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.pixeltape.pattern.*;
import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.Property;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.scenario.GingerlinePanic.URL_PANIC_BRIEFING;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

public class GingerlineBriefingRoom extends Scenario {

    @Property("PanicIP")                private String  panicIp;
    @Property("PanicPort")              private int     panicPort;
    @Property("ButtonHoldTime")         private int     holdTime;
    @Property("LightingServerIP")       private String  lightingIp;
    @Property("LightingServerOscPort")  private int     lightingPort;
    @Property("MediaServerIP")          private String  mediaIp;
    @Property("MediaServerOscPort")     private int     mediaPort;
    @Property("C0-BackgroundColour")    private RGB     backColour;
    @Property("C0-Colour1")             private RGB     colour1;
    @Property("C0-Colour2")             private RGB     colour2;
    @Property("C0-Colour3")             private RGB     colour3;
    @Property("C0-Colour4")             private RGB     colour4;

    private Trigger buttonRed;
    private Trigger buttonRedHold;
    private Trigger buttonBriefingGreen;
    private Trigger buttonBriefingBlue;
    private Trigger buttonCapsuleGreen;
    private Trigger buttonCapsuleBlue;

    private Trigger httpBackground;
    private Trigger httpActivate;
    private Trigger httpStop;

    @Override
    public void setupTriggers() {

        buttonRed =             triggers.gpio("Panic",          2, PULL_UP, false);
        buttonRedHold =         triggers.gpio("Panic-Hold",     2, PULL_UP, false).setHoldTime(1000);

        buttonBriefingGreen =   triggers.gpio("Briefing-Green", 4, PULL_UP, false).setHoldTime(holdTime);
        buttonBriefingBlue =    triggers.gpio("Briefing-Blue",  3, PULL_UP, false).setHoldTime(holdTime);
        buttonCapsuleGreen =    triggers.gpio("Capsule-Green",  6, PULL_UP, false).setHoldTime(holdTime);
        buttonCapsuleBlue =     triggers.gpio("Capsule-Blue",   5, PULL_UP, false).setHoldTime(holdTime);

        httpBackground =        triggers.http("background");
        httpActivate =          triggers.http("active");
        httpStop =              triggers.http("stop");

    }

    private Target stopPixelTape;
    private Target backgroundScene;
    private Target activationScene;

    private Target briefingGreen;
    private Target briefingBlue;
    private Target capsuleGreen;
    private Target capsuleBlue;

    private Target panicLights;
    private Target panicMonitor;

    @Override
    public void setupTargets() {

        briefingGreen =     targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1001/start", 255));
        briefingBlue =      targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1002/start", 255));
        capsuleGreen =      targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1003/start", 255));
        capsuleBlue =       targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1004/start", 255));

        panicLights =       targets.osc(new OscCommand(lightingIp, lightingPort, "alarm/c0", 255));
        panicMonitor =      targets.http(new HttpCommand(POST, panicIp, panicPort, "gpiomofo/trigger", URL_PANIC_BRIEFING+"/fire", ""));

        ////////////////
        // Pixel Tape //
        ////////////////

        stopPixelTape = targets.stopPixelTape();

        Target fadeToBlack =
            targets.pixelTape(FadeToBlack.class)
                .setFadeTime(1000)
                .setRefreshInterval(50)
                .init(DOME_1_START, WHOLE_TAPE);

        ////////////////
        // Background //
        ////////////////

        int domeRefresh = 300;
        int pipeRefresh = 500;

        Target slowDome1 =
            targets.pixelTape(SlidingBars.class)
                    .setColour(backColour.withBrightness(0.75))
                    .setFadeInTime(2000)
                    .setBarChange(5, 12, 1)
                    .setRefreshInterval(domeRefresh)
                    .init(DOME_1_START, DOME_SIZE);

        Target slowDome2 =
            targets.pixelTape(SlidingBars.class)
                    .setColour(backColour.withBrightness(0.75))
                    .setFadeInTime(2000)
                    .setBarChange(5, 12, 1)
                    .setRefreshInterval(domeRefresh)
                    .init(DOME_2_START, DOME_SIZE);

        Target slowDome3 =
            targets.pixelTape(SlidingBars.class)
                    .setColour(backColour.withBrightness(0.75))
                    .setFadeInTime(2000)
                    .setBarChange(5, 12, 1)
                    .setRefreshInterval(domeRefresh)
                    .init(DOME_3_START, DOME_SIZE);

        Target slowDome4 =
            targets.pixelTape(SlidingBars.class)
                    .setColour(backColour.withBrightness(0.75))
                    .setFadeInTime(2000)
                    .setBarChange(5, 12, 1)
                    .setRefreshInterval(domeRefresh)
                    .init(DOME_4_START, DOME_SIZE);

        int barSpace = 8;

        Target slowPipe1 =
            targets.pixelTape(SlidingBars.class)
                .setColour(backColour)
                .setFadeInTime(2000)
                .setSpaceSize(barSpace)
                .setReverse(true)
                .setBarChange(6, 13, 2)
                .setRefreshInterval(pipeRefresh)
                .init(PIPE_1_START, PIPE_1_SIZE);

        Target slowPipe2 =
            targets.pixelTape(SlidingBars.class)
                .setColour(backColour)
                .setFadeInTime(2000)
                .setSpaceSize(barSpace)
                .setBarChange(5, 12, 2)
                .setRefreshInterval(pipeRefresh)
                .init(PIPE_2_START, PIPE_2_SIZE);

        Target slowPipe3 =
            targets.pixelTape(SlidingBars.class)
                .setColour(backColour)
                .setFadeInTime(2000)
                .setSpaceSize(barSpace)
                .setBarChange(4, 11, 2)
                .setRefreshInterval(pipeRefresh)
                .setReverse(true)
                .init(PIPE_3_START, PIPE_3_SIZE);

        Target slowPipe4 =
            targets.pixelTape(SlidingBars.class)
                .setColour(backColour)
                .setFadeInTime(2000)
                .setSpaceSize(barSpace)
                .setBarChange(7, 11, 2)
                .setRefreshInterval(pipeRefresh)
                .init(PIPE_4_START, PIPE_4_SIZE);

        backgroundScene =
            targets.chain("Background-Scene")
                .add(stopPixelTape)
                .add(slowDome1)
                .add(slowDome2)
                .add(slowDome3)
                .add(slowDome4)
                .add(slowPipe1)
                .add(slowPipe2)
                .add(slowPipe3)
                .add(slowPipe4)
                .build().oneShot(true);

        //////////////
        // Activate //
        //////////////

        Target spinDome1 =
            targets.pixelTape(SlidingBars.class)
                .setRefreshRange(400, 90, -20)
                .setColour(colour1)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_1_START, DOME_SIZE);

        Target spinDome2 =
            targets.pixelTape(SlidingBars.class)
                .setRefreshRange(400, 90, -20)
                .setColour(colour2)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_2_START, DOME_SIZE);

        Target spinDome3 =
            targets.pixelTape(SlidingBars.class)
                .setRefreshRange(400, 90, -20)
                .setColour(colour3)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_3_START, DOME_SIZE);

        Target spinDome4 =
            targets.pixelTape(SlidingBars.class)
                .setRefreshRange(400, 90, -20)
                .setColour(colour4)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_4_START, DOME_SIZE);

        Target fastPipe1 =
                targets.pixelTape(PipePattern.class)
                        .setOnColour(colour1)
                        .setRefreshInterval(10)
                        .setReverse(true)
                        .init(PIPE_1_START,PIPE_1_SIZE);

        Target fastPipe2 =
                targets.pixelTape(PipePattern.class)
                        .setOnColour(colour2)
                        .setRefreshInterval(10)
                        .init(PIPE_2_START,PIPE_2_SIZE);

        Target fastPipe3 =
                targets.pixelTape(PipePattern.class)
                        .setOnColour(colour3)
                        .setRefreshInterval(10)
                        .setReverse(true)
                        .init(PIPE_3_START,PIPE_3_SIZE);

        Target fastPipe4 =
                targets.pixelTape(PipePattern.class)
                        .setOnColour(colour4)
                        .setRefreshInterval(10)
                        .init(PIPE_4_START,PIPE_4_SIZE);

        Target pipePulse1 =
                targets.pixelTape(Pulse.class)
                .setRefreshInterval(10)
                .init(PIPE_1_START, PIPE_1_SIZE);
        Target pipePulse2 =
                targets.pixelTape(Pulse.class)
                .setRefreshInterval(10)
                .init(PIPE_2_START, PIPE_2_SIZE);
        Target pipePulse3 =
                targets.pixelTape(Pulse.class)
                .setRefreshInterval(10)
                .init(PIPE_3_START, PIPE_3_SIZE);
        Target pipePulse4 =
                targets.pixelTape(Pulse.class)
                .setRefreshInterval(10)
                .init(PIPE_4_START, PIPE_4_SIZE);

        Target domePulse1 =
                targets.pixelTape(Pulse.class)
                        .setRefreshInterval(10)
                        .init(DOME_1_START, DOME_SIZE);
        Target domePulse2 =
                targets.pixelTape(Pulse.class)
                        .setRefreshInterval(10)
                        .init(DOME_2_START, DOME_SIZE);
        Target domePulse3 =
                targets.pixelTape(Pulse.class)
                        .setRefreshInterval(10)
                        .init(DOME_3_START, DOME_SIZE);
        Target domePulse4 =
                targets.pixelTape(Pulse.class)
                        .setRefreshInterval(10)
                        .init(DOME_4_START, DOME_SIZE);

        Target pipeWipe1 =
            targets.pixelTape(Wipe.class)
                .setRefreshInterval(10)
                .setReverse(true)
                .init(PIPE_1_START, PIPE_1_SIZE);
        Target pipeWipe2 =
            targets.pixelTape(Wipe.class)
                .setRefreshInterval(10)
                .init(PIPE_2_START, PIPE_2_SIZE);
        Target pipeWipe3 =
            targets.pixelTape(Wipe.class)
                .setRefreshInterval(10)
                .setReverse(true)
                .init(PIPE_3_START, PIPE_3_SIZE);
        Target pipeWipe4 =
            targets.pixelTape(Wipe.class)
                .setRefreshInterval(10)
                .init(PIPE_4_START, PIPE_4_SIZE);

        activationScene =
            targets.chain("Activation-Scene")

                .add(0,     capsuleGreen)

                .add(2000,  fadeToBlack)
                .add(0,     stopPixelTape)

                .add(0,     fastPipe1)
                .add(0,     spinDome1)
                .add(0,     fastPipe2)
                .add(0,     spinDome2)
                .add(0,     fastPipe3)
                .add(0,     spinDome3)
                .add(0,     fastPipe4)
                .add(11000, spinDome4)

                .add(0,     fastPipe1.cancel())
                .add(0,     spinDome1.cancel())
                .add(0,     domePulse1)
                .add(1500,  pipePulse1)

                .add(0,     fastPipe2.cancel())
                .add(0,     spinDome2.cancel())
                .add(0,     domePulse2)
                .add(1500,  pipePulse2)

                .add(0,     fastPipe3.cancel())
                .add(0,     spinDome3.cancel())
                .add(0,     domePulse3)
                .add(1500,  pipePulse3)

                .add(0,     fastPipe4.cancel())
                .add(0,     spinDome4.cancel())
                .add(0,     domePulse4)
                .add(3000,  pipePulse4)

                .add(0,     pipePulse1.cancel())
                .add(50,    pipeWipe1)
                .add(0,     pipePulse2.cancel())
                .add(50,    pipeWipe2)
                .add(0,     pipePulse3.cancel())
                .add(50,    pipeWipe3)
                .add(0,     pipePulse4.cancel())
                .add(500,   pipeWipe4)

                .add(3000,  fadeToBlack)
                .add(1000,  stopPixelTape)

                .add(0,     backgroundScene)

            .build().oneShot(true);

    }

    @Override
    public void setupLinks() {

        links
            .link(buttonRed,            panicLights)
            .link(buttonRedHold,        panicMonitor)

            .link(buttonCapsuleGreen,   activationScene)
            .link(buttonCapsuleBlue,    capsuleBlue)
            .link(buttonBriefingGreen,  briefingGreen)
            .link(buttonBriefingBlue,   briefingBlue)

            .link(httpActivate,         activationScene)
            .link(httpBackground,       backgroundScene)
            .link(httpStop,             stopPixelTape)
        ;

        links.lock(30000, activationScene);

    }

    @Override
    public void start() {

        backgroundScene.activate();

    }

    public static final int DOME_SIZE = 47;
    public static final int ALL_DOMES = DOME_SIZE * 4;

    public static final int DOME_1_START = 0;
    public static final int DOME_2_START = DOME_SIZE;
    public static final int DOME_3_START = 2 * DOME_SIZE;
    public static final int DOME_4_START = 3 * DOME_SIZE;

    public static final int PIPE_1_SIZE = 23 + 63;
    public static final int PIPE_2_SIZE = 60 + 27;
    public static final int PIPE_3_SIZE = 26 + 59;
    public static final int PIPE_4_SIZE = 63 + 29;

    public static final int PIPE_4_START = ALL_DOMES;
    public static final int PIPE_3_START = PIPE_4_START + PIPE_4_SIZE;
    public static final int PIPE_2_START = PIPE_3_START + PIPE_3_SIZE;
    public static final int PIPE_1_START = PIPE_2_START + PIPE_2_SIZE;

    public static final int WHOLE_TAPE = PIPE_1_START + PIPE_1_SIZE;

}
