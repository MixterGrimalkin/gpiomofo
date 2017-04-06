package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.service.Service;

public class OscTarget extends Target {

    @Service private OscService osc;

    @Parameter("onCommand") private OscCommand onCommand;
    @Parameter("offCommand") private OscCommand offCommand;

    @Override
    public void enable() {
        if ( offCommand==null ) {
            oneShot(true);
        }
    }

    @Override
    protected void onActivate() {
        if ( onCommand!=null ) {
            osc.send(onCommand);
        }
    }

    @Override
    protected void onDeactivate() {
        if ( offCommand!=null ) {
            osc.send(offCommand);
        }
    }

}
