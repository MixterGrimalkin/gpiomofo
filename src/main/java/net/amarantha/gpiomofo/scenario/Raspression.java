package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.core.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.HCSR04;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.service.osc.OscService;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.properties.Property;

public class Raspression extends Scenario {

    @Inject private HCSR04 blackSensor;
    @Inject private HCSR04 redSensor;
    @Inject private OscService osc;

    @Property("RaspressionClientIP") private String raspressionClientIP;

    @Override
    public void setupTriggers() {

        blackSensor.start(0, 2);
//        redSensor.start(4, 5);

    }

    private int lastBlack = -1;
    private int lastRed = -1;

    @Override
    public void setupTargets() {

        blackSensor.onReadSensor((value) -> {
            int midiValue = MathUtils.bound(0, 255, (int)Math.round(value*255));
            if ( midiValue !=lastBlack ) {
                osc.send(new OscCommand(raspressionClientIP, 5000, "black", midiValue ));
                lastBlack = midiValue ;
            }
        });
//        redSensor.onReadSensor((value) -> {
//            if ( value!=lastRed ) {
//                osc.send(new OscCommand("192.168.0.14", 5000, "red", value));
//                lastRed = value;
//            }
//        });
    }

    @Override
    public void setupLinks() {
    }
}
