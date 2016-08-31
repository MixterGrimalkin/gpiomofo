package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.pattern.ChasePattern;
import net.amarantha.gpiomofo.pixeltape.pattern.IntensityFade;
import net.amarantha.gpiomofo.pixeltape.pattern.SlidingBars;
import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

public class GingerlineBikeRoom extends Scenario {

    private Trigger underwaterButton;
    private Trigger panicUnderwater;
    private Trigger panicUnderwaterHold;
    private Trigger panicBikes;
    private Trigger panicBikesHold;
    private Trigger panicKitchen;
    private Trigger panicKitchenHold;
    private Trigger interrupt;
    private Trigger lightButton1;
    private Trigger lightButton2;
    private Trigger lightButton3;
    private Trigger lightButton4;

    private Target underwaterControl;
    private Target panicUnderwaterLights;
    private Target panicUnderwaterPi;
    private Target panicBikesLights;
    private Target panicBikesPi;
    private Target panicKitchenLights;
    private Target panicKitchenPi;
    private Target lightScene1;
    private Target lightScene2;
    private Target lightScene3;
    private Target lightScene4;

    @Inject
    private PixelTapeController pixeltape;
    private Target stop;
    private Trigger lightButton0;
    private Trigger lightButtonExit;
    private Target exitScene;

    @Override
    public void setupTriggers() {

        interrupt = triggers.gpio("INT", 0, PULL_UP, true);

        underwaterButton =      triggers.gpio("Underwater-Button", 5, PULL_UP, false);
        panicUnderwater =       triggers.gpio("Panic-2", 2, PULL_UP, false);
        panicUnderwaterHold =   triggers.gpio("Panic-2-Hold", 2, PULL_UP, false).setHoldTime(1000);
        panicBikes =            triggers.gpio("Panic-3", 3, PULL_UP, false);
        panicBikesHold =        triggers.gpio("Panic-3-Hold", 3, PULL_UP, false).setHoldTime(1000);
        panicKitchen =          triggers.gpio("Panic-4", 4, PULL_UP, false);
        panicKitchenHold =      triggers.gpio("Panic-4-Hold", 4, PULL_UP, false).setHoldTime(1000);

        lightButton0 = triggers.osc("0", 53000, "bike-lights-0");
        lightButton1 = triggers.osc("1", 53000, "bike-lights-1");
        lightButton2 = triggers.osc("2", 53000, "bike-lights-2");
        lightButton3 = triggers.osc("3", 53000, "bike-lights-3");
        lightButton4 = triggers.osc("4", 53000, "bike-lights-4");

        lightButtonExit = triggers.osc("5", 53000, "bike-exit");

    }

