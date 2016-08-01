package net.amarantha.gpiomofo.config;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.link.LinkFactory;
import net.amarantha.gpiomofo.midi.MidiCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.target.TargetFactory;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.TriggerFactory;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class TestConfig extends Config {

    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;
    @Inject private LinkFactory links;

    private Trigger button;
    private Trigger wire;
    private Trigger all;
    private Trigger network;

    private Target redLed;
    private Target blueLed;
    private Target lowNote;
    private Target playSong;
    private Target greenpeaceLogo;
    private Target python;

    @Override
    public void setupTriggers() {

        button =
                triggers.gpio("Button", 0, PULL_DOWN, true);

        wire =
                triggers.gpio("Wire", 3, PULL_DOWN, false);

        network =
                triggers.http("Play Song");

        all =
                triggers.composite(button, wire, network);

    }

    @Override
    public void setupTargets() {

        redLed =
                targets.gpio("Red LED", 1, true);

        blueLed =
                targets.gpio("Blue LED", 2, true);

        lowNote =
                targets.midi("Low Note", new MidiCommand(NOTE_ON, 1, 64, 127), new MidiCommand(NOTE_OFF, 1, 64, 0)).clearDelay(3000L);

        greenpeaceLogo =
                targets.http("Greenpeace", "POST", "192.168.1.60:8001", "lightboard/scene/greenpeace-logo/load", "");

        python =
                targets.python("Python", "python/test.py");

        playSong =
                targets.audio("Play Song", "audio/bsp.mp3");

    }

    @Override
    public void setupLinks() {

        links.link(button,      redLed, lowNote, python);
        links.link(wire,        blueLed, greenpeaceLogo);
        links.link(all,         playSong);

    }

}

