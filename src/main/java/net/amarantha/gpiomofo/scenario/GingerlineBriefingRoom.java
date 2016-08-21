package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.pattern.ChasePattern;
import net.amarantha.gpiomofo.pixeltape.pattern.FadeToBlack;
import net.amarantha.gpiomofo.pixeltape.pattern.FlashAndFade;
import net.amarantha.gpiomofo.pixeltape.pattern.SlidingBars;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;

public class GingerlineBriefingRoom extends Scenario {

    @Inject private PixelTapeController pixeltape;

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

    private Trigger startButton;
    private Trigger httpBackground;
    private Trigger httpActivate;
    private Trigger httpStop;

    @Override
    public void setupTriggers() {

        startButton =       triggers.gpio(2, PULL_UP, true);
        httpBackground =    triggers.http("background");
        httpActivate =      triggers.http("active");
        httpStop =          triggers.http("stop");

    }

    private Target stopPixelTape;
    private Target backgroundScene;
    private Target activationScene;

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

        RGB backColour = new RGB(255,255,70);

        Target domesChase =
            targets.pixelTape(ChasePattern.class)
                .setColour(backColour)
                .setBlockWidth(50)
                .setMovement(5)
                .setRefreshInterval(40)
                .init(DOME_1_START, ALL_DOMES);

        Target slowPipe1 =
            targets.pixelTape(SlidingBars.class)
                .setColour(backColour)
                .setFadeInTime(2000)
                .setReverse(true)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_1_START, PIPE_1_SIZE);

        Target slowPipe2 =
            targets.pixelTape(SlidingBars.class)
                .setColour(backColour)
                .setFadeInTime(2000)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_2_START, PIPE_2_SIZE);

        Target slowPipe3 =
            targets.pixelTape(SlidingBars.class)
                .setColour(backColour)
                .setFadeInTime(2000)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .setReverse(true)
                .init(PIPE_3_START, PIPE_3_SIZE);

        Target slowPipe4 =
            targets.pixelTape(SlidingBars.class)
                .setColour(backColour)
                .setFadeInTime(2000)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_4_START, PIPE_4_SIZE);

        backgroundScene =
            targets.chain("Background-Scene")
                .add(stopPixelTape)
                .add(domesChase)
                .add(slowPipe1)
                .add(slowPipe2)
                .add(slowPipe3)
                .add(slowPipe4)
                .build().oneShot(true);

        //////////////
        // Activate //
        //////////////

        RGB colour1 = new RGB(255, 100, 0);
        RGB colour2 = new RGB(100, 0, 255);
        RGB colour3 = new RGB(50, 80, 255);
        RGB colour4 = new RGB(255, 0, 100);

        Target spinDome1 =
            targets.pixelTape(SlidingBars.class)
                .setRefreshRange(400, 30, -20)
                .setColour(colour1)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_1_START, DOME_SIZE);

        Target spinDome2 =
            targets.pixelTape(SlidingBars.class)
                .setRefreshRange(400, 30, -20)
                .setColour(colour2)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_2_START, DOME_SIZE);

        Target spinDome3 =
            targets.pixelTape(SlidingBars.class)
                .setRefreshRange(400, 30, -20)
                .setColour(colour3)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_3_START, DOME_SIZE);

        Target spinDome4 =
            targets.pixelTape(SlidingBars.class)
                .setRefreshRange(400, 30, -20)
                .setColour(colour4)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_4_START, DOME_SIZE);


        int pipeActiveBar = 10;
        int pipeActiveSpace = 5;

        Target fastPipe1 =
            targets.pixelTape(SlidingBars.class)
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour1)
                .setReverse(true)
                .setFadeInTime(5000)
                .init(PIPE_1_START, PIPE_1_SIZE);

        Target fastPipe2 =
            targets.pixelTape(SlidingBars.class)
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour2)
                .setFadeInTime(5000)
                .init(PIPE_2_START, PIPE_2_SIZE);

        Target fastPipe3 =
            targets.pixelTape(SlidingBars.class)
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour3)
                .setReverse(true)
                .setFadeInTime(5000)
                .init(PIPE_3_START, PIPE_3_SIZE);

        Target fastPipe4 =
            targets.pixelTape(SlidingBars.class)
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
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

        activationScene =
            targets.chain("Activation-Scene")

                // Fade to black
                .add(2000,  fadeToBlack)
                .add(0,     stopPixelTape)

                // Dome&Pipe 1
                .add(0,     spinDome1)
                .add(5000,  fastPipe1)

                // Dome&Pipe 2
                .add(0,     spinDome2)
                .add(5000,  fastPipe2)

                // Dome&Pipe 3
                .add(0,     spinDome3)
                .add(5000,  fastPipe3)

                // Dome&Pipe 4
                .add(0,     spinDome4)
                .add(15000, fastPipe4)

                // Flash 1
                .add(0,     spinDome1.cancel())
                .add(0,     fastPipe1.cancel())
                .add(0,     flashDome1)
                .add(5000,  flashPipe1)

                // Flash 2
                .add(0,     spinDome2.cancel())
                .add(0,     fastPipe2.cancel())
                .add(0,     flashDome2)
                .add(5000,  flashPipe2)

                // Flash 3
                .add(0,     spinDome3.cancel())
                .add(0,     fastPipe3.cancel())
                .add(0,     flashDome3)
                .add(5000,  flashPipe3)

                // Flash 4
                .add(0,     spinDome4.cancel())
                .add(0,     fastPipe4.cancel())
                .add(0,     flashDome4)
                .add(6000,  flashPipe4)

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
            .link(startButton,      activationScene)
            .link(httpActivate,     activationScene)
            .link(httpBackground,   backgroundScene)
            .link(httpStop,         stopPixelTape)
        ;

        links.lock(60000, activationScene);

        pixeltape
            .init(WHOLE_TAPE)
            .start();

        backgroundScene.activate();

    }

}
