package net.amarantha.gpiomofo.service.gpio;

import net.amarantha.gpiomofo.Gui;

import java.util.Map;

public class GpioServiceMock extends GpioServiceGUI {

    public GpioServiceMock() {
        super(null);
    }

    public GpioServiceMock(Gui gui) {
        super(null);
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
        inputLastState.clear();
        inputLastChange.clear();
        inputTimeouts.clear();
        onHighCallbacks.clear();
        onLowCallbacks.clear();
        onChangeCallbacks.clear();
        whenHighCallbacks.clear();
        whenLowCallbacks.clear();
    }

}
