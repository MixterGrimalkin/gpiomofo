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
    private double posDelta = 0.0;
    private double negDelta = 0.0;
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
                    delta = negDelta;
                } else {
                    delta = 0;
                }
            }
        } else if (current <= min) {
            if (delta < 0) {
                current = min;
                if (bounce) {
                    delta = posDelta;
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
//        if ( delta >= 0 ) {
            return delta(delta, -delta);
//        } else {
//            return delta(-delta, delta);
//        }
    }

    public Pixel delta(double up, double down) {
        delta = up;
        posDelta = up >= 0 ? up : down;
        negDelta = down <= 0 ? down : up;
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

    public Pixel fadeUp(int duration) {
        if ( current < max ) {
            delta = posDelta = (max - min) / (duration / updateInterval);
            bounce = false;
        }
        return this;
    }

    public Pixel fadeDown(int duration) {
        if ( current > min ) {
            delta = posDelta = -(max - min) / (duration / updateInterval);
            bounce = false;
        }
        return this;
    }

    public Pixel bounceFadeUp(int duration) {
        return bounceFade(duration, duration, true);
    }
    public Pixel bounceFadeUp(int upDuration, int downDuration) {
        return bounceFade(upDuration, downDuration, true);
    }
    public Pixel bounceFadeDown(int upDuration, int downDuration) {
        return bounceFade(upDuration, downDuration, false);
    }
    public Pixel bounceFadeDown(int duration) {
        return bounceFade(duration, duration, false);
    }
    public Pixel bounceFade(int upDuration, int downDuration, boolean goUp) {
        posDelta = (max - min) / (upDuration / updateInterval);
        negDelta = -(max - min) / (downDuration / updateInterval);
        bounce = true;
        delta = goUp ? posDelta : negDelta;
        return this;
    }

    public Pixel pause() {
        delta = 0;
        return this;
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
