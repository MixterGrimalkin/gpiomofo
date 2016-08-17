package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.pattern.ChasePattern;
import net.amarantha.gpiomofo.pixeltape.pattern.FlashAndFade;
import net.amarantha.gpiomofo.pixeltape.pattern.SlidingBars;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.scenario.GingerLineSetup.MEDIA_SERVER_IP;
import static net.amarantha.gpiomofo.scenario.GingerLineSetup.MEDIA_SERVER_OSC_PORT;

public class GingerlineBriefingRoom extends Scenario {

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

    @Inject private PixelTapeController pixeltape;

    @Inject private ChasePattern domesChase;

    @Inject private SlidingBars slowBarsPipe1;
    @Inject private SlidingBars slowBarsPipe2;
    @Inject private SlidingBars slowBarsPipe3;
    @Inject private SlidingBars slowBarsPipe4;

    @Inject private SlidingBars colourSpinDome1;
    @Inject private SlidingBars colourSpinDome2;
    @Inject private SlidingBars colourSpinDome3;
    @Inject private SlidingBars colourSpinDome4;

    @Inject private SlidingBars colourBarsPipe1;
    @Inject private SlidingBars colourBarsPipe2;
    @Inject private SlidingBars colourBarsPipe3;
    @Inject private SlidingBars colourBarsPipe4;

    @Inject private FlashAndFade flashFadePipe1;
    @Inject private FlashAndFade flashFadePipe2;
    @Inject private FlashAndFade flashFadePipe3;
    @Inject private FlashAndFade flashFadePipe4;

    private void configurePixelTapePatterns() {

        ////////////////
        // Background //
        ////////////////

        RGB backColour = new RGB(255,255,70);

        domesChase
                .setMinColour(255,255,70)
                .setBlockWidth(50)
                .setMovement(5)
                .setRefreshInterval(40)
                .init(DOME_1_START, ALL_DOMES);

        slowBarsPipe1
                .setColour(backColour)
                .setFadeInTime(500)
                .setReverse(true)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_1_START, PIPE_1_SIZE);

