package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.midi.MidiCommand;
import net.amarantha.gpiomofo.target.*;

@Singleton
public class TargetFactory extends Factory<Target> {

    @Inject private Injector injector;

    public TargetFactory() {
        super("Target");
    }

    //////////
    // GPIO //
    //////////

    public GpioTarget gpio(int outputPin, Boolean outputState) {
        return gpio(getNextName("Gpio"+outputPin), outputPin, outputState);
    }

    public GpioTarget gpio(String name, int outputPin, Boolean outputState) {

        GpioTarget target =
            injector.getInstance(GpioTarget.class)
                .outputPin(outputPin, outputState);

        register(name, target);

        return target;
    }

    //////////
    // HTTP //
    //////////

    public HttpTarget http(String method, String host, String path, String payload) {
        return http(getNextName("Http-"+method), method, host, path, payload);
    }

    public HttpTarget http(String name, String method, String host, String path, String payload) {

        HttpTarget target =
            injector.getInstance(HttpTarget.class)
                .onCommand(method, host, path, payload);

        // Assume HTTP command is just an ON - can be overridden later
        target.oneShot(true);

        register(name, target);

        return target;
    }

    //////////
    // MIDI //
    //////////

    public MidiTarget midi(MidiCommand onCommand, MidiCommand offCommand) {
        return midi(getNextName("Midi"), onCommand, offCommand);
    }

    public MidiTarget midi(String name, MidiCommand onCommand, MidiCommand offCommand) {

        MidiTarget target =
            injector.getInstance(MidiTarget.class)
                .onCommand(onCommand)
                .offCommand(offCommand);

        register(name, target);

        return target;
    }

    ///////////
    // Audio //
    ///////////

    public AudioTarget audio(String filename) {
        return audio(getNextName("Audio"), filename);
    }

    public AudioTarget audio(String name, String filename) {

        AudioTarget target =
            injector.getInstance(AudioTarget.class)
                .setAudioFile(filename);

        register(name, target);

        return target;
    }

    ////////////
    // Python //
    ////////////

    public PythonTarget python(String script) {
        return python(getNextName("Python"), script);
    }

    public PythonTarget python(String name, String script) {

        PythonTarget target =
            injector.getInstance(PythonTarget.class)
                .scriptFile(script);

        register(name, target);

        return target;

    }

    /////////////
    // Chained //
    /////////////

    public ChainBuilder chain() {
        return chain(getNextName("Chain"));
    }

    public ChainBuilder chain(String name) {

        ChainedTarget target =
            injector.getInstance(ChainedTarget.class);

        register(name, target);

        return new ChainBuilder(target);
    }

    public class ChainBuilder {
        private ChainedTarget chainedTarget;
        private ChainBuilder(ChainedTarget chainedTarget) {
            this.chainedTarget = chainedTarget;
        }
        public ChainBuilder add(Target... targets) {
            return add(null, targets);
        }
        public ChainBuilder add(Integer delay, Target... targets) {
            chainedTarget.addTarget(delay, targets);
            return this;
        }
        public ChainedTarget build() {
            return chainedTarget;
        }
    }

}
