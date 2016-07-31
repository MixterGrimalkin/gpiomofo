package net.amarantha.gpiomofo.target;

import com.pi4j.io.gpio.PinPullResistance;

public class TriggerConfig {

    private final int triggerPin;
    private final PinPullResistance resistance;
    private final boolean triggerState;

    public TriggerConfig(int triggerPin, PinPullResistance resistance, boolean triggerState) {
        this.triggerPin = triggerPin;
        this.resistance = resistance;
        this.triggerState = triggerState;
    }

    public int getTriggerPin() {
        return triggerPin;
    }

    public PinPullResistance getResistance() {
        return resistance;
    }

    public boolean getTriggerState() {
        return triggerState;
    }
}
