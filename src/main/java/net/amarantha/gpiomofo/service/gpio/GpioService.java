package net.amarantha.gpiomofo.service.gpio;

import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;

import java.util.*;

import static java.lang.System.currentTimeMillis;

public abstract class GpioService {

    protected List<Integer> digitalInputs = new ArrayList<>();
    protected List<Integer> digitalOutputs = new ArrayList<>();

    protected Map<Integer, Boolean>   inputLastState = new HashMap<>();
    protected Map<Integer, Long>      inputLastChange = new HashMap<>();
    protected Map<Integer, Long>      inputTimeouts = new HashMap<>();

    protected Map<Integer, List<OnHigh>>    onHighCallbacks = new HashMap<>();
    protected Map<Integer, List<OnLow>>     onLowCallbacks = new HashMap<>();
    protected Map<Integer, List<OnChange>>  onChangeCallbacks = new HashMap<>();

    protected Map<Integer, List<OnHigh>>    whenHighCallbacks = new HashMap<>();
    protected Map<Integer, List<OnLow>>     whenLowCallbacks = new HashMap<>();

    public abstract boolean isValidPin(int pinNumber);

    public boolean isProvisioned(int pinNumber) {
        return isDigitalInput(pinNumber) || isDigitalOutput(pinNumber);
    }

    ///////////////////
    // Digital Input //
    ///////////////////

    public boolean read(int pinNumber) {
        return digitalRead(pinNumber);
    }

    protected abstract boolean digitalRead(int pinNumber);

    public void setupDigitalInput(int pinNumber) {
        setupDigitalInput(pinNumber, DEFAULT_PULL_RESISTANCE);
    }

    public void setupDigitalInput(int pinNumber, PinPullResistance resistance) {
        if ( !isValidPin(pinNumber) ) {
            throw new IllegalStateException("Pin " + pinNumber + " is not valid");
        }
        if ( isProvisioned(pinNumber) ) {
            throw new IllegalStateException("Pin " + pinNumber + " is already in use");
        }
        provisionDigitalInput(pinNumber, resistance);
        digitalInputs.add(pinNumber);
        inputLastState.put(pinNumber, digitalRead(pinNumber));
        inputLastChange.put(pinNumber, currentTimeMillis());
        inputTimeouts.put(pinNumber, 0L);
    }

    protected abstract void provisionDigitalInput(int pinNumber, PinPullResistance resistance);

    public boolean isDigitalInput(int pinNumber) {
        return digitalInputs.contains(pinNumber);
    }

    private void failIfNotDigitalInput(int pinNumber) {
        if ( !isDigitalInput(pinNumber) ) {
            throw new IllegalStateException("Pin " + pinNumber + " is not an input");
        }
    }

    ////////////////////
    // Digital Output //
    ////////////////////

    public void write(int pinNumber, boolean state) {
        failIfNotDigitalOutput(pinNumber);
        digitalWrite(pinNumber, state);
    }

    public void toggle(int pinNumber) {
        failIfNotDigitalOutput(pinNumber);
        digitalWrite(pinNumber, !digitalRead(pinNumber));
    }

    protected abstract void digitalWrite(int pinNumber, boolean high);

    public void setupDigitalOutput(int pinNumber) {
        setupDigitalOutput(pinNumber, DEFAULT_OUTPUT_STATE);
    }

    public void setupDigitalOutput(int pinNumber, boolean initialState) {
        if ( !isValidPin(pinNumber) ) {
            throw new IllegalStateException("Pin " + pinNumber + " is not valid");
        }
        if ( isProvisioned(pinNumber) ) {
            throw new IllegalStateException("Pin " + pinNumber + " is already in use");
        }
        provisionDigitalOutput(pinNumber, initialState ? PinState.HIGH : PinState.LOW);
        digitalOutputs.add(pinNumber);
    }

    protected abstract void provisionDigitalOutput(int pinNumber, PinState initialState);

    public boolean isDigitalOutput(int pinNumber) {
        return digitalOutputs.contains(pinNumber);
    }

    private void failIfNotDigitalOutput(int pinNumber) {
        if ( !isDigitalOutput(pinNumber) ) {
            throw new IllegalStateException("Pin " + pinNumber + " is not an output");
        }
    }

