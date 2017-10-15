package net.amarantha.gpiomofo.service.dmx;

import net.amarantha.utils.colour.RGB;

import java.util.function.Consumer;

public class DmxDevice {

    private DmxService dmx;
    private int startChannel = 0;

    public DmxDevice(DmxService dmx, int startChannel) {
        this.dmx = dmx;
        this.startChannel = startChannel;
    }

    public void setValue(Integer value) {
        value = value != null ? value : 0;
        dmx.set(startChannel, value);
    }

    public Consumer<RGB> getInterceptor() {
        return (rgb) -> {
            rgb = rgb != null ? rgb : RGB.BLACK;
            setValue((rgb.getRed() + rgb.getGreen() + rgb.getBlue()) / 3);
        };
    }

}
