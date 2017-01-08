package net.amarantha.gpiomofo.trigger;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.osc.OscService;

public class OscTrigger extends Trigger {

    @Inject private OscService osc;

    private int port;
    private String address;

    public OscTrigger setReceiver(int port, String address) {
        osc.onReceive(port, address, ((time, message) -> fire(true)));
        this.port = port;
        this.address = address;
        return this;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }
}
