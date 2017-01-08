package net.amarantha.gpiomofo.trigger;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.service.gpio.GpioService;

public class GpioTrigger extends Trigger {

    @Inject private GpioService gpio;

    private int pinNumber;
    private PinPullResistance resistance;
    private boolean triggerState;

    public GpioTrigger setTriggerPin(int pinNumber, PinPullResistance resistance, boolean triggerState) {
        if ( !gpio.isDigitalInput(pinNumber) ) {
            gpio.setupDigitalInput(pinNumber, resistance);
        }
        gpio.onInputChange(pinNumber, (state) -> fire(state==triggerState));
        this.pinNumber = pinNumber;
        this.resistance = resistance;
        this.triggerState = triggerState;
        return this;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public PinPullResistance getResistance() {
        return resistance;
    }

    public boolean getTriggerState() {
        return triggerState;
    }

}
