package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.pattern.ChasePattern;
import net.amarantha.gpiomofo.pixeltape.pattern.FlashBang;
import net.amarantha.gpiomofo.pixeltape.pattern.SlidingBars;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.scenario.GingerLineSetup.MEDIA_SERVER_IP;
import static net.amarantha.gpiomofo.scenario.GingerLineSetup.MEDIA_SERVER_OSC_PORT;

public class GingerlineBriefingRoom extends Scenario {

    private Trigger panicButton;
    private Trigger backgroundTrigger;
    private Trigger activeTrigger;

    private Target panicTarget;
    private Target webTarget;

    @Inject private PixelTapeController tape;

    @Inject private SlidingBars domeBackground1;
    @Inject private SlidingBars domeBackground2;
    @Inject private SlidingBars domeBackground3;
    @Inject private SlidingBars domeBackground4;

    @Inject private SlidingBars pipeBackground1;
    @Inject private SlidingBars pipeBackground2;
    @Inject private SlidingBars pipeBackground3;
    @Inject private SlidingBars pipeBackground4;

    @Inject private SlidingBars domeActive1;
    @Inject private SlidingBars domeActive2;
    @Inject private SlidingBars domeActive3;
    @Inject private SlidingBars domeActive4;

    @Inject private SlidingBars pipeActive1;
    @Inject private SlidingBars pipeActive2;
    @Inject private SlidingBars pipeActive3;
    @Inject private SlidingBars pipeActive4;

    @Inject private FlashBang flashBang1;
    @Inject private FlashBang flashBang2;
    @Inject private FlashBang flashBang3;
    @Inject private FlashBang flashBang4;

    @Inject private ChasePattern chasePattern;

    @Override
    public void setupTriggers() {

        backgroundTrigger = triggers.http("background");
        activeTrigger = triggers.http("active");

    }

    @Override
    public void setupTargets() {

    }

    @Override
    public void setupLinks() {

        RGB backColour = new RGB(255,255,70);
        domeBackground1
                .setColour(backColour)
                .setFadeInTime(5000)
                .setBarChange(5, 12, 2)
                .setRefreshInterval(350)
                .init(DOME_1_START, DOME_SIZE);
        domeBackground2
                .setColour(backColour)
                .setFadeInTime(5000)
                .setBarChange(5, 12, 2)
                .setRefreshInterval(352)
                .init(DOME_2_START, DOME_SIZE);
        domeBackground3
                .setColour(backColour)
                .setFadeInTime(5000)
                .setBarChange(5, 12, 2)
                .setRefreshInterval(348)
                .init(DOME_3_START, DOME_SIZE);
        domeBackground4
                .setColour(backColour)
                .setFadeInTime(5000)
                .setBarChange(5, 12, 2)
                .setRefreshInterval(354)
                .init(DOME_4_START, DOME_SIZE);

        chasePattern
                .setMinColour(255,255,70)
                .setBlockWidth(50)
                .setMovement(5)
//                .setBounce(true)
                .setRefreshInterval(40)
        .init(DOME_1_START, ALL_DOMES);


        pipeBackground1
                .setColour(backColour)
                .setFadeInTime(500)
                .setReverse(true)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_1_START, PIPE_1);
        pipeBackground2
                .setColour(backColour)
                .setFadeInTime(500)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_2_START, PIPE_2);
        pipeBackground3
                .setColour(backColour)
                .setFadeInTime(500)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .setReverse(true)
                .init(PIPE_3_START, PIPE_3);
        pipeBackground4
                .setColour(backColour)
                .setFadeInTime(500)
                .setBarSize(5, 10)
                .setRefreshInterval(250)
                .init(PIPE_4_START, PIPE_4);

        RGB colour1 = new RGB(255, 100, 0);
        RGB colour2 = new RGB(100, 0, 255);
        RGB colour3 = new RGB(50, 80, 255);
        RGB colour4 = new RGB(255, 0, 100);

        domeActive1
                .setRefreshRange(400, 30, -20)
                .setColour(colour1)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_1_START, DOME_SIZE);
        domeActive2
                .setRefreshRange(400, 30, -20)
                .setColour(colour2)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_2_START, DOME_SIZE);
        domeActive3
                .setRefreshRange(400, 30, -20)
                .setColour(colour3)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_3_START, DOME_SIZE);
        domeActive4
                .setRefreshRange(400, 30, -20)
                .setColour(colour4)
                .setBarChange(5, 12, 2)
                .setFadeInTime(5000)
                .init(DOME_4_START, DOME_SIZE);

        int pipeActiveBar = 10;
        int pipeActiveSpace = 5;

        pipeActive1
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour1)
                .setReverse(true)
                .setFadeInTime(5000)
                .init(PIPE_1_START, PIPE_1);
        pipeActive2
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour2)
                .setFadeInTime(5000)
                .init(PIPE_2_START, PIPE_2);
        pipeActive3
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour3)
                .setReverse(true)
                .setFadeInTime(5000)
                .init(PIPE_3_START, PIPE_3);
        pipeActive4
                .setBarSize(pipeActiveBar, pipeActiveSpace)
                .setRefreshRange(400, 30, -20)
                .setColour(colour4)
                .setFadeInTime(5000)
                .init(PIPE_4_START, PIPE_4);

        Target backgroundPattern;
        Target activatePattern;

        Trigger stopTrigger = triggers.http("stop");
        Target stopPixelTape = targets.stopPixelTape();

        Trigger testOsc = triggers.http("osc");
        Target osc = targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "helloben", 255));

        flashBang1.setReverse(true).init(PIPE_1_START, PIPE_1);
        flashBang2.init(PIPE_2_START, PIPE_2);
        flashBang3.setReverse(true).init(PIPE_3_START, PIPE_3);
        flashBang4.init(PIPE_4_START, PIPE_4);

        backgroundPattern =
            targets.chain()
                .add(stopPixelTape)
                .add(targets.pixelTape(chasePattern))
