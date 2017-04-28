package net.amarantha.gpiomofo.service.gpio.ultrasonic;

public class RangeSensorHCSR04 extends RangeSensor {

    static {
        System.loadLibrary("hc-sr04");
    }

    public RangeSensorHCSR04() {
        super("Range Sensor HC-SR04");
    }

    @Override
    public native void init(int trigger, int echo);

    @Override
    public native long measure(int trigger, int echo);

}