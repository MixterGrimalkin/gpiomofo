package net.amarantha.gpiomofo.display.lightboard;

public class NativeLightBoard extends NativeLightboardWrapper {

    private int height;
    private int width;

    @Override
    public void init(int width, int height) {
        initNative(this.height = height, this.width = width);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public Long interval() {
        return 1L;
    }

    @Override
    public boolean needsOwnThread() {
        return true;
    }

    ////////////////////
    // Native Methods //
    ////////////////////

    protected native void initNative(int rows, int cols);

    @Override
    protected native void setPoint(int row, int col, boolean red, boolean green);

    @Override
    protected native void setPins(int clock, int store, int output, int data1R, int data2R, int data1G, int data2G, int addr0, int addr1, int addr2, int addr3);

    public native void sleep();

    public native void wake();

}
