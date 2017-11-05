package net.amarantha.gpiomofo.service.dmx;

import com.google.inject.Singleton;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import net.amarantha.gpiomofo.core.Constants;
import net.amarantha.gpiomofo.display.dmx.Dmx;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.service.AbstractService;
import net.amarantha.utils.shell.Utility;

import java.util.TooManyListenersException;

@Singleton
@PropertyGroup("SerialDmx")
public class DmxService extends AbstractService {

    @Property("Port") private String port;

    private Dmx dmx;

    public DmxService() {
        super("Dmx Service");
        dmx = new Dmx();
    }

    @Override
    protected void onStart() {
        try {
            dmx.open(port);
        } catch (UnsupportedCommOperationException | TooManyListenersException | PortInUseException | NoSuchPortException e) {
            Utility.log("Error opening '"+port+"': "+e.getClass().getSimpleName());
        }
    }

    @Override
    protected void onStop() {

    }

    public void set(int channel, int value) {
        if ( dmx.isOpen() ) {
            dmx.setDMXChannel(channel, value, true);
        }
    }

    public DmxDevice device(int channel) {
        return new DmxDevice(this, channel);
    }

    public DmxRgbDevice rgbDevice(int startChannel) {
        return new DmxRgbDevice(this, startChannel);
    }
}
