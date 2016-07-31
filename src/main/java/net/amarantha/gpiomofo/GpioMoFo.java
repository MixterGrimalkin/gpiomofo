package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.gpio.GpioProvider;
import net.amarantha.gpiomofo.midi.MidiCommand;
import net.amarantha.gpiomofo.midi.MidiService;
import net.amarantha.gpiomofo.target.TargetFactory;
import net.amarantha.gpiomofo.target.TriggerConfig;

import javax.sound.midi.ShortMessage;
import java.util.Scanner;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;

@Singleton
public class GpioMoFo {

    @Inject private GpioProvider gpio;
    @Inject private TargetFactory targets;
    @Inject private MidiService midi;

    private void setup() {

        TriggerConfig button = new TriggerConfig(0, PULL_DOWN, true);

        targets.gpio(button, 1, true)
                .clearAfter(3000L);

        targets.gpio(button, 2, false)
                .clearAfter(3000L);

        targets.midi(button, new MidiCommand(ShortMessage.NOTE_ON, 1, 64, 127), new MidiCommand(ShortMessage.NOTE_OFF, 1, 64, 0))
                .clearAfter(3000L);

    }

    public void start() {
        System.out.println("Starting GpioMoFo...");
        setup();
        midi.openDevice();
        gpio.startInputMonitor();
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {}
        gpio.stopInputMonitor();
        gpio.shutdown();
        System.out.println("Goodbye");
        System.exit(0);
    }

}
