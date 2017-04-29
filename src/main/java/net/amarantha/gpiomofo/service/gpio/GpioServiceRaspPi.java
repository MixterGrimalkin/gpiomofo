package net.amarantha.gpiomofo.service.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;

import java.util.HashMap;
import java.util.Map;

import static com.pi4j.io.gpio.RaspiPin.*;

public class GpioServiceRaspPi extends GpioService {

    private GpioController gpioController = GpioFactory.getInstance();

    protected Map<Integer, Pin> availablePins = new HashMap<>();

    public GpioServiceRaspPi() {
        super("Raspberry Pi GPIO Service");
        WiringPiSetup.init();
        availablePins.put( 0, GPIO_00);
        availablePins.put( 1, GPIO_01);
        availablePins.put( 2, GPIO_02);
        availablePins.put( 3, GPIO_03);
        availablePins.put( 4, GPIO_04);
        availablePins.put( 5, GPIO_05);
        availablePins.put( 6, GPIO_06);
        availablePins.put( 7, GPIO_07);
        availablePins.put( 8, GPIO_08);
        availablePins.put( 9, GPIO_09);
        availablePins.put(10, GPIO_10);
        availablePins.put(11, GPIO_11);
        availablePins.put(12, GPIO_12);
        availablePins.put(13, GPIO_13);
        availablePins.put(14, GPIO_14);
        availablePins.put(15, GPIO_15);
        availablePins.put(16, GPIO_16);
        availablePins.put(17, GPIO_17);
        availablePins.put(18, GPIO_18);
        availablePins.put(19, GPIO_19);
        availablePins.put(20, GPIO_20);
        availablePins.put(21, GPIO_21);
        availablePins.put(22, GPIO_22);
        availablePins.put(23, GPIO_23);
        availablePins.put(24, GPIO_24);
        availablePins.put(25, GPIO_25);
        availablePins.put(26, GPIO_26);
        availablePins.put(27, GPIO_27);
        availablePins.put(28, GPIO_28);
        availablePins.put(29, GPIO_29);
    }

    @Override
    public boolean isValidPin(int pinNumber) {
        return availablePins.containsKey(pinNumber);
    }

    @Override
    protected boolean digitalRead(int pinNumber) {
        return Gpio.digitalRead(pinNumber)==1;
    }

    @Override
    protected void provisionDigitalInput(int pinNumber, PinPullResistance resistance) {
        gpioController.provisionDigitalInputPin(availablePins.get(pinNumber), resistance);
    }

    @Override
    protected void digitalWrite(int pinNumber, boolean high) {
        Gpio.digitalWrite(pinNumber, high);
    }

    @Override
    protected void provisionDigitalOutput(int pinNumber, PinState initialState) {
        gpioController.provisionDigitalOutputPin(availablePins.get(pinNumber), initialState);
    }

}
