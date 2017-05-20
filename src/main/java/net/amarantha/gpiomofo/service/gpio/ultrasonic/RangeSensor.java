package net.amarantha.gpiomofo.service.gpio.ultrasonic;

import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.service.AbstractService;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@PropertyGroup("RangeSensor")
public abstract class RangeSensor extends AbstractService {

    protected List<Sensor> sensors = new LinkedList<>();

    private Timer scanTimer;

    @Property("Refresh") private int refreshInterval = 50;

    public RangeSensor(String name) {
        super(name);
    }

    protected abstract void init(int trigger, int echo);

    protected abstract long measure(int trigger, int echo);

    public void addSensor(int trigger, int echo, Consumer<Double> callback) {
        sensors.add(new Sensor(trigger, echo, callback));
    }

    private void measureAll() {
        sensors.forEach((s)->{
            if ( s.callback!=null ) {
                s.callback.accept((double)measure(s.trigger, s.echo));
            }
        });
    }

    @Override
    protected void onStart() {
        sensors.forEach((sensor)->init(sensor.trigger, sensor.echo));
        scanTimer = new Timer();
        scanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                measureAll();
            }
        }, 0, refreshInterval);
    }

    @Override
    protected void onStop() {
        if ( scanTimer!=null ) {
            scanTimer.cancel();
        }
    }

    protected static class Sensor {
        int trigger;
        int echo;
        Consumer<Double> callback;
        Sensor(int trigger, int echo, Consumer<Double> callback) {
            this.trigger = trigger;
            this.echo = echo;
            this.callback = callback;
        }
    }

}
