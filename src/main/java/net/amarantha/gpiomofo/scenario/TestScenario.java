package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.http.HttpCommand;
import net.amarantha.gpiomofo.midi.MidiCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class TestScenario extends Scenario {

    @Override
    public void setupTriggers() {
        Trigger button =    triggers.gpio("Button", 0, PULL_DOWN, true);
        Trigger wire =      triggers.gpio("Wire", 3, PULL_DOWN, false);
        Trigger net =       triggers.http("Net");
        Trigger wireNet =   triggers.composite("WireNet", wire, net);
        triggers.composite("All", button, wireNet);
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

        targets.midi("Midi Note", new MidiCommand(NOTE_ON, 1, 64, 127), new MidiCommand(NOTE_OFF, 1, 64, 0))
                .clearDelay(3000L);

        HttpCommand loadScene = new HttpCommand("POST", "192.168.1.60", 8001, "lightboard/scene", "", "");
        targets.http("Zapper", loadScene.withPath("zapper-finale/load"), loadScene.withPath("splash/load"));
        targets.http("Greenpeace", loadScene.withPath("greenpeace-logo/load"));

        targets.python("Python", "python/test.py");
        targets.audio("Sea Power", "audio/bsp.mp3");
    }

    @Override
    public void setupLinks() {
        links
            .link("Button",     "Red Toggle", "Midi Note")
            .link("Wire",       "Blue", "Python")
            .link("Net",        "LED Flash")
            .link("WireNet",    "Greenpeace")
            .link("All",        "Sea Power", "Zapper")
        ;
    }

}

