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

    private Trigger blueTrigger;
    private Trigger amberTrigger;
    private Trigger redTrigger;

    @Inject private PixelTapeController pixelTapeController;

    private Target blueScene;
    private Target amberScene;
    private Target redScene;
    private Target one;
    private Target two;
    private Target three;
    private Target four;
    private Target five;


    /*
        Static state for 10 minutes

        POP GOES THE WEASEL

        Colour Fades

        TIMEKEEPER. BATTLE

     */
    @Override
    public void setupTriggers() {

        blueTrigger = triggers.osc("blue", 53000, "blue");
        amberTrigger = triggers.osc("amber", 53000, "amber");
        redTrigger = triggers.osc("red", 53000, "red");

    }

    private static final int SMALL_BALL = 7;
    private static final int BIG_BALL = 14;

    private static final int BALL_1_S = 0;
    private static final int BALL_2_B = SMALL_BALL;
    private static final int BALL_3_S = BALL_2_B + BIG_BALL;
    private static final int BALL_4_B = BALL_3_S + SMALL_BALL;
    private static final int BALL_5_S = BALL_4_B + BIG_BALL;

    @Override
    public void setupTargets() {

        Target stop = targets.stopPixelTape();

        one = targets.pixelTape(SolidColour.class)
                .setColour(new RGB(255,0,0))
                .init(BALL_1_S, SMALL_BALL);

        two = targets.pixelTape(SolidColour.class)
                .setColour(new RGB(0,255,0))
                .init(BALL_2_B, BIG_BALL);

        three = targets.pixelTape(SolidColour.class)
                .setColour(new RGB(0,0,255))
                .init(BALL_3_S, SMALL_BALL);

        four = targets.pixelTape(SolidColour.class)
                .setColour(new RGB(255,255,0))
                .init(BALL_4_B, BIG_BALL);

        five = targets.pixelTape(SolidColour.class)
                .setColour(new RGB(255,0,255))
                .init(BALL_5_S, SMALL_BALL);

//        Target blueSceneRGB =
//                targets.pixelTape(SolidColour.class)
//                        .setColour(new RGB(0, 0, 255))
//                        .init(0, 72);
//        Target blueSceneRGBW =
//                targets.pixelTape(SolidColourWithWhite.class)
//                        .setColour(new RGBW(0, 0, 255, 0))
//                        .init(72, 120);
//        blueScene = targets.chain()
//                .add(stop)
//                .add(blueSceneRGB)
//                .add(blueSceneRGBW)
//                .build().oneShot(true);
//
//        Target amberSceneRGB =
//                targets.pixelTape(SolidColour.class)
//                        .setColour(new RGB(255, 60, 0))
//                        .init(0, 72);
//        Target amberSceneRGBW =
//                targets.pixelTape(SolidColourWithWhite.class)
//                        .setColour(new RGBW(255, 60, 0, 50))
//                        .init(72, 120);
//        amberScene = targets.chain()
//                .add(stop)
//                .add(amberSceneRGB)
//                .add(amberSceneRGBW)
//                .build().oneShot(true);
//
//        Target redSceneRGB =
//                targets.pixelTape(SolidColour.class)
//                        .setColour(new RGB(255, 0, 0))
//                        .init(0, 172);
//        Target redSceneRGBW =
//                targets.pixelTape(SolidColourWithWhite.class)
//                        .setColour(new RGBW(255, 0, 0, 0))
//                        .init(72, 120);
//        redScene = targets.chain()
//                .add(stop)
//                .add(redSceneRGB)
////                .add(redSceneRGBW)
//                .build().oneShot(true);

    }

    @Override
    public void setupLinks() {


        pixelTapeController
            .init(BALL_5_S + SMALL_BALL)
            .start();

        one.activate();
        two.activate();
        three.activate();
        four.activate();
        five.activate();

    }

    public static final int RGB_WIDTH = 72;
    public static final int RGBW_WIDTH = 120;

}
