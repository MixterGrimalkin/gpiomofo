package net.amarantha.gpiomofo.service.pixeltape;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.core.target.Target;

public class StopPixelTapeTarget extends Target {

    @Inject private PixelTapeService pixelTape;

    @Override
    protected void onActivate() {
        pixelTape.stopAll(clear);
    }

    @Override
    protected void onDeactivate() {

    }

    public StopPixelTapeTarget setClear(boolean clear) {
        this.clear = clear;
        return this;
    }

    private boolean clear = true;
}
