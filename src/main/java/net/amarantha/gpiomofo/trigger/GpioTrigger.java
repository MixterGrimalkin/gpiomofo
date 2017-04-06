package net.amarantha.gpiomofo.trigger;

import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.utils.service.Service;

public class GpioTrigger extends Trigger {

    @Service private GpioService gpio;

    @Parameter("pin")           private int pinNumber;
    @Parameter("resistance")    private PinPullResistance resistance;
    @Parameter("triggerState")  private boolean triggerState;

    @Override
    public void enable() {
        if (!gpio.isDigitalInput(pinNumber)) {
            gpio.setupDigitalInput(pinNumber, resistance);
        }
        gpio.onInputChange(pinNumber, (state) -> fire(state == triggerState));
    }

}
