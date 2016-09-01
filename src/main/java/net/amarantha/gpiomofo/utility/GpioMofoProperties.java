package net.amarantha.gpiomofo.utility;

import com.google.inject.Singleton;
import net.amarantha.gpiomofo.pixeltape.RGB;

@Singleton
public class GpioMofoProperties extends PropertyManager {

    public String mediaIp() {
        return getString("mediaServerIP", "192.168.42.99");
    }

    public int mediaOscPort() {
        return getInt("mediaServerOscPort", 53000);
    }

    public String lightingIp() {
        return getString("lightingIp", "192.168.42.100");
    }

    public int lightingOscPort() {
        return getInt("lightingServerOscPort", 7700);
    }

    public RGB getColour(String name) {
        String c = getString(name, "100,100,100");
        String[] elements = c.split(",");
        if ( elements.length==3 ) {
            int red = Integer.parseInt(elements[0]);
            int green = Integer.parseInt(elements[1]);
            int blue = Integer.parseInt(elements[2]);
            return new RGB(red, green, blue);
        }
        return new RGB(100,100,100);
    }
}
