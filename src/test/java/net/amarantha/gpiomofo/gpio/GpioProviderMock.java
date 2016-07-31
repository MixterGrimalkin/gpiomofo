package net.amarantha.gpiomofo.gpio;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;

public class GpioProviderMock extends GpioProvider {

    @Override
    protected GpioPinDigitalOutput createDigitalOutputPin(int pinNumber, PinState state) {
        return null;
    }

    @Override
    protected GpioPinDigitalInput createDigitalInputPin(int pinNumber, PinPullResistance resistance) {
        return null;
    }

    @Override
    public void write(int pinNumber, boolean high) {

    }

    @Override
    public boolean read(int pinNumber) {
        return false;
    }
}
