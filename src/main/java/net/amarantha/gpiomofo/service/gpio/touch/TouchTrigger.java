package net.amarantha.gpiomofo.service.gpio.touch;

import net.amarantha.gpiomofo.core.trigger.Trigger;

import javax.inject.Inject;

public class TouchTrigger extends Trigger {

    @Inject private TouchSensor sensor;

    public TouchTrigger setPin(int pin, boolean triggerState) {
        sensor.addListener(pin, s -> fire(s == triggerState));
        return this;
    }

}
