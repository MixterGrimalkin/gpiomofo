package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.utils.service.Service;

public class GpioTarget extends Target {

    @Service private GpioService gpio;

    @Parameter("pin") private int pinNumber;
    @Parameter("activeState") private Boolean activeState;

    @Override
    public void enable() {
        if ( !gpio.isDigitalOutput(pinNumber) ) {
            gpio.setupDigitalOutput(pinNumber, activeState != null && !activeState);
        }
    }

    @Override
    public void onActivate() {
        if ( activeState !=null ) {
            gpio.write(pinNumber, activeState);
        } else {
            gpio.toggle(pinNumber);
        }
    }

    @Override
    public void onDeactivate() {
        if ( activeState !=null ) {
            gpio.write(pinNumber, !activeState);
        } else {
            gpio.toggle(pinNumber);
        }
    }

    public GpioTarget outputPin(int pinNumber, Boolean activeState) {
        this.pinNumber = pinNumber;
        this.activeState = activeState;
        enable();
        return this;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public Boolean getActiveState() {
        return activeState;
    }

}
