package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.midi.MidiCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.core.target.QueuedTarget;
import net.amarantha.gpiomofo.core.target.Target;
import net.amarantha.gpiomofo.core.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class TestScenario extends Scenario {

    @Override
    public void setupTriggers() {
        Trigger button =    triggers.gpio("Button", 0, PULL_DOWN, true);
        Trigger wire =      triggers.gpio("Wire", 3, PULL_DOWN, false);
        Trigger otherWire =      triggers.gpio("Other Wire", 4, PULL_UP, true);
        Trigger net =       triggers.http("Net");
        Trigger wireNet =   triggers.composite("WireNet", wire, net);
        triggers.composite("All", button, wireNet);
        triggers.osc("Osc", 55000, "gpiomofo");
    }

    @Override
    public void setupTargets() {
        targets.gpio("Red Toggle", 1, null)
                .oneShot(true);
        targets.gpio("Blue", 2, true);

        Target redLedOn =   targets.gpio("Red On", 1, true);
        Target redLedOff =  targets.gpio("Red Off", 1, false);
        Target blueLedOn =  targets.gpio("Blue On", 2, true);
        Target blueLedOff = targets.gpio("Blue Off", 2, false);
        Integer delay = 100;
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

        int startNote = 78;
        QueuedTarget queuedTarget = targets.queue("Falling Midi");
        for ( int note=78; note>52; note-- ) {
            Target noteTarget = targets.midi(new MidiCommand(NOTE_ON, 1, note, 127), new MidiCommand(NOTE_OFF, 1, note, 0))
                    .clearDelay(3000L);
            queuedTarget.addTargets(noteTarget);
        }

        HttpCommand loadScene = new HttpCommand("POST", "192.168.1.60", 8001, "lightboard/scene", "", "");
        targets.http("Zapper", loadScene.withPath("zapper-finale/load"), loadScene.withPath("splash/load"));
        targets.http("Greenpeace", loadScene.withPath("greenpeace-logo/load"));
        targets.http("Showers", loadScene.withPath("showers/load"));

        targets.python("Python", "python/init.py");
        targets.audio("Sea Power", "audio/bsp.mp3");

        targets.osc("Osc",
                new OscCommand("192.168.1.70", 53000, "hello", "one", "two"),
                new OscCommand("192.168.1.70", 53000, "goodbye", "three", "fout")
        );
    }

    @Override
    public void setupLinks() {
        links
            .link("Button",     "Red Toggle", "Falling Midi")
            .link("Wire",       "Blue", "Python")
            .link("Net",        "LED Flash")
            .link("WireNet",    "Greenpeace")
            .link("All",        "Sea Power", "Zapper")
            .link("Other Wire", "Osc")
            .link("Osc", "Showers")
        ;
    }

}

