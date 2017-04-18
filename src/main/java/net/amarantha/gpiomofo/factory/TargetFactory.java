package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.target.AudioTarget;
import net.amarantha.gpiomofo.target.ShellTarget;
import net.amarantha.gpiomofo.target.*;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.midi.entity.MidiCommand;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.string.StringMap;

import java.util.ArrayList;
import java.util.List;

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

    public GpioTarget gpio(String name, int pin, Boolean activeState) {
        return create(name, GpioTarget.class,
            new StringMap()
                .add("pin", pin)
                .add("activeState", activeState)
            .get()
        );
    }

    //////////
    // HTTP //
    //////////

    public HttpTarget http(HttpCommand onCommand) {
        return http(getNextName("Http"), onCommand);
    }

    public HttpTarget http(String name, HttpCommand onCommand) {
        return http(name, onCommand, null);
    }

    public HttpTarget http(HttpCommand onCommand, HttpCommand offCommand) {
        return http(getNextName("Http"), onCommand, offCommand);
    }

    public HttpTarget http(String name, HttpCommand onCommand, HttpCommand offCommand) {
        return create(name, HttpTarget.class,
                new StringMap()
                    .add("onCommand", onCommand)
                    .add("offCommand", offCommand)
                .get()
        );
    }

    //////////
    // MIDI //
    //////////

    public MidiTarget midi(MidiCommand onCommand) {
        return midi(getNextName("Midi"), onCommand);
    }

    public MidiTarget midi(String name, MidiCommand onCommand) {
        return midi(name, onCommand, null);
    }

    public MidiTarget midi(MidiCommand onCommand, MidiCommand offCommand) {
        return midi(getNextName("Midi"), onCommand, offCommand);
    }

    public MidiTarget midi(String name, MidiCommand onCommand, MidiCommand offCommand) {
        return create(name, MidiTarget.class,
            new StringMap()
                .add("onCommand", onCommand)
                .add("offCommand", offCommand)
            .get()
        );
    }

    /////////
    // OSC //
    /////////

    public OscTarget osc(OscCommand onCommand) {
        return osc(getNextName("Osc"), onCommand);
    }

    public OscTarget osc(String name, OscCommand onCommand) {
        return osc(name, onCommand, null);
    }

    public OscTarget osc(OscCommand onCommand, OscCommand offCommand) {
        return osc(getNextName("Osc"), onCommand, offCommand);
    }

    public OscTarget osc(String name, OscCommand onCommand, OscCommand offCommand) {
        return create(name, OscTarget.class,
            new StringMap()
                .add("onCommand", onCommand)
                .add("offCommand", offCommand)
            .get()
        );
    }

    ///////////
    // Audio //
    ///////////

    public AudioTarget audio(String filename) {
        return audio(getNextName("Audio"), filename);
    }

    public AudioTarget audio(String filename, boolean loop) {
        return audio(getNextName("Audio"), filename, loop);
    }

    public AudioTarget audio(String name, String filename) {
        return audio(name, filename, false);
    }

    public AudioTarget audio(String name, String filename, boolean loop) {
        return create(name, AudioTarget.class,
            new StringMap()
                .add("filename", filename)
                .add("loop", loop)
            .get()
        );
    }

    ////////////
    // Python //
    ////////////

    public ShellTarget shell(String script) {
        return shell(getNextName("Shell"), script);
    }

    public ShellTarget shell(String name, String command) {
        return create(name, ShellTarget.class,
            new StringMap()
                .add("command", command)
            .get()
        );
    }

    /////////////
    // Chained //
    /////////////

    public ChainBuilder chain() {
        return chain(getNextName("Chain"));
    }

    public ChainBuilder chain(String name) {
        return new ChainBuilder(create(name, ChainedTarget.class));
    }

    public class ChainBuilder {
        private ChainedTarget chainedTarget;
        private ChainBuilder(ChainedTarget chainedTarget) {
            this.chainedTarget = chainedTarget;
        }
        public ChainBuilder add(Target target) {
            return add(null, target);
        }
        public ChainBuilder add(Integer delay, Target... targets) {
            chainedTarget.addTarget(delay, targets);
            return this;
        }
        public ChainedTarget build() {
            return chainedTarget;
        }
    }

    ////////////
    // Queued //
    ////////////

    public QueuedTarget queue(String name) {
        return create(name, QueuedTarget.class);
    }

    public QueuedTarget queue(Target... ts) {
        return queue(getNextName("Queue"), ts);
    }

    public QueuedTarget queue(String name, String... names) {
        List<Target> ts = new ArrayList<>(names.length);
        for ( String n : names ) {
            ts.add(get(n));
        }
        return queue(name, ts.toArray(new Target[names.length]));
    }

    public QueuedTarget queue(String name, Target... ts) {
        QueuedTarget target = queue(name).addTargets(ts);
        return target;
    }

    public QueueResetTarget queueReset(QueuedTarget t) {
        return queueReset(getNextName("QueueReset"), t);
    }

    public QueueResetTarget queueReset(String name, QueuedTarget t) {
        QueueResetTarget target = create(name, QueueResetTarget.class).queuedTarget(t);
        target.oneShot(true);
        return target;
    }

    //////////////
    // Inverted //
    //////////////

    public InvertedTarget invert(Target target) {
        return invert(getNextName("Invert"), target);
    }

    public InvertedTarget invert(String name, Target target) {
        return create(name, InvertedTarget.class).target(target);
    }

    //////////////////
    // Cancellation //
    //////////////////

    public CancellationTarget cancel(Target t) {
        return cancel(getNextName("Cancel"), t);
    }

    public CancellationTarget cancel(String name, Target t) {
        CancellationTarget target = create(name, CancellationTarget.class).cancel(t);
        target.oneShot(true);
        return target;
    }

    ////////////////
    // Pixel Tape //
    ////////////////

    public <P extends PixelTapeTarget> P pixelTape(Class<P> clazz) {
        return pixelTape(getNextName("PixelTape"), clazz);
    }

    public <P extends PixelTapeTarget> P pixelTape(String name, Class<P> clazz) {
        P target = create(name, clazz);
        target.oneShot(true);
        return target;
    }

    public StopPixelTapeTarget stopPixelTape() {
        return stopPixelTape(getNextName("StopPixelTape"));
    }

    public StopPixelTapeTarget stopPixelTape(String name) {
        StopPixelTapeTarget target = create(name, StopPixelTapeTarget.class);
        target.oneShot(true);
        return target;
    }

}
