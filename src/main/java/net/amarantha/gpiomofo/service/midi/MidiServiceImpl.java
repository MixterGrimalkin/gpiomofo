package net.amarantha.gpiomofo.service.midi;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.utility.Utility;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.Property;
import net.amarantha.utils.properties.PropertyNotFoundException;

import javax.sound.midi.*;

@Singleton
public class MidiServiceImpl implements MidiService {

    @Property("MidiDevice") private String deviceName;

    private MidiDevice midiOutDevice;

    @Inject
    public MidiServiceImpl(PropertiesService props) {
        try {
            props.injectProperties(this);
        } catch (PropertyNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void start() {
        System.out.println("Starting MIDI Service...");
        try {
            connectDevice(deviceName);
            midiOutDevice = getMidiOutDevice(deviceName);
            midiOutDevice.open();
        } catch (MidiUnavailableException e) {
            System.out.println("Could not open MIDI device '" + deviceName + "'");
        }
    }


    @Override
    public void stop() {
        System.out.println("Stopping MIDI Service...");
        if ( midiOutDevice !=null ) {
            midiOutDevice.close();
        }
        disconnectDevice(deviceName);
    }

    @Override
    public void send(MidiCommand midiCommand) {
        if ( midiCommand!=null ) {
            send(midiCommand.getCommand(), midiCommand.getChannel(), midiCommand.getData1(), midiCommand.getData2());
        }
    }

    @Override
    public void send(int command, int channel, int data1, int data2) {
        if ( midiOutDevice !=null ) {
            try {
                Receiver receiver = midiOutDevice.getReceiver();
                ShortMessage message = new ShortMessage();
                message.setMessage(command, channel-1, data1, data2);
                receiver.send(message, -1);
            } catch (InvalidMidiDataException e) {
                System.err.println("Invalid MIDI Data: " + e.getMessage());
            } catch (MidiUnavailableException e) {
                System.err.println("MIDI Device Unavailable: " + e.getMessage());
            }
        }
    }

    /////////////////
    // MIDI Device //
    /////////////////

    private MidiDevice getMidiOutDevice(String name) throws MidiUnavailableException {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            MidiDevice device = MidiSystem.getMidiDevice(info);
            try {
                if (info.getDescription().contains(name)) {
                    if ( device.getReceiver() != null ) {
                        return device;
                    }
                }
            } catch ( MidiUnavailableException ignored ) {
            }
        }
        throw new MidiUnavailableException("MIDI Device '" + name + "' not found");
    }

    private void connectDevice(String name) {
        Utility.executeCommand(new String[]{"aconnect", name, "Midi Through"});
    }

    private void disconnectDevice(String name) {
        Utility.executeCommand(new String[]{"aconnect", "-d", name, "Midi Through"});
    }


}
