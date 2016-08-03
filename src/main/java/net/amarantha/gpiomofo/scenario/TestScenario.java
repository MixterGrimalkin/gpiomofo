package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.midi.MidiCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class TestScenario extends Scenario {

    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;
    @Inject private LinkFactory links;

    private Trigger button;
    private Trigger wire;
    private Trigger all;
    private Trigger network;

    private Target redLedOn;
    private Target blueLedOn;
    private Target note;
    private Target playSong;
    private Target greenpeaceLogo;
    private Target python;
    private Target flashLeds;
    private Target redLedOff;
    private Target blueLedOff;

    @Override
    public void setupTriggers() {

        button =
                triggers.gpio("Button", 0, PULL_DOWN, true);

        wire =
                triggers.gpio("Wire", 3, PULL_DOWN, false);

        network =
                triggers.http("Net");

        all =
                triggers.composite("Combo", button, wire, network);

    }

    @Override
    public void setupTargets() {

        redLedOn =
                targets.gpio("Red On", 1, true);

        redLedOff =
                targets.gpio("Red Off", 1, false);

        blueLedOn =
                targets.gpio("Blue On", 2, true);

        blueLedOff =
                targets.gpio("Blue Off", 2, false);

        Integer delay = 50;
        flashLeds =
            targets.chain("LED Flash")
                .add(delay, redLedOn)
                .add(delay, blueLedOn)
                .add(delay, redLedOff)
                .add(delay, blueLedOff)
                .add(delay, redLedOn)
                .add(delay, blueLedOn)
                .add(delay, redLedOff)
                .add(delay, blueLedOff)
                .add(delay, redLedOn)
                .add(delay, blueLedOn)
                .add(delay, redLedOff)
                .add(delay, blueLedOff)
            .build().oneShot(true);


        note =
                targets.midi("Midi Note", new MidiCommand(NOTE_ON, 1, 64, 127), new MidiCommand(NOTE_OFF, 1, 64, 0)).clearDelay(3000L);

        greenpeaceLogo =
                targets.http("Greenpeace", "POST", "192.168.1.60:8001", "lightboard/scene/greenpeace-logo/load", "");

        python =
                targets.python("Python", "python/test.py");

        playSong =
                targets.audio("Sea Power", "audio/bsp.mp3");

    }

    @Override
    public void setupLinks() {

        links.link(button,      flashLeds);
        links.link(wire,        redLedOn, blueLedOn);
        links.link(network,         playSong);

    }

}

