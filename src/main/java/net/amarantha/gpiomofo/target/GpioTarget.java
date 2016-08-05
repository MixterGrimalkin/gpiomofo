package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.gpio.GpioService;

public class GpioTarget extends Target {

    @Inject private GpioService gpio;

    @Override
    public void onActivate() {
        if ( outputState!=null ) {
            gpio.write(outputPin, outputState);
        } else {
            gpio.toggle(outputPin);
        }
    }

    @Override
    public void onDeactivate() {
        if ( outputState!=null ) {
            gpio.write(outputPin, !outputState);
        } else {
            gpio.toggle(outputPin);
        }
    }

    private int outputPin;
    private Boolean outputState;

    public GpioTarget outputPin(int pinNumber, Boolean outputState) {
        if ( !gpio.isDigitalOutput(pinNumber) ) {
            gpio.setupDigitalOutput(pinNumber, outputState != null && !outputState);
        }
        this.outputPin = pinNumber;
        this.outputState = outputState;
        return this;
    }

    public int getOutputPin() {
        return outputPin;
    }

    public Boolean getOutputState() {
        return outputState;
    }

}
