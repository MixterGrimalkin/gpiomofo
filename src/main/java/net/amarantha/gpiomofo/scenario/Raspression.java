package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensorHCSR04;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.service.Service;

@PropertyGroup("Raspression")
public class Raspression extends Scenario {

    @Service private OscService osc;

    @Inject private RangeSensorHCSR04 blackSensor;
    @Inject private RangeSensorHCSR04 redSensor;

    @Property("RaspressionClientIP") private String raspressionClientIP;

    private int lastBlack = -1;
    private int lastRed = -1;

    @Override
    public void setup() {

//        blackSensor.start(0, 2);
//        redSensor.start(4, 5);

//        blackSensor.onReadSensor((value) -> {
//            int midiValue = MathUtils.bound(0, 255, (int)Math.round(value*255));
//            if ( midiValue !=lastBlack ) {
//                osc.send(new OscCommand(raspressionClientIP, 5000, "black", midiValue ));
//                lastBlack = midiValue ;
//            }
//        });
//        redSensor.onReadSensor((value) -> {
//            if ( value!=lastRed ) {
//                osc.send(new OscCommand("192.168.0.14", 5000, "red", value));
//                lastRed = value;
//            }
//        });
    }

}
