package net.amarantha.gpiomofo.service.osc;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.core.trigger.Trigger;

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
