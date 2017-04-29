package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.service.Service;

public class OscValueTrigger extends ContinuousTrigger {

    @Parameter("port") private int port;
    @Parameter("address") private String address;

    @Service private OscService osc;

    @Override
    public void enable() {
        osc.onReceive(port, address, (date, args) -> {
            if ( !args.isEmpty() ) {
                fireCallbacks(((Integer)args.get(0)).doubleValue());
            }
        });
    }

}
