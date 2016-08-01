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

    public GpioTarget gpio(String name, int outputPin, Boolean outputState) {

        GpioTarget target =
            injector.getInstance(GpioTarget.class)
                .outputPin(outputPin, outputState);

        target.setName(name);
        registerTarget(target);

        return target;
    }

    public HttpTarget http(String name, String method, String host, String path, String payload) {
        HttpTarget target =
            injector.getInstance(HttpTarget.class)
                .onCommand(method, host, path, payload);

        // Assume HTTP command is just an ON - can be overridden later
        target.oneShot(true);

        target.setName(name);
        registerTarget(target);

        return target;
    }

    public MidiTarget midi(String name, MidiCommand onCommand, MidiCommand offCommand) {

        MidiTarget target =
            injector.getInstance(MidiTarget.class)
                .onCommand(onCommand)
                .offCommand(offCommand);

        target.setName(name);
        registerTarget(target);

        return target;
    }

    public AudioTarget audio(String name, String filename) {

        AudioTarget target =
            injector.getInstance(AudioTarget.class)
                .setAudioFile(filename);

        target.setName(name);
        registerTarget(target);

        return target;
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

}
