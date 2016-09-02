package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.pixeltape.pattern.ChasePattern;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;

public class TestScenario2 extends Scenario {

    @Override
    public void setupTriggers() {

        triggers.gpio("Button1", 0, PULL_UP, true);
        triggers.gpio("Button2", 1, PULL_UP, true);

    }

    @Override
    public void setupTargets() {

        targets.stopPixelTape("Stop");

        targets
            .pixelTape("Red-Chase", ChasePattern.class)
                .setColour(new RGB(255,0,0))
                .init(0, 100)
                .oneShot(false);

        targets
            .pixelTape("Green-Chase", ChasePattern.class)
                .setColour(new RGB(0,255,0))
                .setReverse(true)
                .init(0, 100)
                .oneShot(false);


    }

    @Override
    public void setupLinks() {

        links
                .link("Button1", "Stop", "Red-Chase")
                .link("Button2", "Stop", "Green-Chase")
        ;

    }

}
