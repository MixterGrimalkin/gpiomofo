package net.amarantha.gpiomofo.display.lightboard;

public class NativeLightBoard_256X64_Big_Sign extends NativeLightBoard {

    static {
        System.loadLibrary("lightboard_256x64_big_sign");
    }

    @Override
    public Long interval() {
        return 1L;
    }

}
