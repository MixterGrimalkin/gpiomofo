package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.service.gpio.touch.TouchSensor;
import net.amarantha.utils.service.Service;

public class TouchTrigger extends Trigger {

    @Service private TouchSensor sensor;

    @Parameter("pin")           private int pin;
    @Parameter("triggerState")  private boolean triggerState;

    @Override
    public void enable() {
        sensor.addListener(pin, s -> fire(s == triggerState));
    }

}
