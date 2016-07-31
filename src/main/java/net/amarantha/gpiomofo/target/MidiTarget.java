package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.midi.MidiCommand;
import net.amarantha.gpiomofo.midi.MidiService;

public class MidiTarget extends AbstractTarget {

    @Inject private MidiService midi;

    private MidiCommand onCommand;
    private MidiCommand offCommand;

    public MidiTarget onCommand(MidiCommand command) {
        onCommand = command;
        return this;
    }

    public MidiTarget offCommand(MidiCommand command) {
        offCommand = command;
        return this;
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
