package net.amarantha.gpiomofo.trigger;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.task.TaskService;

public class UltrasonicSensor extends RangeTrigger {

    static {
        System.loadLibrary("hc-sr04");
    }

    private TaskService tasks;

    @Inject
    public UltrasonicSensor(TaskService tasks) {
        this.tasks = tasks;
    }

    private final static int SAMPLES = 7;

    private final static int MIN_VALUE = 250;
    private final static int MAX_VALUE = 2200;

    public void start() {
        init();
        tasks.addRepeatingTask(this, 5, this::readSensor);
    }

    private void readSensor() {
        double total = 0;
        for ( int s=0; s<SAMPLES; s++ ) {
            total += measure();
        }
        double avg = total / SAMPLES;
        double norm = 1 - ((avg-MIN_VALUE) / (MAX_VALUE-MIN_VALUE));
        norm = Math.max(0, Math.min(1, norm));
        fire(norm);
    }

    public native void init();

    public native long measure();

}