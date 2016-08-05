package net.amarantha.gpiomofo.trigger;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.osc.OscService;

public class OscTrigger extends Trigger {

    @Inject
    private OscService osc;

    public OscTrigger setReceiver(int port, String address) {
        osc.onReceive(port, address, ((time, message) -> fire(true)));
        return this;
    }

}
