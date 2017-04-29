package net.amarantha.gpiomofo.service.gpio.ultrasonic;

import net.amarantha.gpiomofo.service.gpio.WiringPiSetup;

public class RangeSensorHCSR04 extends RangeSensor {

    static {
        System.loadLibrary("hc-sr04");
    }

    public RangeSensorHCSR04() {
        super("Range Sensor HC-SR04");
        WiringPiSetup.init();
    }

    @Override
    public native void init(int trigger, int echo);

    @Override
    public native long measure(int trigger, int echo);

}