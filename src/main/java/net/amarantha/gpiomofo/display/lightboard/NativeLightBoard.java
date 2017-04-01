package net.amarantha.gpiomofo.display.lightboard;

public class NativeLightBoard extends NativeWrapper {

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

    protected native void setPoint(int row, int col, boolean red, boolean green);

    public native void sleep();

    public native void wake();

}
