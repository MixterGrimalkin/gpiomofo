package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.pattern.*;
import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.utility.PropertyManager;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

public class GingerlineBriefingRoom extends Scenario {

    @Inject private PixelTapeController pixeltape;

    @Inject private PropertyManager props;

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

    public static final int WHOLE_TAPE = ALL_DOMES + PIPE_1_SIZE + PIPE_2_SIZE + PIPE_3_SIZE + PIPE_4_SIZE;

    private Trigger panicButton;
    private Trigger panicButtonHold;
    private Trigger blueBriefing;
    private Trigger greenBriefing;
    private Trigger blueCapsule;
    private Trigger greenCapsule;

    private Trigger httpBackground;
    private Trigger httpActivate;
    private Trigger httpStop;

    @Override
    public void setupTriggers() {

        panicButton =       triggers.gpio("Panic", 2, PULL_UP, false);
        panicButtonHold =   triggers.gpio("Panic-Hold", 2, PULL_UP, false).setHoldTime(1000);
        blueBriefing =      triggers.gpio("Briefing-Blue", 3, PULL_UP, false);
        greenBriefing =     triggers.gpio("Briefing-Green", 4, PULL_UP, false);
        blueCapsule =       triggers.gpio("Capsule-Blue", 5, PULL_UP, false);
        greenCapsule =      triggers.gpio("Capsule-Green", 6, PULL_UP, false);

        httpBackground =    triggers.http("background");
        httpActivate =      triggers.http("active");
        httpStop =          triggers.http("stop");

    }

    private Target stopPixelTape;
    private Target backgroundScene;
    private Target activationScene;

    private Target greenCap;
    private Target blueCap;
    private Target greenBR;
    private Target blueBR;

    private Target panicTarget;
    private Target panicPiTarget;

    @Override
    public void setupTargets() {

        stopPixelTape = targets.stopPixelTape();

        Target fadeToBlack =
            targets.pixelTape(FadeToBlack.class)
                .setFadeTime(1000)
                .setRefreshInterval(50)
                .init(DOME_1_START, WHOLE_TAPE);

        ////////////////
        // Background //
        ////////////////

        int backRed = props.getInt("backRed", 255);
        int backGreen = props.getInt("backGreen", 255);
        int backBlue = props.getInt("backBlue", 255);

        RGB backColour = new RGB(backRed,backGreen,backBlue);

        Target domesChase =
            targets.pixelTape(ChasePattern.class)
                .setColour(backColour)
                .setBlockWidth(50)
                .setMovement(5)
                .setRefreshInterval(40)
                .init(DOME_1_START, ALL_DOMES);

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

        RGB colour1 = new RGB(255, 10, 0);
        RGB colour2 = new RGB(255, 40, 0);
        RGB colour3 = new RGB(255, 10, 0);
        RGB colour4 = new RGB(255, 40, 0);
//        RGB colour1 = new RGB(255, 100, 0);
//        RGB colour2 = new RGB(100, 0, 255);
//        RGB colour3 = new RGB(50, 80, 255);
//        RGB colour4 = new RGB(255, 0, 100);

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


        int pipeActiveBar = 10;
        int pipeActiveSpace = 5;

        Target fastPipe1 =
            targets.pixelTape(SlidingBars.class)
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -10)
                .setColour(colour1)
                .setReverse(true)
                .setFadeInTime(5000)
                .init(PIPE_1_START, PIPE_1_SIZE);

