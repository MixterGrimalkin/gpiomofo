package net.amarantha.gpiomofo.midi;

public interface MidiService {

    void openDevice();

    void openDevice(String name);

    void closeDevice();

    void send(MidiCommand midiCommand);

    void send(int command, int channel, int data1, int data2);

}
