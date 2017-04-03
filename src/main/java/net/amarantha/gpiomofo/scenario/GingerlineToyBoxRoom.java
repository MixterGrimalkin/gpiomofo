package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.service.pixeltape.pattern.BrightnessRipple;
import net.amarantha.gpiomofo.service.pixeltape.pattern.CyclicFade;
import net.amarantha.gpiomofo.service.pixeltape.pattern.SolidColour;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.osc.OscCommand;
import net.amarantha.utils.properties.Property;
import net.amarantha.utils.properties.PropertyGroup;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.scenario.GingerlinePanic.URL_PANIC_TOYBOX;
import static net.amarantha.utils.http.entity.HttpCommand.POST;

@PropertyGroup("Gingerline")
public class GingerlineToyBoxRoom extends Scenario {

    @Property("PanicIP")                private String  panicIp;
    @Property("PanicPort")              private int     panicPort;
    @Property("ButtonHoldTime")         private int     holdTime;
    @Property("LightingServerIP")       private String  lightingIp;
    @Property("LightingServerOscPort")  private int     lightingPort;
    @Property("MediaServerIP")          private String  mediaIp;
    @Property("MediaServerOscPort")     private int     mediaPort;
    @Property("C5-Amber")               private RGB     amber;
    @Property("C5-Green")               private RGB     green;
    @Property("C5-Purple")              private RGB     purple;
    @Property("C5-Blue")                private RGB     blue;

    private Trigger panicButton;
    private Trigger panicHold;

    private Trigger button1;
    private Trigger button2;
    private Trigger button3;
    private Trigger button4;

    private Trigger oscStop;
    private Trigger oscAmber;
    private Trigger oscBlue;
    private Trigger oscGreen;
    private Trigger oscPurple;
    private Trigger oscMix1;
    private Trigger oscMix2;
    private Trigger oscMix3;
    private Trigger oscSlowFade;
    private Trigger oscFastFade;
    private Trigger oscRippleFade;
    private Trigger oscKillFade;
    private Trigger oscEndOfWorld;

    @Override
    public void setupTriggers() {

        panicButton =   triggers.gpio("Panic",      2, PULL_UP, false);
        panicHold =     triggers.gpio("Panic-Hold", 2, PULL_UP, false).setHoldTime(1000);

        button1 =       triggers.gpio("Button1", 3, PULL_UP, false).setHoldTime(holdTime);
        button2 =       triggers.gpio("Button2", 4, PULL_UP, false).setHoldTime(holdTime);
        button3 =       triggers.gpio("Button3", 5, PULL_UP, false).setHoldTime(holdTime);
        button4 =       triggers.gpio("Button4", 6, PULL_UP, false).setHoldTime(holdTime);

        oscStop =       triggers.osc("Stop",            53000, "stop");
        oscAmber =      triggers.osc("Amber",           53000, "amber");
        oscBlue =       triggers.osc("Blue",            53000, "blue");
        oscGreen =      triggers.osc("Green",           53000, "green");
        oscPurple =     triggers.osc("Purple",          53000, "purple");
        oscMix1 =       triggers.osc("Mix-1",           53000, "mix1");
        oscMix2 =       triggers.osc("Mix-2",           53000, "mix2");
        oscMix3 =       triggers.osc("Mix-3",           53000, "mix3");
        oscSlowFade =   triggers.osc("Slow-Fade",       53000, "slow-fade");
        oscFastFade =   triggers.osc("Fast-Fade",       53000, "fast-fade");
        oscRippleFade = triggers.osc("Ripple-Fade",     53000, "ripple-fade");
        oscKillFade =   triggers.osc("Kill-Fade",       53000, "kill-fade");
        oscEndOfWorld = triggers.osc("End-Of-World",    53000, "end-of-world");

    }

    private Target panicLights;
    private Target panicMonitor;
    private Target stopAndClear;
    private Target osc1;
    private Target osc2;
    private Target osc3;
    private Target osc4;
    private Target amberScene;
    private Target blueScene;
    private Target greenScene;
    private Target purpleScene;
    private Target mix1;
    private Target mix2;
    private Target mix3;
    private Target slowFade;
    private Target fastFade;
    private Target rippleFade;
    private Target killFade;
    private Target endOfWorld;
    private Target cancelEndOfWorld;


