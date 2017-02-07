package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.service.gpio.MPR121;

import javax.inject.Inject;

public class TouchTrigger extends Trigger {

    @Inject private MPR121 mpr121;

    public TouchTrigger setPin(int pin, boolean triggerState) {
        mpr121.addListener(pin, (state)->fire(state==triggerState));
        return this;
    }

}
