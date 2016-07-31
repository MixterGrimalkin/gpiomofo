package net.amarantha.gpiomofo.gpio;

import com.pi4j.io.gpio.*;

public class GpioProviderImpl extends GpioProvider {

    private GpioController gpioController = GpioFactory.getInstance();

    @Override
    protected GpioPinDigitalOutput createDigitalOutputPin(int pinNumber, PinState state) {
        return gpioController.provisionDigitalOutputPin(availablePins.get(pinNumber), state==null ? DEFAULT_PIN_STATE : state );
    }

    @Override
    protected GpioPinDigitalInput createDigitalInputPin(int pinNumber, PinPullResistance resistance) {
        return gpioController.provisionDigitalInputPin(availablePins.get(pinNumber), resistance==null ? DEFAULT_PULL_RESISTANCE : resistance);
    }

    @Override
    public void write(int pinNumber, boolean high) {
        GpioPinDigitalOutput pin = digitalOutputPins.get(pinNumber);
        if ( pin!=null ) {
            pin.setState(high);
        }
    }

    @Override
    public boolean read(int pinNumber) {
        GpioPinDigital gpioPin = digitalInputPins.get(pinNumber);
        if ( gpioPin==null ) {
            gpioPin = digitalOutputPins.get(pinNumber);
        }
        if ( gpioPin!=null ) {
            return gpioPin.isHigh();
        }
        return false;
    }

    private static final PinState DEFAULT_PIN_STATE = PinState.LOW;
    private static final PinPullResistance DEFAULT_PULL_RESISTANCE = PinPullResistance.PULL_DOWN;

}