    @Override
    public void setupTargets() {

        panicLights =   targets.osc(new OscCommand(lightingIp, lightingPort, "alarm/c5", 255));
        panicMonitor =  targets.http(new HttpCommand(POST, panicIp, panicPort, "gpiomofo/trigger", URL_PANIC_TOYBOX+"/fire", ""));

        Target stop = targets.stopPixelTape().setClear(false);
        stopAndClear = targets.stopPixelTape().setClear(true);

        osc1 = targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1401/start", 255));
        osc2 = targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1402/start", 255));
        osc3 = targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1403/start", 255));
        osc4 = targets.osc(new OscCommand(mediaIp, mediaPort, "cue/1404/start", 255));

        RGB white = new RGB(255,255,255);

        amberScene =
            targets.chain()
                .add(stop)
                .add(buildGlobes(amber))
            .build().oneShot(true);
        blueScene =
            targets.chain()
                .add(stop)
                .add(buildGlobes(blue))
            .build().oneShot(true);
        greenScene =
            targets.chain()
                .add(stop)
                .add(buildGlobes(green))
            .build().oneShot(true);
        purpleScene =
            targets.chain()
                .add(stop)
                .add(buildGlobes(purple))
            .build().oneShot(true);
        mix1 =
            targets.chain()
                .add(stop)
                .add(buildGlobes(purple, green, blue, green, amber))
            .build().oneShot(true);
        mix2 =
            targets.chain()
                .add(stop)
                .add(buildGlobes(blue, purple, amber, blue, green))
            .build().oneShot(true);
        mix3 =
            targets.chain()
                .add(stop)
                .add(buildGlobes(green, amber, blue, purple, amber))
            .build().oneShot(true);
        Target mix4 = targets.chain()
                .add(stop)
                .add(buildGlobes(green, blue, white, purple, amber))
                .build().oneShot(true);
        Target mix5 = targets.chain()
                .add(stop)
                .add(buildGlobes(purple, white, blue, white, green))
                .build().oneShot(true);
        Target mix6 = targets.chain()
                .add(stop)
                .add(buildGlobes(white, green, purple, amber, white))
                .build().oneShot(true);

        Target slowFadeInner = targets.pixelTape(CyclicFade.class)
                .setMin(0.5).setMax(1.0).setDelta(0.05).setRefreshInterval(50)
        .init(0, WHOLE_TAPE);

        Target fastFadeInner = targets.pixelTape(CyclicFade.class)
                .setMin(0.3).setMax(1.0).setDelta(0.25).setRefreshInterval(50)
        .init(0, WHOLE_TAPE);

        Target rippleFadeInner = targets.pixelTape(BrightnessRipple.class)
                .init(0, WHOLE_TAPE);

        Target reallyFastFadeInner = targets.pixelTape(CyclicFade.class)
                .setMin(0.2).setMax(1.0).setDelta(0.5).setRefreshInterval(50)
                .init(0, WHOLE_TAPE);

        slowFade = targets.chain()
                .add(reallyFastFadeInner.cancel())
                .add(fastFadeInner.cancel())
                .add(rippleFadeInner.cancel())
                .add(slowFadeInner)
                .build().oneShot(true);

        fastFade = targets.chain()
                .add(reallyFastFadeInner.cancel())
                .add(slowFadeInner.cancel())
                .add(rippleFadeInner.cancel())
                .add(fastFadeInner)
                .build().oneShot(true);

        rippleFade = targets.chain()
                .add(reallyFastFadeInner.cancel())
                .add(slowFadeInner.cancel())
                .add(fastFadeInner.cancel())
                .add(rippleFadeInner)
                .build().oneShot(true);

        Target reallyFastFade = targets.chain()
                .add(slowFadeInner.cancel())
                .add(fastFadeInner.cancel())
                .add(rippleFadeInner.cancel())
                .add(reallyFastFadeInner)
                .build().oneShot(true);

        killFade = targets.chain()
                .add(reallyFastFadeInner.cancel())
                .add(slowFadeInner.cancel())
                .add(fastFadeInner.cancel())
                .add(rippleFadeInner.cancel())
                .build().oneShot(true);

        int delay = 75;

        endOfWorld = targets.chain()
                .add(reallyFastFade)
                .add(delay, mix1)
                .add(delay, amberScene)
                .add(delay, mix2)
                .add(delay, mix3)
                .add(delay, mix4)
                .add(delay, mix5)
                .add(delay, mix6)
                .add(delay, blueScene)
                .add(delay, mix4)
                .add(delay, mix5)
                .add(delay, mix6)
                .add(delay, blueScene)
                .add(delay, mix2)
                .add(delay, amberScene)
                .add(delay, greenScene)
                .add(delay, mix4)
                .add(delay, mix5)
                .add(delay, mix6)
                .add(delay, mix4)
                .add(delay, mix5)
                .add(delay, mix6)
                .add(delay, purpleScene)
                .add(delay, mix3)
                .add(delay, mix1)
                .add(delay, purpleScene)
                .build().repeat(true).oneShot(true);

        cancelEndOfWorld = endOfWorld.cancel();

    }

