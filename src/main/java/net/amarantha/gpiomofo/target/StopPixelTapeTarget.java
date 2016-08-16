package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;

public class StopPixelTapeTarget extends Target {

    @Inject private PixelTapeController pixelTape;

    @Override
    protected void onActivate() {
        pixelTape.stopAll();
    }

    @Override
    protected void onDeactivate() {

    }
}
