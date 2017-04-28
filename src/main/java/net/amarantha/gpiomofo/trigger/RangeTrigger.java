package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensor;
import net.amarantha.utils.service.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class RangeTrigger extends Trigger {

    @Parameter("triggerPin") private int trigger;
    @Parameter("echoPin") private int echo;
    @Parameter("minValue") private int min = 400;
    @Parameter("maxValue") private int max = 12000;

    @Service private RangeSensor sensor;

    private List<Consumer<Double>> callbacks = new LinkedList<>();

    @Override
    public void enable() {
        sensor.addSensor(trigger, echo, min, max, this::fireCallbacks);
    }

    private void fireCallbacks(double value) {
        callbacks.forEach((callback)->callback.accept(value));
    }

    public void onMeasure(Consumer<Double> callback) {
        callbacks.add(callback);
    }

    private void setRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

}