        slowBarsPipe2
                .setColour(backColour)
                .setFadeInTime(500)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_2_START, PIPE_2_SIZE);

        slowBarsPipe3
                .setColour(backColour)
                .setFadeInTime(500)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .setReverse(true)
                .init(PIPE_3_START, PIPE_3_SIZE);

        slowBarsPipe4
                .setColour(backColour)
                .setFadeInTime(500)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_4_START, PIPE_4_SIZE);

        //////////////
        // Activate //
        //////////////

        RGB colour1 = new RGB(255, 100, 0);
        RGB colour2 = new RGB(100, 0, 255);
        RGB colour3 = new RGB(50, 80, 255);
        RGB colour4 = new RGB(255, 0, 100);

        colourSpinDome1
                .setRefreshRange(400, 30, -20)
                .setColour(colour1)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_1_START, DOME_SIZE);

        colourSpinDome2
                .setRefreshRange(400, 30, -20)
                .setColour(colour2)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_2_START, DOME_SIZE);

        colourSpinDome3
                .setRefreshRange(400, 30, -20)
                .setColour(colour3)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_3_START, DOME_SIZE);

        colourSpinDome4
                .setRefreshRange(400, 30, -20)
                .setColour(colour4)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_4_START, DOME_SIZE);

        int pipeActiveBar = 10;
        int pipeActiveSpace = 5;

        colourBarsPipe1
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour1)
                .setReverse(true)
                .setFadeInTime(5000)
                .init(PIPE_1_START, PIPE_1_SIZE);

        colourBarsPipe2
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour2)
                .setFadeInTime(5000)
                .init(PIPE_2_START, PIPE_2_SIZE);

        colourBarsPipe3
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour3)
                .setReverse(true)
                .setFadeInTime(5000)
                .init(PIPE_3_START, PIPE_3_SIZE);

        colourBarsPipe4
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour4)
                .setFadeInTime(5000)
                .init(PIPE_4_START, PIPE_4_SIZE);

        flashFadePipe1.setReverse(true)
                .init(PIPE_1_START, PIPE_1_SIZE);

        flashFadePipe2
                .init(PIPE_2_START, PIPE_2_SIZE);

        flashFadePipe3.setReverse(true)
                .init(PIPE_3_START, PIPE_3_SIZE);

        flashFadePipe4
                .init(PIPE_4_START, PIPE_4_SIZE);

        ////////////////
        // Setup Tape //
        ////////////////

        pixeltape
                .addPattern(flashFadePipe1)
                .addPattern(flashFadePipe2)
                .addPattern(flashFadePipe3)
                .addPattern(flashFadePipe4)
                .addPattern(colourSpinDome1)
                .addPattern(colourSpinDome2)
                .addPattern(colourSpinDome3)
                .addPattern(colourSpinDome4)
                .addPattern(colourBarsPipe1)
                .addPattern(colourBarsPipe2)
                .addPattern(colourBarsPipe3)
                .addPattern(colourBarsPipe4)
                .addPattern(domesChase)
                .addPattern(slowBarsPipe1)
                .addPattern(slowBarsPipe2)
                .addPattern(slowBarsPipe3)
                .addPattern(slowBarsPipe4)
                .init(WHOLE_TAPE);

        pixeltape.start();

    }

    private Trigger podiumButton;
    private Trigger httpBackground;
    private Trigger httpActivate;
    private Trigger httpSendOsc;
    private Trigger httpStop;

    @Override
    public void setupTriggers() {

        podiumButton =      triggers.gpio(2, PULL_UP, true);
        httpBackground =    triggers.http("background");
        httpActivate =      triggers.http("active");
        httpStop =          triggers.http("stop");
        httpSendOsc =       triggers.http("osc");

    }

    private Target stopPixelTape;
    private Target backgroundScene;
    private Target activationScene;
    private Target sendOscCommand;

    @Override
    public void setupTargets() {

        stopPixelTape = targets.stopPixelTape();

        backgroundScene =
            targets.chain()
                .add(stopPixelTape)
                .add(targets.pixelTape(domesChase))
                .add(targets.pixelTape(slowBarsPipe1))
                .add(targets.pixelTape(slowBarsPipe2))
                .add(targets.pixelTape(slowBarsPipe3))
                .add(targets.pixelTape(slowBarsPipe4))
            .build().oneShot(true);

        Target pipe1 = targets.pixelTape(colourBarsPipe1);
        Target flash1 =
            targets.chain()
                .add(targets.cancel(pipe1).setForce(true))
                .add(targets.pixelTape(flashFadePipe1))
            .build();

        Target pipe2 = targets.pixelTape(colourBarsPipe2);
        Target flash2 =
            targets.chain()
                .add(targets.cancel(pipe2).setForce(true))
                .add(targets.pixelTape(flashFadePipe2))
            .build();

        Target pipe3 = targets.pixelTape(colourBarsPipe3);
        Target flash3 = targets.chain()
                .add(targets.cancel(pipe3).setForce(true))
                .add(targets.pixelTape(flashFadePipe3))
            .build();

        Target pipe4 = targets.pixelTape(colourBarsPipe4);
        Target flash4 = targets.chain()
                .add(targets.cancel(pipe4).setForce(true))
                .add(targets.pixelTape(flashFadePipe4))
            .build();

        activationScene =
            targets.chain()
                .add(0,     stopPixelTape)
                .add(0,     pipe1)
                .add(5000,  targets.pixelTape(colourSpinDome1))
                .add(0,     pipe2)
                .add(5000,  targets.pixelTape(colourSpinDome2))
                .add(0,     pipe3)
                .add(5000,  targets.pixelTape(colourSpinDome3))
                .add(0,     pipe4)
                .add(15000, targets.pixelTape(colourSpinDome4))
                .add(5000,  flash1)
                .add(5000,  flash2)
                .add(5000,  flash3)
                .add(5000,  flash4)
                .add(2000,  backgroundScene)
            .build().oneShot(true);

        sendOscCommand = targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "helloben", 255));

    }

    @Override
    public void setupLinks() {

        configurePixelTapePatterns();

        links
            .link(podiumButton,     activationScene)
            .link(httpActivate,     activationScene)
            .link(httpBackground,   backgroundScene)
            .link(httpStop,         stopPixelTape)
            .link(httpSendOsc,      sendOscCommand)
        ;

        links.lock(30000, activationScene);

        backgroundScene.activate();

    }

}
