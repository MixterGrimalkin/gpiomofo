package net.amarantha.gpiomofo.display.lightboard;

public class NativeLightBoard_192X32_Big_Sign extends NativeLightBoard {

    static {
        System.loadLibrary("lightboard_192x32_big_sign");
    }

}
