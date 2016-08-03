package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.trigger.CompositeTrigger;
import net.amarantha.gpiomofo.trigger.GpioTrigger;
import net.amarantha.gpiomofo.trigger.HttpTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;

@Singleton
public class TriggerFactory extends Factory<Trigger> {

    @Inject private Injector injector;

    public TriggerFactory() {
        super("Trigger");
    }

    //////////
    // GPIO //
    //////////

    public GpioTrigger gpio(int pinNumber, PinPullResistance resistance, boolean triggerState) {
        return gpio(getNextName("Gpio"+pinNumber), pinNumber, resistance, triggerState);
    }

    public GpioTrigger gpio(String name, int pinNumber, PinPullResistance resistance, boolean triggerState) {

        GpioTrigger trigger =
            injector.getInstance(GpioTrigger.class)
                .setTriggerPin(pinNumber, resistance, triggerState);

        register(name, trigger);

        return trigger;
    }

    //////////
    // HTTP //
    //////////

    public HttpTrigger http() {
        return http(getNextName("Http"));
    }

    public HttpTrigger http(String name) {

        HttpTrigger trigger =
                injector.getInstance(HttpTrigger.class);

        register(name, trigger);

        return trigger;
    }

    ///////////////
    // Composite //
    ///////////////

    public CompositeTrigger composite(Trigger... triggers) {
        return composite(getNextName("Composite"), triggers);
    }

    public CompositeTrigger composite(String name, Trigger... triggers) {

        CompositeTrigger trigger =
            injector.getInstance(CompositeTrigger.class)
                .addTriggers(triggers);

        register(name, trigger);

        return trigger;
    }

}
