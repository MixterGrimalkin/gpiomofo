package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.annotation.Parameter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ContinuousTrigger extends Trigger {

    @Parameter("minValue") protected int min = 400;
    @Parameter("maxValue") protected int max = 12000;

    @Override
    public abstract void enable();

    private List<Consumer<Double>> callbacks = new LinkedList<>();

    protected void fireCallbacks(double value) {
        callbacks.forEach((callback)->callback.accept(normalise(value)));
    }

    public void onMeasure(Consumer<Double> callback) {
        callbacks.add(callback);
    }

    protected double normalise(double value) {
        double normalised;
        if (value <= min) {
            normalised = 0.0;
        } else if (value >= max) {
            normalised = 1.0;
        } else {
            normalised = (value - min) / (max - min);
        }
        return normalised;
    }

}
