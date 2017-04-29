package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.service.Service;

public class OscTrigger extends Trigger {

    @Service private OscService osc;

    @Parameter("port")      private int port;
    @Parameter("address")   private String address;
    @Parameter("data")    private String data;

    @Override
    public void enable() {
        osc.onReceive(port, address, ((time, args) -> {
            if ( args!=null && !args.isEmpty() ) {
                fire(args.get(0).equals(data));
            }
        }));
    }

}
