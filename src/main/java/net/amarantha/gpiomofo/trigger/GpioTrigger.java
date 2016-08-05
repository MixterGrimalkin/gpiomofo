package net.amarantha.gpiomofo.trigger;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.service.gpio.GpioService;

public class GpioTrigger extends Trigger {

    @Inject private GpioService gpio;

    public GpioTrigger setTriggerPin(int pinNumber, PinPullResistance resistance, boolean triggerState) {
        if ( !gpio.isDigitalInput(pinNumber) ) {
            gpio.setupDigitalInput(pinNumber, resistance);
        }
        gpio.onInputChange(pinNumber, (state) -> fire(state==triggerState));
        this.triggerState = triggerState;
        return this;
    }

    private boolean triggerState;

    public boolean getTriggerState() {
        return triggerState;
    }

}
