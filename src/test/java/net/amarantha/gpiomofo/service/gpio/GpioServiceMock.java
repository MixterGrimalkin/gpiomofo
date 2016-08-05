package net.amarantha.gpiomofo.service.gpio;

import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;

import java.util.HashMap;
import java.util.Map;

public class GpioServiceMock extends GpioService {

    private Map<Integer, Boolean> inputStates = new HashMap<>();
    private Map<Integer, Boolean> outputStates = new HashMap<>();

    @Override
    public boolean isValidPin(int pinNumber) {
        return pinNumber>=0 && pinNumber<=29;
    }

    @Override
    protected boolean digitalRead(int pinNumber) {
        return inputStates.get(pinNumber)==null ? outputStates.get(pinNumber) : inputStates.get(pinNumber);
    }

    @Override
    protected void provisionDigitalInput(int pinNumber, PinPullResistance resistance) {
        inputStates.put(pinNumber, false);
    }

    @Override
    protected void digitalWrite(int pinNumber, boolean state) {
        outputStates.put(pinNumber, state);
    }

    @Override
    protected void provisionDigitalOutput(int pinNumber, PinState initialState) {
        outputStates.put(pinNumber, initialState==PinState.HIGH);
    }

    /////////////
    // Testing //
    /////////////

    @Override
    public void scanPins() {
        super.scanPins();
    }

    public void setInput(int pinNumber, boolean state) {
        if ( !inputStates.containsKey(pinNumber) ) {
            throw new IllegalStateException("TESTING ERROR: Pin " + pinNumber + " is not an input");
        }
        inputStates.put(pinNumber, state);
        scanPins();
    }

    public boolean getOutput(int pinNumber) {
        if ( !outputStates.containsKey(pinNumber) ) {
            throw new IllegalStateException("TESTING ERROR: Pin " + pinNumber + " is not an output");
        }
        return outputStates.get(pinNumber);
    }

    public Map<Integer, Boolean> getOutputStates() {
        return outputStates;
    }

    public void reset() {
        inputStates.clear();
        outputStates.clear();
        digitalInputs.clear();
        digitalOutputs.clear();
        inputLastChange.clear();
        inputLastChange.clear();
        inputTimeouts.clear();
        onHighCallbacks.clear();
        onLowCallbacks.clear();
        onChangeCallbacks.clear();
        whenHighCallbacks.clear();
        whenLowCallbacks.clear();
    }

}