    @Override
    public void setupTargets() {

        Target bars1 = targets
                .pixelTape(SlidingBars.class)
                .setBarSize(3, 3)
                .setColour(new RGB(65,255,0))
                .setRefreshInterval(150)
                .init(0, 150);
        Target fade1 = targets
                .pixelTape(IntensityFade.class)
                .setMin(0.1)
                .setMax(0.4)
                .setMinPause(100)
//                .setMaxPause(20)
                .setIntensityDelta(0.7)
                .init(0,150);

        Target bars2 = targets
            .pixelTape(SlidingBars.class)
                .setBarSize(3, 3)
                .setColour(new RGB(65,255,0))
                .setRefreshInterval(150)
                .init(0, 150);
        Target fade2 = targets
            .pixelTape(IntensityFade.class)
                .setMin(0.1)
                .setMax(0.6)
                .setMinPause(25)
                .setMaxPause(20)
                .setIntensityDelta(0.1)
                .init(0,150);

        Target bars3 = targets
            .pixelTape(SlidingBars.class)
                .setBarSize(3, 3)
                .setColour(new RGB(65,255,0))
                .setRefreshInterval(90)
                .init(0, 150);
        Target fade3 = targets
            .pixelTape(IntensityFade.class)
                .setMin(0.1)
                .setMax(0.7)
                .setMinPause(10)
                .setIntensityDelta(0.1)
                .init(0,150);

        Target bars4 = targets
            .pixelTape(SlidingBars.class)
                .setBarSize(4, 3)
                .setColour(new RGB(65,255,0))
                .setRefreshInterval(50)
                .init(0, 150);
        Target fade4 = targets
            .pixelTape(IntensityFade.class)
                .setMin(0.45)
                .setMax(0.9)
                .setMinPause(0)
                .setIntensityDelta(0.1)
                .init(0,150);

        Target exit = targets
            .pixelTape(ChasePattern.class)
                .setColour(new RGB(65,255,0).withBrightness(0.8))
                .setBlockWidth(30)
                .setMovement(10)
                .setRefreshInterval(50)
                .init(0, 150);


        stop = targets.stopPixelTape();

        lightScene1 = targets.chain("One")
                .add(stop)
                .add(fade1)
                .add(bars1)
                .build().oneShot(true);

        lightScene2 = targets.chain("Two")
                .add(stop)
                .add(fade2)
                .add(bars2)
                .build().oneShot(true);

        lightScene3 = targets.chain("Three")
                .add(stop)
                .add(fade3)
                .add(bars3)
                .build().oneShot(true);

        lightScene4 = targets.chain("Four")
                .add(stop)
                .add(fade4)
                .add(bars4)
                .build().oneShot(true);

        exitScene = targets.chain("Exit")
                .add(stop)
                .add(exit)
                .build().oneShot(true);

        String ipBen = "192.168.42.100";
        int portBen = 7700;

        underwaterControl = targets.osc(new OscCommand(ipBen, portBen, "alarm/c2slide", 255));

        panicUnderwaterLights = targets.osc(new OscCommand(ipBen, portBen, "alarm/c2", 255));
        panicUnderwaterPi = targets.http(
                new HttpCommand(POST, "192.168.42.105", 8001, "gpiomofo/trigger/panic-underwater/fire", "", "")
        );

        panicBikesLights = targets.osc(new OscCommand(ipBen, portBen, "alarm/c3", 255));
        panicBikesPi = targets.http(
                new HttpCommand(POST, "192.168.42.105", 8001, "gpiomofo/trigger/panic-bikes/fire", "", "")
        );

        panicKitchenLights = targets.osc(new OscCommand(ipBen, portBen, "alarm/c4"));
        panicKitchenPi = targets.http(
                new HttpCommand(POST, "192.168.42.105", 8001, "gpiomofo/trigger/panic-kitchen/fire", "", "")
        );

    }

    @Override
    public void setupLinks() {


        links
                .link(lightButtonExit, exitScene)
                .link(lightButton0, stop)
                .link(lightButton1, lightScene1)
                .link(lightButton2, lightScene2)
                .link(lightButton3, lightScene3)
                .link(lightButton4, lightScene4)
                .link(underwaterButton,     underwaterControl)
                .link(panicUnderwater,      panicUnderwaterLights)
                .link(panicUnderwaterHold,  panicUnderwaterPi)
                .link(panicBikes,           panicBikesLights)
                .link(panicBikesHold,       panicBikesPi)
                .link(panicKitchen,         panicKitchenLights)
                .link(panicKitchenHold,     panicKitchenPi)
        ;

        pixeltape.init(150).start();

//        try {
//            I2CBus bus = I2CFactory.getInstance(1);
//            final I2CDevice device = bus.getDevice(0x70);
//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    while ( true ) {
//                        try {
//                            System.out.println("Read: " + device.read());
//                            System.out.println("Read6: " + device.read(6));
//                            Thread.sleep(1000);
//                        } catch (IOException | InterruptedException e) {
//                            System.out.println("Fucked");
//                        }
//                    }
//                }
//            }, 1000);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}