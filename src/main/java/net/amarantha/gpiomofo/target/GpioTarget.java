package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.gpio.GpioProvider;

public class GpioTarget extends AbstractTarget {

    @Inject private GpioProvider gpio;

    private int pinNumber;
    private Boolean outputState;

    @Override
    public void onActivate() {
        if ( outputState !=null ) {
            gpio.write(pinNumber, outputState);
        } else {
            gpio.toggle(pinNumber);
        }
    }

    @Override
    public void onDeactivate() {
        if ( outputState !=null ) {
            gpio.write(pinNumber, !outputState);
        } else {
            gpio.toggle(pinNumber);
        }
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public GpioTarget outputPin(int pinNumber) {
        this.pinNumber = pinNumber;
        return this;
    }

    public Boolean getOutputState() {
        return outputState;
    }

    public GpioTarget outputState(Boolean outputState) {
        this.outputState = outputState;
        return this;
    }
}
