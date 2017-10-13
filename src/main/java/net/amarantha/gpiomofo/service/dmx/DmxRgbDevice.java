package net.amarantha.gpiomofo.service.dmx;

import net.amarantha.utils.colour.RGB;

import java.util.function.Consumer;

public class DmxRgbDevice {

    private DmxService dmx;
    private int startChannel = 0;

    public DmxRgbDevice(DmxService dmx, int startChannel) {
        this.dmx = dmx;
        this.startChannel = startChannel;
    }

    public void setValue(RGB rgb) {
        rgb = rgb != null ? rgb : RGB.BLACK;
        dmx.set(startChannel, rgb.getRed());
        dmx.set(startChannel + 1, rgb.getGreen());
        dmx.set(startChannel + 2, rgb.getBlue());
    }

    public Consumer<RGB> getInterceptor() {
        return this::setValue;
    }

}
