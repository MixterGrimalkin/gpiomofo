package net.amarantha.gpiomofo.service.midi;

import com.google.inject.Singleton;

@Singleton
public class MidiServiceMock implements MidiService {

    private boolean deviceOpen = false;
    private MidiCommand lastMidiCommand = null;

    @Override
    public void start() {
        deviceOpen = true;
    }

    @Override
    public void stop() {
        deviceOpen = false;
    }

    @Override
    public void send(MidiCommand midiCommand) {
        lastMidiCommand = midiCommand;
    }

    @Override
    public void send(int command, int channel, int data1, int data2) {
        lastMidiCommand = new MidiCommand(command, channel, data1, data2);
    }

    public MidiCommand getLastCommand() {
        return lastMidiCommand;
    }

    public boolean isDeviceOpen() {
        return deviceOpen;
    }

    public void clearLastCommand() { lastMidiCommand = null; }

}
