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

    @Override
    public void setupTargets() {

        Target stop = targets.stopPixelTape();

        Target blueSceneRGB =
                targets.pixelTape(SolidColour.class)
                        .setColour(new RGB(0, 0, 255))
                        .init(0, 72);
        Target blueSceneRGBW =
                targets.pixelTape(SolidColourWithWhite.class)
                        .setColour(new RGBW(0, 0, 255, 0))
                        .init(72, 120);
        blueScene = targets.chain()
                .add(stop)
                .add(blueSceneRGB)
                .add(blueSceneRGBW)
                .build().oneShot(true);

        Target amberSceneRGB =
                targets.pixelTape(SolidColour.class)
                        .setColour(new RGB(255, 60, 0))
                        .init(0, 72);
        Target amberSceneRGBW =
                targets.pixelTape(SolidColourWithWhite.class)
                        .setColour(new RGBW(255, 60, 0, 50))
                        .init(72, 120);
        amberScene = targets.chain()
                .add(stop)
                .add(amberSceneRGB)
                .add(amberSceneRGBW)
                .build().oneShot(true);

        Target redSceneRGB =
                targets.pixelTape(SolidColour.class)
                        .setColour(new RGB(255, 0, 0))
                        .init(0, 72);
        Target redSceneRGBW =
                targets.pixelTape(SolidColourWithWhite.class)
                        .setColour(new RGBW(255, 0, 0, 0))
                        .init(72, 120);
        redScene = targets.chain()
                .add(stop)
                .add(redSceneRGB)
                .add(redSceneRGBW)
                .build().oneShot(true);

    }

    @Override
    public void setupLinks() {

        links
            .link(blueTrigger, blueScene)
            .link(amberTrigger, amberScene)
            .link(redTrigger, redScene)
        ;

        pixelTapeController
            .init(RGB_WIDTH+160)
            .start();

        amberScene.activate();


    }

    public static final int RGB_WIDTH = 72;
    public static final int RGBW_WIDTH = 120;

}
