package net.amarantha.gpiomofo.display.pixeltape;

import static net.amarantha.utils.shell.Utility.log;

public class NeoPixelMock extends AbstractNeoPixel {

    @Override
    public void init(int pixelCount) {
        log("Starting Mock NeoPixel...");
        super.init(pixelCount);
    }

}
