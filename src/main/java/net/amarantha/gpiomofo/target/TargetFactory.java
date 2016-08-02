package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.midi.MidiCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TargetFactory {

    @Inject private Injector injector;

    /////////////
    // Chained //
    /////////////

    public ChainBuilder chain() {
        return chain(getNextName("chain"));
    }

    public ChainBuilder chain(String name) {

        ChainedTarget target =
            injector.getInstance(ChainedTarget.class);

        target.setName(name);
        registerTarget(target);

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

    //////////
    // GPIO //
    //////////

    public GpioTarget gpio(int outputPin, Boolean outputState) {
        String namePrefix = "gpio[" + (outputState==null?"~":outputState?"":"!") + outputPin + "]";
        return gpio(getNextName(namePrefix), outputPin, outputState);
    }

    public GpioTarget gpio(String name, int outputPin, Boolean outputState) {

        GpioTarget target =
            injector.getInstance(GpioTarget.class)
                .outputPin(outputPin, outputState);

        registerTarget(name, target);

        return target;
    }

    //////////
    // HTTP //
    //////////

    public HttpTarget http(String method, String host, String path, String payload) {
        return http(getNextName("http-"+method.toLowerCase()), method, host, path, payload);
    }

    public HttpTarget http(String name, String method, String host, String path, String payload) {

        HttpTarget target =
            injector.getInstance(HttpTarget.class)
                .onCommand(method, host, path, payload);

        // Assume HTTP command is just an ON - can be overridden later
        target.oneShot(true);

        registerTarget(name, target);

        return target;
    }

    //////////
    // MIDI //
    //////////

    public MidiTarget midi(MidiCommand onCommand, MidiCommand offCommand) {
        return midi(getNextName("midi"), onCommand, offCommand);
    }

    public MidiTarget midi(String name, MidiCommand onCommand, MidiCommand offCommand) {

        MidiTarget target =
            injector.getInstance(MidiTarget.class)
                .onCommand(onCommand)
                .offCommand(offCommand);

        registerTarget(name, target);

        return target;
    }

    ///////////
    // Audio //
    ///////////

    public AudioTarget audio(String filename) {
        return audio(getNextName("audio"), filename);
    }

    public AudioTarget audio(String name, String filename) {

        AudioTarget target =
            injector.getInstance(AudioTarget.class)
                .setAudioFile(filename);

        registerTarget(name, target);

        return target;
    }

    ////////////
    // Python //
    ////////////

    public PythonTarget python(String script) {
        return python(getNextName("python"), script);
    }

    public PythonTarget python(String name, String script) {

        PythonTarget target =
            injector.getInstance(PythonTarget.class)
                .scriptFile(script);

        target.setName(name);
        registerTarget(target);

        return target;

    }

    ///////////////////
    // Registrations //
    ///////////////////

    private Map<String, Target> registeredTargets = new HashMap<>();

    public void registerTarget(String name, Target target) {
        target.setName(name);
        registerTarget(target);
    }

    public void registerTarget(Target target) {
        String name = target.getName();
        if ( registeredTargets.containsKey(name) ) {
            throw new IllegalStateException("Target '" + name + "' is already registered");
        }
        registeredTargets.put(name, target);
        System.out.println(target.getClass().getSimpleName() + ": " + name);
    }

    public Target getTarget(String name) {
        return registeredTargets.get(name);
    }

    public Collection<Target> getAllTargets() {
        return registeredTargets.values();
    }

    public void clearAll() {
        registeredTargets.clear();
    }

    private String getNextName() {
        return getNextName("target");
    }
    private String getNextName(String prefix) {
        Integer nextId = nextIds.get(prefix);
        if ( nextId==null ) {
            nextId = 1;
        }
        String name = prefix + "-" + nextId++;
        nextIds.put(prefix, nextId);
        return name;
    }

    private Map<String, Integer> nextIds = new HashMap<>();

}
