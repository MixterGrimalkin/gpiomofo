package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.service.osc.OscService;

public class OscTarget extends Target {

    @Inject private OscService osc;

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

    private OscCommand onCommand;
    private OscCommand offCommand;

    public OscTarget onCommand(OscCommand command) {
        onCommand = command;
        return this;
    }

    public OscTarget offCommand(OscCommand command) {
        offCommand = command;
        return this;
    }

}
