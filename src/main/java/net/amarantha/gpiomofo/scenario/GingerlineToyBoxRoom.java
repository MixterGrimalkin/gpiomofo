package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.RGBW;
import net.amarantha.gpiomofo.pixeltape.pattern.SolidColour;
import net.amarantha.gpiomofo.pixeltape.pattern.SolidColourWithWhite;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

public class GingerlineToyBoxRoom extends Scenario {

    private Trigger testTrigger;
    private Target panicTarget;

    @Inject private PixelTapeController pixelTapeController;

    @Inject private SolidColour small1Red;
    @Inject private SolidColour small1Off;
    @Inject private SolidColour small2Red;
    @Inject private SolidColour small2Off;
    @Inject private SolidColour small3Red;
    @Inject private SolidColour small3Off;

    @Inject private SolidColourWithWhite big1Red;
    @Inject private SolidColourWithWhite big1Off;

    /*
        Static state for 10 minutes

        POP GOES THE WEASEL

        Colour Fades

        TIMEKEEPER. BATTLE

     */
    @Override
    public void setupTriggers() {

        testTrigger = triggers.http("test");

        small1Red.setColour(new RGB(255, 0, 0)).init(0, 24);
        small1Off.setColour(new RGB(0,0,0)).init(0,24);
        small2Red.setColour(new RGB(255, 0, 0)).init(24, 24);
        small2Off.setColour(new RGB(0,0,0)).init(24,24);
        small3Red.setColour(new RGB(255, 0, 0)).init(48, 24);
        small3Off.setColour(new RGB(0,0,0)).init(48,24);

        big1Red.setColour(new RGBW(255,0,0,0)).init(72, 60);
        big1Off.setColour(new RGBW(0,0,0,0)).init(72, 60);


        pixelTapeController
            .addPattern(small1Red)
            .addPattern(small2Red)
            .addPattern(small3Red)
            .addPattern(big1Red)
            .addPattern(small1Off)
            .addPattern(small2Off)
            .addPattern(small3Off)
            .addPattern(big1Off)
        ;

    }

    @Override
    public void setupTargets() {

        panicTarget = small1Red;

    }

    @Override
    public void setupLinks() {

        links.link(testTrigger,   panicTarget);

        Target on1 = small1Red;
        Target on2 = small2Red;
        Target on3 = small3Red;
        Target on4 = big1Red;

        Target off1 = small1Off;
        Target off2 = small2Off;
        Target off3 = small3Off;
        Target off4 = big1Off;

        Target one = targets.chain()
            .add(1000, on1)
            .add(0, targets.cancel(on1))
            .add(800, off1)
            .add(0, targets.cancel(off1))
        .build().repeat(true);

        Target two = targets.chain()
            .add(900, on2)
            .add(0, targets.cancel(on2))
            .add(800, off2)
            .add(0, targets.cancel(off2))
        .build().repeat(true);

        Target three = targets.chain()
            .add(950, on3)
            .add(0, targets.cancel(on3))
            .add(800, off3)
            .add(0, targets.cancel(off3))
        .build().repeat(true);

        Target four = targets.chain()
            .add(1050, on4)
            .add(0, targets.cancel(on4))
            .add(800, off4)
            .add(0, targets.cancel(off4))
        .build().repeat(true);

        pixelTapeController.init(RGB_WIDTH+80);

        pixelTapeController.start();

        one.activate();
        two.activate();
        three.activate();
        four.activate();

    }

    public static final int RGB_WIDTH = 72;
    public static final int RGBW_WIDTH = 120;

}
