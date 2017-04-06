package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.service.Service;

public class OscTrigger extends Trigger {

    @Service private OscService osc;

    @Parameter("port")      private int port;
    @Parameter("address")   private String address;

    @Override
    public void enable() {
        osc.onReceive(port, address, ((time, message) -> fire(true)));
    }

}