    ///////////////
    // Listeners //
    ///////////////

    public void onInputHigh(int pinNumber, OnHigh callback) {
        failIfNotDigitalInput(pinNumber);
        addCallback(onHighCallbacks, pinNumber, callback);
    }

    public void onInputLow(int pinNumber, OnLow callback) {
        failIfNotDigitalInput(pinNumber);
        addCallback(onLowCallbacks, pinNumber, callback);
    }

    public void onInputChange(int pinNumber, OnChange callback) {
        failIfNotDigitalInput(pinNumber);
        addCallback(onChangeCallbacks, pinNumber, callback);
    }

    public void whileInputHigh(int pinNumber, OnHigh callback) {
        failIfNotDigitalInput(pinNumber);
        addCallback(whenHighCallbacks, pinNumber, callback);
    }

    public void whileInputLow(int pinNumber, OnLow callback) {
        failIfNotDigitalInput(pinNumber);
        addCallback(whenLowCallbacks, pinNumber, callback);
    }

    private <C> void addCallback(Map<Integer, List<C>> map, int pinNumber, C callback) {
        List<C> callbacks = map.get(pinNumber);
        if ( callbacks==null ) {
            callbacks = new ArrayList<>();
            map.put(pinNumber, callbacks);
        }
        callbacks.add(callback);
    }

    ///////////////////
    // Input Monitor //
    ///////////////////

    private Timer monitorTimer;

    public void startInputMonitor() {
        startInputMonitor(DEFAULT_SCAN_PERIOD);
    }

    public void startInputMonitor(long period) {
        System.out.println("Starting GPIO Monitor...");
        stopInputMonitor();
        monitorTimer = new Timer();
        monitorTimer.schedule(new TimerTask() {
            @Override public void run() {
                scanPins();
            }
        }, 0, period);
    }

    public void stopInputMonitor() {
        if ( monitorTimer!=null ) {
            monitorTimer.cancel();
            monitorTimer = null;
        }
    }

    public void shutdown() {
        stopInputMonitor();
        for ( Integer pinNumber : digitalOutputs ) {
            write(pinNumber, false);
        }
    }

    protected void scanPins() {

        for ( Integer pinNumber : digitalInputs ) {

            boolean currentState = digitalRead(pinNumber);
            boolean lastState = inputLastState.get(pinNumber);

            // Continuous State Callbacks
            if ( currentState ) {
                if (whenHighCallbacks.containsKey(pinNumber)) {
                    whenHighCallbacks.get(pinNumber).forEach(OnHigh::onHigh);
                }
            } else {
                if (whenLowCallbacks.containsKey(pinNumber)) {
                    whenLowCallbacks.get(pinNumber).forEach(OnLow::onLow);
                }
            }

            // State Changed Callbacks
            if ( currentState != lastState && isReady(pinNumber) ) {

                inputLastState.put(pinNumber, currentState);
                inputLastChange.put(pinNumber, currentTimeMillis());

                if ( onChangeCallbacks.containsKey(pinNumber) ) {
                    for ( OnChange callback : onChangeCallbacks.get(pinNumber) ) {
                        callback.onChange(currentState);
                    }
                }
                if ( currentState ) {
                    if ( onHighCallbacks.containsKey(pinNumber) ) {
                        onHighCallbacks.get(pinNumber).forEach(OnHigh::onHigh);
                    }
                } else {
                    if ( onLowCallbacks.containsKey(pinNumber) ) {
                        onLowCallbacks.get(pinNumber).forEach(OnLow::onLow);
                    }
                }
            }
        }
    }

    private boolean isReady(int pinNumber) {
        return currentTimeMillis()- inputLastChange.get(pinNumber) > inputTimeouts.get(pinNumber);
    }

    //////////////
    // Callback //
    //////////////

    public interface OnHigh {
        void onHigh();
    }

    public interface OnLow {
        void onLow();
    }

    public interface OnChange {
        void onChange(boolean state);
    }

    ///////////////
    // Constants //
    ///////////////

    private static final int                DEFAULT_SCAN_PERIOD = 10;
    private static final boolean            DEFAULT_OUTPUT_STATE = false;
    private static final PinPullResistance  DEFAULT_PULL_RESISTANCE = PinPullResistance.PULL_DOWN;

}