        Target fastPipe2 =
            targets.pixelTape(SlidingBars.class)
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -10)
                .setColour(colour2)
                .setFadeInTime(5000)
                .init(PIPE_2_START, PIPE_2_SIZE);

        Target fastPipe3 =
            targets.pixelTape(SlidingBars.class)
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -10)
                .setColour(colour3)
                .setReverse(true)
                .setFadeInTime(5000)
                .init(PIPE_3_START, PIPE_3_SIZE);

        Target fastPipe4 =
            targets.pixelTape(SlidingBars.class)
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -10)
                .setColour(colour4)
                .setFadeInTime(5000)
                .init(PIPE_4_START, PIPE_4_SIZE);

        Target flashPipe1 =
            targets.pixelTape(FlashAndFade.class)
                .setReverse(true)
                .setSparkColour(colour1)
                .init(PIPE_1_START, PIPE_1_SIZE);

        Target flashPipe2 =
            targets.pixelTape(FlashAndFade.class)
                .setSparkColour(colour2)
                .init(PIPE_2_START, PIPE_2_SIZE);

        Target flashPipe3 =
            targets.pixelTape(FlashAndFade.class)
                .setReverse(true)
                .setSparkColour(colour3)
                .init(PIPE_3_START, PIPE_3_SIZE);

        Target flashPipe4 =
            targets.pixelTape(FlashAndFade.class)
                .setSparkColour(colour4)
                .init(PIPE_4_START, PIPE_4_SIZE);

        Target flashDome1 =
            targets.pixelTape(FlashAndFade.class)
                .setDarkColour(colour1)
                .setUseSpark(false)
                .setFadeOut(false)
                .init(DOME_1_START, DOME_SIZE);

        Target flashDome2 =
            targets.pixelTape(FlashAndFade.class)
                .setDarkColour(colour2)
                .setUseSpark(false)
                .setFadeOut(false)
                .init(DOME_2_START, DOME_SIZE);

        Target flashDome3 =
            targets.pixelTape(FlashAndFade.class)
                .setDarkColour(colour3)
                .setUseSpark(false)
                .setFadeOut(false)
                .init(DOME_3_START, DOME_SIZE);

        Target flashDome4 =
            targets.pixelTape(FlashAndFade.class)
                .setDarkColour(colour4)
                .setUseSpark(false)
                .setFadeOut(false)
                .init(DOME_4_START, DOME_SIZE);

        Target pipe1 =
                targets.pixelTape(PipePattern.class)
                        .setOnColour(colour1)
                        .setRefreshInterval(10)
                        .setReverse(true)
                        .init(PIPE_1_START,PIPE_1_SIZE);

        Target pipe2 =
                targets.pixelTape(PipePattern.class)
                        .setOnColour(colour2)
                        .setRefreshInterval(10)
                        .init(PIPE_2_START,PIPE_2_SIZE);

        Target pipe3 =
                targets.pixelTape(PipePattern.class)
                        .setOnColour(colour3)
                        .setRefreshInterval(10)
                        .setReverse(true)
                        .init(PIPE_3_START,PIPE_3_SIZE);

        Target pipe4 =
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

        String ip = props.getString("mediaServerIP", "192.168.42.100");
        int port = props.getInt("mediaServerOscPort", 7700);

        greenBR = targets.osc(new OscCommand(ip, port, "cue/1001/start", 255));
        blueBR = targets.osc(new OscCommand(ip, port, "cue/1002/start", 255));
        greenCap = targets.osc(new OscCommand(ip, port, "cue/1003/start", 255));
        blueCap = targets.osc(new OscCommand(ip, port, "cue/1004/start", 255));

        panicTarget = targets.osc(new OscCommand("192.168.42.100", 7700, "alarm/c0", 255));
        panicPiTarget = targets.http(
            new HttpCommand(POST, "192.168.42.105", 8001, "gpiomofo/trigger/panic-briefing/fire", "", "")
        );

        activationScene =
            targets.chain("Activation-Scene")

                .add(0, greenCap)

                // Fade to black
                .add(2000,  fadeToBlack)
                .add(0,     stopPixelTape)

                .add(0, pipe1)
                .add(0, spinDome1)
                .add(0, pipe2)
                .add(0, spinDome2)
                .add(0, pipe3)
                .add(0, spinDome3)
                .add(0, pipe4)
                .add(11000, spinDome4)

                .add(0, pipe1.cancel())
                .add(0, spinDome1.cancel())
                .add(0, domePulse1)
                .add(1500, pipePulse1)

                .add(0, pipe2.cancel())
                .add(0, spinDome2.cancel())
                .add(0, domePulse2)
                .add(1500, pipePulse2)

                .add(0, pipe3.cancel())
                .add(0, spinDome3.cancel())
                .add(0, domePulse3)
                .add(1500, pipePulse3)

                .add(0, pipe4.cancel())
                .add(0, spinDome4.cancel())
                .add(0, domePulse4)
                .add(3000, pipePulse4)

                .add(0, pipePulse1.cancel())
                .add(50, pipeWipe1)
                .add(0, pipePulse2.cancel())
                .add(50, pipeWipe2)
                .add(0, pipePulse3.cancel())
                .add(50, pipeWipe3)
                .add(0, pipePulse4.cancel())
                .add(500, pipeWipe4)

                // Fade to Black
                .add(3000,  fadeToBlack)
                .add(1000,  stopPixelTape)

                // Restart Background
                .add(0,     backgroundScene)

            .build().oneShot(true);

    }

    @Override
    public void setupLinks() {

        links
            .link(greenCapsule, activationScene)
            .link(blueCapsule, blueCap)
            .link(greenBriefing, greenBR)
            .link(blueBriefing, blueBR)
            .link(httpActivate,     activationScene)
            .link(httpBackground,   backgroundScene)
            .link(httpStop,         stopPixelTape)
            .link(panicButton,      panicTarget)
            .link(panicButtonHold,      panicPiTarget)
        ;

        links.lock(30000, activationScene);

        pixeltape
            .init(WHOLE_TAPE)
            .start();

        backgroundScene.activate();

    }

}