    @Override
    public void setupLinks() {

        links
                .link(oscStop,          cancelEndOfWorld, stopAndClear)
                .link(oscAmber,         amberScene)
                .link(oscBlue,          blueScene)
                .link(oscGreen,         greenScene)
                .link(oscPurple,        purpleScene)
                .link(oscMix1,          mix1)
                .link(oscMix2,          mix2)
                .link(oscMix3,          mix3)
                .link(oscSlowFade,      slowFade)
                .link(oscFastFade,      fastFade)
                .link(oscRippleFade,    rippleFade)
                .link(oscKillFade,      killFade)
                .link(oscEndOfWorld,    endOfWorld)

                .link(panicButton,      panicLights)
                .link(panicHold,        panicMonitor)
                .link(button1,          osc1)
                .link(button2,          osc2)
                .link(button3,          osc3)
                .link(button4,          osc4)
        ;

    }

    private Target buildGlobes(RGB colour) {
        RGB[] colours = new RGB[5];
        double variation = 0.8;
        for ( int i=0; i<5; i++ ) {
            int redVar = (int)Math.round(variation * Math.max(30, colour.getRed()));
            int greenVar = (int)Math.round(variation * Math.max(30, colour.getGreen()));
            int blueVar = (int)Math.round(variation * Math.max(30, colour.getBlue()));
            double factor1 = (Math.random() * 2 * redVar) - redVar;
            double factor2 = (Math.random() * 2 * greenVar) - greenVar;
            double factor3 = (Math.random() * 2 * blueVar) - blueVar;
            int red = (int)Math.round(Math.max(0, Math.min(255, colour.getRed()+factor1)));
            int green = (int)Math.round(Math.max(0, Math.min(255, colour.getGreen()+factor2)));
            int blue = (int)Math.round(Math.max(0, Math.min(255, colour.getBlue()+factor3)));
            colours[i] = new RGB(red, green, blue);
        }
        return buildGlobes(colours[0], colours[1], colours[2], colours[3], colours[4]);
    }

    private Target buildGlobes(RGB colour1, RGB colour2, RGB colour3, RGB colour4, RGB colour5) {
        return targets.chain()
                .add(targets.pixelTape(SolidColour.class)
                        .setColour(colour1)
                        .init(BALL_1_S, SMALL_BALL))
                .add(targets.pixelTape(SolidColour.class)
                        .setColour(colour2)
                        .init(BALL_2_B, BIG_BALL))
                .add(targets.pixelTape(SolidColour.class)
                        .setColour(colour3)
                        .init(BALL_3_S, SMALL_BALL))
                .add(targets.pixelTape(SolidColour.class)
                        .setColour(colour4)
                        .init(BALL_4_B, BIG_BALL))
                .add(targets.pixelTape(SolidColour.class)
                        .setColour(colour5)
                        .init(BALL_5_S, SMALL_BALL))
                .build().oneShot(true);
    }

    private static final int SMALL_BALL = 7;
    private static final int BIG_BALL = 21;

    private static final int BALL_1_S = 0;
    private static final int BALL_2_B = SMALL_BALL;
    private static final int BALL_3_S = BALL_2_B + BIG_BALL;
    private static final int BALL_4_B = BALL_3_S + SMALL_BALL;
    private static final int BALL_5_S = BALL_4_B + BIG_BALL;
    private static final int WHOLE_TAPE = BALL_5_S + SMALL_BALL;

}
