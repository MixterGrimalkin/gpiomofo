package net.amarantha.gpiomofo.display.pixeltape;

import com.google.inject.Inject;
import net.amarantha.utils.colour.RGB;

public class Pixel {

    private enum Mode { OFF, PATTERN, ANIMATION}

    private Mode mode = Mode.PATTERN;

    public Pixel off() {
        mode = Mode.OFF;
        return this;
    }

    public Pixel pattern() {
        mode = Mode.PATTERN;
        return this;
    }

    public Pixel animation() {
        mode = Mode.ANIMATION;
        return this;
    }

    @Inject private NeoPixel neoPixel;

    private int updateInterval;

    private int number;
    private double current = 0.0;
    private double min = 0.0;
    private double max = 1.0;
    private double delta = 0.0;
    private double posDelta = 0.0;
    private double negDelta = 0.0;
    private boolean bounce = false;
    private RGB rgb = RGB.WHITE;

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Pixel update(RGB rgb) {
        switch ( mode ) {
            case OFF:
                return this;
            case PATTERN:
                return applyDelta();
            case ANIMATION:
                return set(rgb);
        }
        return null;
    }

    public RGB currentState() {
        return rgb.withBrightness(current);
    }

    private Pixel set(RGB rgb) {
        this.rgb = rgb;
        current = 1.0;
        return this;
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

    public RGB rgb() {
        return rgb;
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

    public boolean goingDown() {
        return delta < 0;
    }

    public Pixel fadeUp(int duration) {
        if ( current <= max ) {
            delta = posDelta = (max - min) / (duration / updateInterval);
        }
        bounce = false;
        return this;
    }

    public Pixel fadeDown(int duration) {
        if ( current >= min ) {
            delta = posDelta = -(max - min) / (duration / updateInterval);
        }
        bounce = false;
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

    public Pixel jump(double jump) {
        current = jump;
        return this;
    }

    public double current() {
        return current;
    }
}
