package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensor;
import net.amarantha.utils.service.Service;

public class RangeTrigger extends ContinuousTrigger {

    @Parameter("triggerPin") private int trigger;
    @Parameter("echoPin") private int echo;

    @Service private RangeSensor sensor;

    @Override
    public void enable() {
        sensor.addSensor(trigger, echo, this::fireCallbacks);
    }

}
