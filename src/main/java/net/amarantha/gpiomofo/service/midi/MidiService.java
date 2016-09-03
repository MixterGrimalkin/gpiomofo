package net.amarantha.gpiomofo.service.midi;

public interface MidiService {

    void start();

    void stop();

    void send(MidiCommand midiCommand);

    void send(int command, int channel, int data1, int data2);

}
