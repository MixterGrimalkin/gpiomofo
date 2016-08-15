package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.RotatingBars;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

public class GingerlineBriefingRoom extends Scenario {

    private Trigger panicButton;

    private Target panicTarget;

    @Inject private PixelTapeController tape;

    @Inject private RotatingBars domeBackground1;
    @Inject private RotatingBars domeBackground2;
    @Inject private RotatingBars domeBackground3;
    @Inject private RotatingBars domeBackground4;

    @Inject private RotatingBars domeActive1;
    @Inject private RotatingBars domeActive2;
    @Inject private RotatingBars domeActive3;
    @Inject private RotatingBars domeActive4;

    @Override
    public void setupTriggers() {

//        panicButton = triggers.gpio("Panic", 0, PULL_DOWN, true);

    }

    @Override
    public void setupTargets() {

//        panicTarget = targets.osc("Panic", new OscCommand(PANIC_IP, PANIC_OSC_PORT, PANIC_BRIEFING_ROOM));

    }

    @Override
    public void setupLinks() {

//        WS281x ws = new WS281x(18, 255, 9);
//        for ( int i=0; i<9; i++) {
//            ws.setPixelColourRGB(i, 255,0,0);
//        }
//        ws.render();

        int domeSize = 47;
        int refresh = 350;

        RGB colour = RGB.WHITE;//.withBrightness(0.8);
//        domeBackground1.setColour(colour).setRefreshInterval(refresh).init(domeSize);
//        domeBackground2.setColour(colour).setRefreshInterval(refresh+5).init(domeSize);
//        domeBackground3.setColour(colour).setRefreshInterval(refresh+10).init(domeSize);
//        domeBackground4.setColour(colour).setRefreshInterval(refresh+15).init(domeSize);

        domeActive1.setColour(new RGB(255, 100, 0)).setRefreshInterval(8).init(domeSize);
        domeActive2.setColour(new RGB(100, 0, 255)).setRefreshInterval(10).init(domeSize);
        domeActive3.setColour(new RGB(50, 80, 255)).setRefreshInterval(12).init(domeSize);
        domeActive4.setColour(new RGB(255, 0, 100)).setRefreshInterval(14).init(domeSize);

        tape.addPattern(0, domeActive1);
        tape.addPattern(domeSize, domeActive2);
        tape.addPattern(2*domeSize, domeActive3);
        tape.addPattern(3*domeSize, domeActive4);

//        tape.addPattern(0, domeBackground1);
//        tape.addPattern(domeSize, domeBackground2);
//        tape.addPattern(2*domeSize, domeBackground3);
//        tape.addPattern(3*domeSize, domeBackground4);
//
        tape.init(4*domeSize);
        tape.start();


//        links.link(panicButton,   panicTarget);

    }
}
