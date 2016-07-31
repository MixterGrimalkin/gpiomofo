package net.amarantha.gpiomofo.gpio;

import com.pi4j.io.gpio.*;

import java.util.*;

import static com.pi4j.io.gpio.RaspiPin.*;
import static java.lang.System.currentTimeMillis;

public abstract class GpioProvider {

    protected Map<Integer, Pin> availablePins = new HashMap<>();
    protected Map<Integer, GpioPinDigitalOutput>  digitalOutputPins = new HashMap<>();
    protected Map<Integer, GpioPinDigitalInput>   digitalInputPins = new HashMap<>();

    private Map<Integer, List<OnHigh>>    onHighCallbacks = new HashMap<>();
    private Map<Integer, List<OnLow>>     onLowCallbacks = new HashMap<>();
    private Map<Integer, List<OnChange>>  onChangeCallbacks = new HashMap<>();

    private Map<Integer, List<OnHigh>>    whenHighCallbacks = new HashMap<>();
    private Map<Integer, List<OnLow>>     whenLowCallbacks = new HashMap<>();

    protected Map<Integer, Boolean>   lastInputState = new HashMap<>();
    protected Map<Integer, Long>      lastInputStateChange = new HashMap<>();
    protected Map<Integer, Long>      pinTimeouts = new HashMap<>();

    public GpioProvider() {
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

    ///////////////////
    // Input Monitor //
    ///////////////////

    private Timer monitorTimer;

    public void startInputMonitor() {
        startInputMonitor(DEFAULT_SCAN_PERIOD);
    }

    public void startInputMonitor(long period) {
        stopInputMonitor();
        monitorTimer = new Timer();
        monitorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
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

    private void scanPins() {

        for ( Integer pinNumber : digitalInputPins.keySet() ) {

            boolean lastState = lastInputState.get(pinNumber);
            boolean currentState = read(pinNumber);

            // State Changed Callbacks
            if ( lastState!=currentState && currentTimeMillis()-lastInputStateChange.get(pinNumber) > pinTimeouts.get(pinNumber) ) {
                lastInputState.put(pinNumber, currentState);
                lastInputStateChange.put(pinNumber, currentTimeMillis());
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

        }

    }

    ////////////
    // Output //
    ////////////

    public void digitalOutput(int pinNumber) {
        getOrCreateDigitalOutputPin(pinNumber, null);
    }

    public void digitalOutput(int pinNumber, boolean state) {
        getOrCreateDigitalOutputPin(pinNumber, state ? PinState.HIGH : PinState.LOW );
    }

    ///////////
    // Input //
    ///////////

    public void digitalInput(int pinNumber) {
        getOrCreateDigitalInputPin(pinNumber, null);
    }

    public void digitalInput(int pinNumber, PinPullResistance resistance) {
        getOrCreateDigitalInputPin(pinNumber, resistance);
    }

    public void setPinTimeout(int pinNumber, long timeout) {
        pinTimeouts.put(pinNumber, timeout);
    }

    ///////////////
    // Listeners //
    ///////////////

    public void onInputHigh(int pinNumber, OnHigh callback) {
        getOrCreateDigitalInputPin(pinNumber, null);
        addCallback(onHighCallbacks, pinNumber, callback);
    }

    public void onInputLow(int pinNumber, OnLow callback) {
        getOrCreateDigitalInputPin(pinNumber, null);
        addCallback(onLowCallbacks, pinNumber, callback);
    }

    public void onInputChange(int pinNumber, OnChange callback) {
        getOrCreateDigitalInputPin(pinNumber, null);
        addCallback(onChangeCallbacks, pinNumber, callback);
    }

    public void whileInputHigh(int pinNumber, OnHigh callback) {
        getOrCreateDigitalInputPin(pinNumber, null);
        addCallback(whenHighCallbacks, pinNumber, callback);
    }

    public void whileInputLow(int pinNumber, OnLow callback) {
        getOrCreateDigitalInputPin(pinNumber, null);
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

    //////////////////
    // Pin Creation //
    //////////////////

    protected void getOrCreateDigitalOutputPin(int pinNumber, PinState state) {
        GpioPinDigitalOutput pin = digitalOutputPins.get(pinNumber);
        if ( pin==null ) {
            if ( isValidPin(pinNumber) ) {
                if ( digitalInputPins.containsKey(pinNumber) ) {
                    throw new IllegalStateException("Pin " + pinNumber + " is configured as an input");
                }
                pin = createDigitalOutputPin(pinNumber, state);
                digitalOutputPins.put(pinNumber, pin);
                System.out.println("Provisioned Digital Output Pin " + pinNumber);
            } else {
                throw new IllegalStateException("Pin " + pinNumber + " is not valid");
            }
        } else {
            if (state != null) {
                write(pinNumber, state.isHigh());
            }
        }
    }

    protected void getOrCreateDigitalInputPin(int pinNumber, PinPullResistance resistance) {
        GpioPinDigitalInput pin = digitalInputPins.get(pinNumber);
        if ( pin==null ) {
            if ( isValidPin(pinNumber) ) {
                if ( digitalOutputPins.containsKey(pinNumber) ) {
                    throw new IllegalStateException("Pin " + pinNumber + " is configured as an output");
                }
                pin = createDigitalInputPin(pinNumber, resistance);
                digitalInputPins.put(pinNumber, pin);
                lastInputStateChange.put(pinNumber, currentTimeMillis());
                lastInputState.put(pinNumber, read(pinNumber));
                pinTimeouts.put(pinNumber, 0L);
                System.out.println("Provisioned Digital Input Pin " + pinNumber);
            } else {
                throw new IllegalStateException("Pin " + pinNumber + " is not valid");
            }
        }
    }

    protected abstract GpioPinDigitalOutput createDigitalOutputPin(int pinNumber, PinState state);

    protected abstract GpioPinDigitalInput createDigitalInputPin(int pinNumber, PinPullResistance resistance);

    public abstract void write(int pinNumber, boolean high);

    public abstract boolean read(int pinNumber);

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

    /////////////
    // Utility //
    /////////////

    public boolean isValidPin(int pinNumber) {
        return availablePins.containsKey(pinNumber);
    }

    public static final int DEFAULT_SCAN_PERIOD = 10;

}
