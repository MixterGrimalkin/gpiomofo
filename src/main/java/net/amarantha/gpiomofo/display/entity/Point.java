package net.amarantha.gpiomofo.display.entity;

import static net.amarantha.utils.math.MathUtils.round;

public class Point {

    private double x = 0.0;
    private double y = 0.0;
    private int layer = 0;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public int xInt() {
        return round(x);
    }

    public int yInt() {
        return round(y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public Point copy() {
        return new Point(x, y);
    }
}
