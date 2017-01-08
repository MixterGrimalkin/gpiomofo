package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.trigger.*;

@Singleton
public class TriggerFactory extends Factory<Trigger> {

    @Inject private Injector injector;

    public TriggerFactory() {
        super("Trigger");
    }

    private boolean gpioUsed;

    public boolean isGpioUsed() {
        return gpioUsed;
    }

    //////////
    // GPIO //
    //////////

    public GpioTrigger gpio(int pinNumber, PinPullResistance resistance, boolean triggerState) {
        return gpio(getNextName("Gpio"+pinNumber), pinNumber, resistance, triggerState);
    }

    public GpioTrigger gpio(String name, int pinNumber, PinPullResistance resistance, boolean triggerState) {

        gpioUsed = true;

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

    /////////
    // OSC //
    /////////

    public OscTrigger osc(int port, String address) {
        return osc(getNextName("Osc"), port, address);
    }

    public OscTrigger osc(String name, int port, String address) {

        OscTrigger trigger =
            injector.getInstance(OscTrigger.class)
                .setReceiver(port, address);

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

    //////////////
    // Inverted //
    //////////////

    public InvertedTrigger invert(Trigger t) {
        return invert(getNextName("Invert"), t);
    }

    public InvertedTrigger invert(String name, Trigger t) {

        InvertedTrigger trigger =
            injector.getInstance(InvertedTrigger.class)
                .trigger(t);

        register(name, trigger);

        return trigger;
    }

    ///////////
    // Range //
    ///////////

    public RangeTrigger range() {
        return range(getNextName("Range"));
    }

    public RangeTrigger range(String name) {

        RangeTrigger trigger =
            injector.getInstance(RangeTrigger.class);

        register(name, trigger);

        return trigger;

    }

}
