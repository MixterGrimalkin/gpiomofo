package net.amarantha.gpiomofo.trigger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.PinPullResistance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TriggerFactory {

    @Inject
    private Injector injector;

    public GpioTrigger gpio(String name, int pinNumber, PinPullResistance resistance, boolean triggerState) {

        GpioTrigger trigger =
            injector.getInstance(GpioTrigger.class)
                .setTriggerPin(pinNumber, resistance, triggerState);

        trigger.setName(name);
        registerTrigger(trigger);

        return trigger;
    }

    public HttpTrigger http(String name) {

        HttpTrigger trigger =
                injector.getInstance(HttpTrigger.class);

        trigger.setName(name);
        registerTrigger(trigger);

        return trigger;
    }

    public CompositeTrigger composite(Trigger... triggers) {

        StringBuilder name = new StringBuilder();
        for ( Trigger trigger : triggers ) {
            name.append(trigger.getName());
        }

        CompositeTrigger trigger =
            injector.getInstance(CompositeTrigger.class)
                .addTriggers(triggers);

        trigger.setName(name.toString());
        registerTrigger(trigger);

        return trigger;
    }

    ///////////////////
    // Registrations //
    ///////////////////

    private Map<String, Trigger> registeredTriggers = new HashMap<>();

    public void registerTrigger(Trigger trigger) {
        String name = trigger.getName();
        if ( registeredTriggers.containsKey(name) ) {
            throw new IllegalStateException("Trigger '" + name + "' is already registered");
        }
        registeredTriggers.put(name, trigger);
        System.out.println(trigger.getClass().getSimpleName() + ": " + name);
    }

    public Trigger getTrigger(String name) {
        return registeredTriggers.get(name);
    }

    public Collection<Trigger> getAllTriggers() {
        return registeredTriggers.values();
    }

    public void clearAll() {
        registeredTriggers.clear();
    }
}
