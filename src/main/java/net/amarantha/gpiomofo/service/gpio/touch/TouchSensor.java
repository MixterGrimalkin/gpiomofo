package net.amarantha.gpiomofo.service.gpio.touch;

import net.amarantha.gpiomofo.service.Service;

import java.util.*;

public abstract class TouchSensor extends Service {

    protected Map<Integer, Boolean> lastStates = new HashMap<>();
    protected Map<Integer, List<TouchListener>> allListeners = new HashMap<>();

    private Timer scanTimer;
    private int refreshInterval = 20;

    public TouchSensor(String name) {
        super(name);
    }

    protected abstract void init();

    protected abstract void scanPins();

    protected void onStart() {
        init();
        scanTimer = new Timer();
        scanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                scanPins();
            }
        }, 0, refreshInterval);
    }

    protected void onStop() {
        if ( scanTimer!=null ) {
            scanTimer.cancel();
        }
    }

    void checkPinState(int pin, boolean state) {
        if (state != (lastStates.get(pin)!=null && lastStates.get(pin))) {
            fireListenersFor(pin, state);
            lastStates.put(pin, state);
        }
    }

    public void addListener(int pin, TouchListener listener) {
        List<TouchListener> listeners = allListeners.get(pin);
        if ( listeners==null ) {
            listeners = new LinkedList<>();
            allListeners.put(pin, listeners);
            lastStates.put(pin, false);
        }
        allListeners.get(pin).add(listener);
    }

    private void fireListenersFor(int pin, boolean currentState) {
        List<TouchListener> listeners = allListeners.get(pin);
        if ( listeners!=null ) {
            listeners.forEach((listener) -> listener.onTouch(currentState));
        }
    }

    public interface TouchListener {
        void onTouch(boolean state);
    }

}