//                .add(targets.pixelTape(domeBackground1))
//                .add(targets.pixelTape(domeBackground2))
//                .add(targets.pixelTape(domeBackground3))
//                .add(targets.pixelTape(domeBackground4))
                .add(targets.pixelTape(pipeBackground1))
                .add(targets.pixelTape(pipeBackground2))
                .add(targets.pixelTape(pipeBackground3))
                .add(targets.pixelTape(pipeBackground4))
            .build().oneShot(true);

        Target pipe1 = targets.pixelTape(pipeActive1);
        Target pipe2 = targets.pixelTape(pipeActive2);
        Target pipe3 = targets.pixelTape(pipeActive3);
        Target pipe4 = targets.pixelTape(pipeActive4);

        Target flash1 = targets.chain()
                .add(targets.cancel(pipe1).setForce(true))
                .add(targets.pixelTape(flashBang1)).build();

        Target flash2 = targets.chain()
                .add(targets.cancel(pipe2).setForce(true))
                .add(targets.pixelTape(flashBang2)).build();

        Target flash3 = targets.chain()
                .add(targets.cancel(pipe3).setForce(true))
                .add(targets.pixelTape(flashBang3)).build();

        Target flash4 = targets.chain()
                .add(targets.cancel(pipe4).setForce(true))
                .add(targets.pixelTape(flashBang4)).build();

        activatePattern =
            targets.chain()
                .add(1000, stopPixelTape)
                .add(0, pipe1)
                .add(5000, targets.pixelTape(domeActive1))
                .add(0, pipe2)
                .add(5000, targets.pixelTape(domeActive2))
                .add(0, pipe3)
                .add(5000, targets.pixelTape(domeActive3))
                .add(0, pipe4)
                .add(15000, targets.pixelTape(domeActive4))
                .add(5000, flash1)
                .add(5000, flash2)
                .add(5000, flash3)
                .add(5000, flash4)
                .add(2000, backgroundPattern)
            .build().oneShot(true);


        Trigger podium1 = triggers.gpio(2, PULL_UP, true);
        Target queue = targets.queue(backgroundPattern, activatePattern).oneShot(true);

        links
            .link(podium1, queue, osc)
            .link(activeTrigger, activatePattern)
            .link(backgroundTrigger, backgroundPattern)
            .link(stopTrigger, stopPixelTape)
            .link(testOsc, osc)
        ;

        tape.addPattern(flashBang1);
        tape.addPattern(flashBang2);
        tape.addPattern(flashBang3);
        tape.addPattern(flashBang4);
        tape.addPattern(domeActive1);
        tape.addPattern(domeActive2);
        tape.addPattern(domeActive3);
        tape.addPattern(domeActive4);
        tape.addPattern(pipeActive1);
        tape.addPattern(pipeActive2);
        tape.addPattern(pipeActive3);
        tape.addPattern(pipeActive4);
//        tape.addPattern(domeBackground1);
//        tape.addPattern(domeBackground2);
//        tape.addPattern(domeBackground3);
//        tape.addPattern(domeBackground4);
        tape.addPattern(chasePattern);
        tape.addPattern(pipeBackground1);
        tape.addPattern(pipeBackground2);
        tape.addPattern(pipeBackground3);
        tape.addPattern(pipeBackground4);
        tape.init(WHOLE_TAPE);
        tape.start();

        backgroundPattern.activate();

    }

    public static final int DOME_SIZE = 47;
    public static final int ALL_DOMES = DOME_SIZE * 4;

    public static final int DOME_1_START = 0;
    public static final int DOME_2_START = DOME_SIZE;
    public static final int DOME_3_START = 2 * DOME_SIZE;
    public static final int DOME_4_START = 3 * DOME_SIZE;

    public static final int PIPE_1 = 23 + 63;
    public static final int PIPE_2 = 60 + 27;
    public static final int PIPE_3 = 26 + 59;
    public static final int PIPE_4 = 63 + 29;

    public static final int PIPE_4_START = ALL_DOMES;
    public static final int PIPE_3_START = PIPE_4_START + PIPE_4;
    public static final int PIPE_2_START = PIPE_3_START + PIPE_3;
    public static final int PIPE_1_START = PIPE_2_START + PIPE_2;

    public static final int WHOLE_TAPE = ALL_DOMES + PIPE_1 + PIPE_2 + PIPE_3 + PIPE_4;


}
