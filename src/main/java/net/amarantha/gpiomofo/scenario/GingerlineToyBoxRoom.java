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

        Target one = targets.chain()
            .add(1000,  small1Red)
            .add(0,     small1Red.cancel())
            .add(800,   small1Off)
            .add(0,     small1Off.cancel())
        .build().repeat(true);

        Target two = targets.chain()
            .add(1000,  small2Red)
            .add(0,     small2Red.cancel())
            .add(800,   small2Off)
            .add(0,     small2Off.cancel())
        .build().repeat(true);

        Target three = targets.chain()
            .add(1000,  small3Red)
            .add(0,     small3Red.cancel())
            .add(800,   small3Off)
            .add(0,     small3Off.cancel())
        .build().repeat(true);

        Target four = targets.chain()
            .add(1000,  big1Red)
            .add(0,     big1Red.cancel())
            .add(800,   big1Off)
            .add(0,     big1Off.cancel())
        .build().repeat(true);

        pixelTapeController
            .init(RGB_WIDTH+80)
            .start();

        one.activate();
        two.activate();
        three.activate();
        four.activate();

    }

    public static final int RGB_WIDTH = 72;
    public static final int RGBW_WIDTH = 120;

}
