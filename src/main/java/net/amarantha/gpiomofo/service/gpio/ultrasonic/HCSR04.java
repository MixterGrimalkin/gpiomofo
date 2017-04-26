package net.amarantha.gpiomofo.service.gpio.ultrasonic;

import com.google.inject.Inject;
import net.amarantha.utils.task.TaskService;

import java.util.LinkedList;
import java.util.List;

public class HCSR04 extends RangeTrigger {

    // TRIG 12, ECHO 13

    static {
        System.loadLibrary("hc-sr04");
    }

    @Inject private TaskService tasks;

    private final static int SAMPLES = 1;

    private final static int MIN_VALUE = 200;
    private final static int MAX_VALUE = 13000;

    public void start(int trigger, int echo) {
        init(trigger, echo);
        tasks.addRepeatingTask(this, 100, ()->readSensor(trigger, echo));
    }

    private void readSensor(int trigger, int echo) {
        double total = 0;
        for ( int s=0; s<SAMPLES; s++ ) {
            total += measure(trigger, echo);
        }
        double avg = total / SAMPLES;
        double norm = 1 - ((avg-MIN_VALUE) / (MAX_VALUE-MIN_VALUE));
        norm = Math.max(0, Math.min(1, norm));
        fireTriggers(norm);
        fireCallbacks(norm);
    }

    private void fireCallbacks(double value) {
        for ( Callback c : callbacks ) {
            c.call(value);
        }
    }

    public HCSR04 onReadSensor(Callback callback) {
        callbacks.add(callback);
        return this;
    }

    private List<Callback> callbacks = new LinkedList<>();

    public interface Callback {
        void call(double value);
    }

    public native void init(int trigger, int echo);

    public native long measure(int trigger, int echo);

}