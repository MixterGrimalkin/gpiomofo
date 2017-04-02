package net.amarantha.gpiomofo.service.gpio.touch;

import java.util.HashMap;
import java.util.Map;

public class TouchSensorMock extends TouchSensor {

    public TouchSensorMock() {
        super("Touch Sensor Mock");
    }

    public TouchSensorMock(String name ) { super(name); }

    private Map<Integer, Boolean> currentStates = new HashMap<>();

    @Override
    protected void init() {

    }

    @Override
    protected void scanPins() {
        currentStates.forEach(this::checkPinState);
    }

    public void setTouch(int pin, boolean on) {
        currentStates.put(pin, on);
    }

}
