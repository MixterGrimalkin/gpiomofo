package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.midi.MidiCommand;
import net.amarantha.gpiomofo.service.midi.MidiService;

public class MidiTarget extends Target {

    @Inject private MidiService midi;

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

    public MidiCommand getOnCommand() {
        return onCommand;
    }

    public MidiCommand getOffCommand() {
        return offCommand;
    }
}
