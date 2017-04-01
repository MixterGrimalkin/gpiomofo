package net.amarantha.gpiomofo.display.entity;

public class Region {

    public final int left;
    public final int top;
    public final int width;
    public final int height;
    public final int right;
    public final int bottom;

    public Region(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        right = left + width - 1;
        bottom = top + height - 1;
    }

}
