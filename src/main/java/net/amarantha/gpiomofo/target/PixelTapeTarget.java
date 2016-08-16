package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.pixeltape.pattern.PixelTapePattern;

public class PixelTapeTarget extends Target {

    @Override
    protected void onActivate() {
        pattern.start();
    }

    @Override
    protected void onDeactivate() {
        pattern.stop();
    }

    private PixelTapePattern pattern;

    public PixelTapeTarget setPattern(PixelTapePattern pattern) {
        this.pattern = pattern;
        return this;
    }
}
