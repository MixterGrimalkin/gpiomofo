package net.amarantha.gpiomofo.midi;

public class MidiCommand {

    private int command = 0;
    private int channel = 1;
    private int data1 = 0;
    private int data2 = 0;

    public MidiCommand() {}

    public MidiCommand(int command, int channel, int data1, int data2) {
        this.command = command;
        this.channel = channel-1;
        this.data1 = data1;
        this.data2 = data2;
    }

    public void send(MidiService midi) {
        midi.send(command, channel, data1, data2);
    }

    public int getCommand() {
        return command;
    }

    public int getChannel() {
        return channel;
    }

    public int getData1() {
        return data1;
    }

    public int getData2() {
        return data2;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setData1(int data1) {
        this.data1 = data1;
    }

    public void setData2(int data2) {
        this.data2 = data2;
    }

    @Override
    public String toString() {
        return "[MidiCommand:" + command +"," + channel + "," + data1 + "," + data2 + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MidiCommand that = (MidiCommand) o;

        if (command != that.command) return false;
        if (channel != that.channel) return false;
        if (data1 != that.data1) return false;
        return data2 == that.data2;

    }

    @Override
    public int hashCode() {
        int result = command;
        result = 31 * result + channel;
        result = 31 * result + data1;
        result = 31 * result + data2;
        return result;
    }
}
