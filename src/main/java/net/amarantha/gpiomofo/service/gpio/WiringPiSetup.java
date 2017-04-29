package net.amarantha.gpiomofo.service.gpio;

public class WiringPiSetup {

    static {
        System.loadLibrary("WiringPiSetup");
    }

    private static boolean initialised = false;

    public static void init() {
        if ( !initialised ) {
            wiringPiSetup();
            initialised = true;
        }
    }

    private static native void wiringPiSetup();

}
