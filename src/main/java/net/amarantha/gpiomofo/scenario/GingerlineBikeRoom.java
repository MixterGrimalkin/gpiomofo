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
import net.amarantha.gpiomofo.utility.GpioMofoProperties;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.scenario.GingerlinePanic.*;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

public class GingerlineBikeRoom extends Scenario {

    @Inject private GpioMofoProperties props;
    @Inject private PixelTapeController pixeltape;

    private Trigger panicChamber2;
    private Trigger panicChamber2Hold;
    private Trigger panicChamber3;
    private Trigger panicChamber3Hold;
    private Trigger panicChamber4;
    private Trigger panicChamber4Hold;

    private Trigger buttonChamber2Green;

    private Trigger oscPixelTape0;
    private Trigger oscPixelTape1;
    private Trigger oscPixelTape2;
    private Trigger oscPixelTape3;
    private Trigger oscPixelTape4;
    private Trigger oscPixelTapeExit;

    private Trigger buttonChamber3a;
    private Trigger buttonChamber3b;
    private Trigger buttonChamber3c;
    private Trigger buttonChamber3d;
    private Trigger buttonChamber3e;

    @Override
    public void setupTriggers() {

        panicChamber2 =       triggers.gpio("C2-Panic",         2, PULL_UP, false);
        panicChamber2Hold =   triggers.gpio("C2-Panic-Hold",    2, PULL_UP, false).setHoldTime(1000);
        panicChamber3 =       triggers.gpio("C3-Panic",         3, PULL_UP, false);
        panicChamber3Hold =   triggers.gpio("C3-Panic-Hold",    3, PULL_UP, false).setHoldTime(1000);
        panicChamber4 =       triggers.gpio("C4-Panic",         4, PULL_UP, false);
        panicChamber4Hold =   triggers.gpio("C4-Panic-Hold",    4, PULL_UP, false).setHoldTime(1000);

        buttonChamber2Green = triggers.gpio("C2-Button-Green",  5, PULL_UP, false);

        oscPixelTape0 =     triggers.osc("Tape-0", 53000, "bike-lights-0");
        oscPixelTape1 =     triggers.osc("Tape-1", 53000, "bike-lights-1");
        oscPixelTape2 =     triggers.osc("Tape-2", 53000, "bike-lights-2");
        oscPixelTape3 =     triggers.osc("Tape-3", 53000, "bike-lights-3");
        oscPixelTape4 =     triggers.osc("Tape-4", 53000, "bike-lights-4");
        oscPixelTapeExit =  triggers.osc("Tape-5", 53000, "bike-exit");

        buttonChamber3a =   triggers.gpio("C3-Button-A", 6,  PULL_UP, false);
        buttonChamber3b =   triggers.gpio("C3-Button-B", 7,  PULL_UP, false);
        buttonChamber3c =   triggers.gpio("C3-Button-C", 12, PULL_UP, false);
        buttonChamber3d =   triggers.gpio("C3-Button-D", 13, PULL_UP, false);
        buttonChamber3e =   triggers.gpio("C3-Button-E", 14, PULL_UP, false);

    }

    private Target panicLightsChamber2;
    private Target panicMonitorChamber2;
    private Target panicLightsChamber3;
    private Target panicMonitorChamber3;
    private Target panicLightsChamber4;
    private Target panicMonitorChamber4;

    private Target underwaterControl;

    private Target lightStop;
    private Target lightScene1;
    private Target lightScene2;
    private Target lightScene3;
    private Target lightScene4;
    private Target lightSceneExit;

    private Target bikeControl1;
    private Target bikeControl2;
    private Target bikeControl3;
    private Target bikeControl4;
    private Target bikeControl5;

    @Override
    public void setupTargets() {

        String lightingIp = props.lightingIp();
        int lightingPort = props.lightingOscPort();

        panicLightsChamber2 =   targets.osc(new OscCommand(lightingIp, lightingPort, "alarm/c2", 255));
        panicMonitorChamber2 =  targets.http(PANIC.withPath(URL_PANIC_UNDERWATER+"/fire"));

        panicLightsChamber3 =   targets.osc(new OscCommand(lightingIp, lightingPort, "alarm/c3", 255));
        panicMonitorChamber3 =  targets.http(PANIC.withPath(URL_PANIC_BIKES+"/fire"));

        panicLightsChamber4 =   targets.osc(new OscCommand(lightingIp, lightingPort, "alarm/c4", 255));
        panicMonitorChamber4 =  targets.http(PANIC.withPath(URL_PANIC_KITCHEN+"/fire"));

        underwaterControl =     targets.osc(new OscCommand(lightingIp, lightingPort, "alarm/c2slide", 255));

        String mediaIp = props.mediaIp();
        int mediaPort = props.mediaOscPort();

        bikeControl1 =          targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1301/start", 255));
        bikeControl2 =          targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1302/start", 255));
        bikeControl3 =          targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1303/start", 255));
        bikeControl4 =          targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1304/start", 255));
        bikeControl5 =          targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1305/start", 255));

        ////////////////
        // Pixel Tape //
        ////////////////

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


        lightStop = targets.stopPixelTape();

        lightScene1 = targets.chain("One")
                .add(lightStop)
                .add(fade1)
                .add(bars1)
                .build().oneShot(true);

        lightScene2 = targets.chain("Two")
                .add(lightStop)
                .add(fade2)
                .add(bars2)
                .build().oneShot(true);

        lightScene3 = targets.chain("Three")
                .add(lightStop)
                .add(fade3)
                .add(bars3)
                .build().oneShot(true);

        lightScene4 = targets.chain("Four")
                .add(lightStop)
                .add(fade4)
                .add(bars4)
                .build().oneShot(true);

        lightSceneExit = targets.chain("Exit")
                .add(lightStop)
                .add(exit)
                .build().oneShot(true);

    }

    @Override
    public void setupLinks() {


        links
                .link(oscPixelTape0,        lightStop)
                .link(oscPixelTape1,        lightScene1)
                .link(oscPixelTape2,        lightScene2)
                .link(oscPixelTape3,        lightScene3)
                .link(oscPixelTape4,        lightScene4)
                .link(oscPixelTapeExit,     lightSceneExit)

                .link(buttonChamber3a,      bikeControl1)
                .link(buttonChamber3b,      bikeControl2)
                .link(buttonChamber3c,      bikeControl3)
                .link(buttonChamber3d,      bikeControl4)
                .link(buttonChamber3e,      bikeControl5)

                .link(panicChamber2,        panicLightsChamber2)
                .link(panicChamber2Hold,    panicMonitorChamber2)
                .link(panicChamber3,        panicLightsChamber3)
                .link(panicChamber3Hold,    panicMonitorChamber3)
                .link(panicChamber4,        panicLightsChamber4)
                .link(panicChamber4Hold,    panicMonitorChamber4)

                .link(buttonChamber2Green,  underwaterControl)
        ;

        pixeltape
                .init(150)
                .start();

    }

}