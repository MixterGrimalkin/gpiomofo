package net.amarantha.gpiomofo.midi;

import com.google.inject.Singleton;

import javax.sound.midi.*;

@Singleton
public class MidiServiceImpl implements MidiService {

    private MidiDevice midiDevice;

    @Override
    public void openDevice() {
//        System.out.println("MIDI="+System.getenv("MIDIDEVICE"));
        openDevice("USB Uno MIDI Interface");
//        openDevice(System.getenv("MIDIDEVICE"));
    }

    @Override
    public void openDevice(String name) {
        try {
            midiDevice = getMidiDevice(name);
            midiDevice.open();
        } catch (MidiUnavailableException e) {
            System.err.println("Could not startup MIDI device '" + name + "': " + e.getMessage());
        }
    }

    @Override
    public void closeDevice() {
        if ( midiDevice!=null ) {
            midiDevice.close();
        }
    }

    @Override
    public void send(MidiCommand midiCommand) {
        if ( midiCommand!=null ) {
            send(midiCommand.getCommand(), midiCommand.getChannel(), midiCommand.getData1(), midiCommand.getData2());
        }
    }

    public void addListener() {
        System.out.println("Adding Listening");
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            System.out.println(sequencer.getReceiver());
            System.out.println(sequencer.getTransmitter());
            sequencer.addControllerEventListener(new ControllerEventListener() {
                @Override
                public void controlChange(ShortMessage event) {
                    System.out.println("RECEIVED:");
                    System.out.println(event);
                }
            }, new int[]{64});
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(int command, int channel, int data1, int data2) {
        if ( midiDevice!=null ) {
            try {
                Receiver receiver = midiDevice.getReceiver();
                ShortMessage message = new ShortMessage();
                message.setMessage(command, channel, data1, data2);
                receiver.send(message, -1);
            } catch (InvalidMidiDataException e) {
                System.err.println("Invalid MIDI Data: " + e.getMessage());
            } catch (MidiUnavailableException e) {
                System.err.println("MIDI Device Unavailable: " + e.getMessage());
            }
        }
    }

    private MidiDevice getMidiDevice(String name) throws MidiUnavailableException {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            System.out.println(info.getName());
                MidiDevice device = MidiSystem.getMidiDevice(info);
                try {
                    if (device.getReceiver() != null && info.getDescription().contains(name)) {
                        return device;
                    }
                } catch ( MidiUnavailableException ignored ) {}
        }
        throw new MidiUnavailableException("MIDI Device '" + name + "' not found");
    }

}
