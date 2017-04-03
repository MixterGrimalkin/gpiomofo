package net.amarantha.gpiomofo.trigger;

import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.annotation.Service;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.trigger.Trigger;

public class GpioTrigger extends Trigger {

    @Service
    private GpioService gpio;

    @Parameter("pin")
    private int pinNumber;

    @Parameter("resistance")
    private PinPullResistance resistance;

    @Parameter("triggerState")
    private boolean triggerState;

    public GpioTrigger setTriggerPin(int pinNumber, PinPullResistance resistance, boolean triggerState) {
        this.pinNumber = pinNumber;
        this.resistance = resistance;
        this.triggerState = triggerState;
        enable();
        return this;
    }

    public Trigger enable() {
        if (!gpio.isDigitalInput(pinNumber)) {
            gpio.setupDigitalInput(pinNumber, resistance);
        }
        gpio.onInputChange(pinNumber, (state) -> fire(state == triggerState));
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
