package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.utils.midi.MidiService;
import net.amarantha.utils.midi.entity.MidiCommand;
import net.amarantha.utils.service.Service;

public class MidiTarget extends Target {

    @Service  private MidiService midi;

    @Parameter("onCommand") private MidiCommand onCommand;
    @Parameter("offCommand") private MidiCommand offCommand;

    @Override
    public void enable() {
        if ( offCommand==null ) {
            oneShot(true);
        }
    }

    @Override
    protected void onActivate() {
        if ( onCommand!=null ) {
            midi.send(onCommand);
        }
    }

    @Override
    protected void onDeactivate() {
        if ( offCommand!=null ) {
            midi.send(offCommand);
        }
    }

}
