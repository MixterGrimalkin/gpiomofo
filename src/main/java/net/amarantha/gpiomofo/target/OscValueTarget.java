package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.service.Service;

public class OscValueTarget extends Target {

    @Parameter("command") private OscCommand command;
    @Parameter("minValue") private int min;
    @Parameter("maxValue") private int max;

    @Service private OscService osc;

    public void activate(double value) {
        OscCommand sendCommand =
            new OscCommand(command.getHost(), command.getPort(), command.getAddress(),
                MathUtils.round((value*(max-min))+min));
        sendCommand.setSilent(true);
        osc.send(sendCommand);
    }

    @Override
    protected void onActivate() {

    }

    @Override
    protected void onDeactivate() {

    }
}
