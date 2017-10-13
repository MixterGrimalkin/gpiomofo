package net.amarantha.gpiomofo.service.dmx;

import com.google.inject.Singleton;
import net.amarantha.gpiomofo.display.dmx.Dmx;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.service.AbstractService;

@Singleton
@PropertyGroup("SerialDmx")
public class DmxService extends AbstractService {

    @Property("Port") private String port;

    private Dmx dmx;

    public DmxService() {
        super("Dmx");
        dmx = new Dmx();
    }

    @Override
    protected void onStart() {
        dmx.open(port);
    }

    @Override
    protected void onStop() {

    }

    public void set(int channel, int value) {
        if ( dmx.isOpen() ) {
            dmx.setDMXChannel(channel, value, true);
        }
    }

    public DmxRgbDevice rgbDevice(int startChannel) {
        return new DmxRgbDevice(this, startChannel);
    }
}
