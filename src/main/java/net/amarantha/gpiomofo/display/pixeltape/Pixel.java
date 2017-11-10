package net.amarantha.gpiomofo.display.pixeltape;

import net.amarantha.utils.colour.RGB;

public class Pixel {

    private final NeoPixel neoPixel;
    private final int updateInterval;

    private int number;
    private double current = 0.0;
    private double min = 0.0;
    private double max = 1.0;
    private double delta = 0.0;
    private boolean bounce = false;
    private RGB rgb = RGB.WHITE;

    public Pixel(NeoPixel neoPixel, int updateInterval, int number) {
        this.neoPixel = neoPixel;
        this.updateInterval = updateInterval;
        this.number = number;
    }

    public Pixel update() {
        return applyDelta().draw();
    }

    public Pixel applyDelta() {
        current += delta;
        if (current >= max) {
            if (delta > 0) {
                current = max;
                if (bounce) {
                    delta *= -1;
                } else {
                    delta = 0;
                }
            }
        } else if (current <= min) {
            if (delta < 0) {
                current = min;
                if (bounce) {
                    delta *= -1;
                } else {
                    delta = 0;
                }
            }
        }
        return this;
    }

    public Pixel rgb(RGB rgb) {
        this.rgb = rgb;
        return this;
    }

    public Pixel delta(double delta) {
        this.delta = delta;
        return this;
    }

    public Pixel bounce(boolean bounce) {
        this.bounce = bounce;
        return this;
    }

    public Pixel range(double min, double max) {
        this.min = min;
        this.max = max;
        return this;
    }

    public Pixel max(double max) {
        this.max = max;
        return this;
    }

    public Pixel min(double min) {
        this.min = min;
        return this;
    }

    public boolean goingUp() {
        return delta > 0;
    }

    public void fadeUp(int duration) {
        fade(duration, true);
    }

    public void fadeDown(int duration) {
        fade(duration, false);
    }

    public void fade(int duration, boolean up) {
        if ((up && current <= max) || (!up && current >= min)) {
            double distance = up ? (max - min) : -(max - min);
            delta = distance / (duration / updateInterval);
        }
    }

    public Pixel draw() {
        neoPixel.setPixel(number, rgb.withBrightness(current));
        return this;
    }

    public Pixel jump(double jump) {
        current = jump;
        return this;
    }

    public double current() {
        return current;
    }
}
