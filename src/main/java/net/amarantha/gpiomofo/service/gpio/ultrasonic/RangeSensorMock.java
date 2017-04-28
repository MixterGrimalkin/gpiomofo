package net.amarantha.gpiomofo.service.gpio.ultrasonic;

public class RangeSensorMock extends RangeSensor {

    public RangeSensorMock() {
        super("Range Sensor Mock");
    }

    @Override
    protected void init(int trigger, int echo) {

    }

    @Override
    protected long measure(int trigger, int echo) {
        return 0;
    }

}
