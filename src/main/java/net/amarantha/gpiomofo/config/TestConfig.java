package net.amarantha.gpiomofo.config;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.link.LinkFactory;
import net.amarantha.gpiomofo.midi.MidiCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.target.TargetFactory;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.TriggerFactory;

import javax.sound.midi.ShortMessage;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class TestConfig extends Config {

    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;
    @Inject private LinkFactory links;

    private Trigger button;
    private Trigger wire;
    private Target redLedOn;
    private Target redLed2s;
    private Target blueLedOn;
    private Target lowNoteMax3s;
    private Target highNoteHold;
    private Target seaPower;

    @Override
    public void setupTriggers() {

        button = triggers.gpio("Button", 0, PULL_DOWN, true);
        wire = triggers.gpio("Wire", 3, PULL_DOWN, false);

    }

    @Override
    public void setupTargets() {

        redLedOn = targets.gpio("Red LED", 1, true);

        redLed2s = targets.gpio("Red LED for 2 seconds", 1, true)
                .followTrigger(false)
                .clearDelay(2000L);

        blueLedOn = targets.gpio("Blue LED", 2, true);

        lowNoteMax3s = targets.midi("Low Note for max 3 seconds",
                new MidiCommand(NOTE_ON, 1, 64, 127), new MidiCommand(NOTE_OFF, 1, 64, 0)
        ).clearDelay(3000L);

        highNoteHold = targets.midi("High Note hold",
                new MidiCommand(NOTE_ON, 1, 71, 127), new MidiCommand(NOTE_OFF, 1, 71, 0)
        );

        seaPower = targets.audio("British Sea Power", "audio/bsp.mp3");

    }

    @Override
    public void setupLinks() {

        links.link(button, redLedOn);
        links.link(button, lowNoteMax3s);
        links.link(button, highNoteHold);

        links.link(wire, redLed2s);
        links.link(wire, blueLedOn);
        links.link(wire, seaPower);

    }

}
