package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.trigger.*;
import net.amarantha.gpiomofo.service.gpio.touch.TouchTrigger;
import net.amarantha.gpiomofo.service.gpio.touch.TouchTriggerSet;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeTrigger;

import java.util.Map;

import static net.amarantha.utils.reflection.ReflectionUtils.iterateAnnotatedFields;
import static net.amarantha.utils.reflection.ReflectionUtils.reflectiveSet;

@Singleton
public class TriggerFactory extends Factory<Trigger> {

    @Inject private Injector injector;
    @Inject private ServiceFactory services;

    public TriggerFactory() {
        super("Trigger");
    }

    private boolean gpioUsed;
    private boolean touchUsed;

    public boolean isGpioUsed() {
        return gpioUsed;
    }

    public boolean isTouchUsed() {
        return touchUsed;
    }

    public <T extends Trigger> T create(Class<T> triggerClass, Map<String, String> config) {
        return create(getNextName(), triggerClass, config);
    }

    public <T extends Trigger> T create(String name, Class<T> triggerClass, Map<String, String> config) {
        T trigger = injector.getInstance(triggerClass);
        services.inject(trigger);
        iterateAnnotatedFields(trigger, Parameter.class,
                (field, annotation) ->
                        reflectiveSet(trigger, field, config.get(annotation.value()),
                            (type, value) ->
                                    type==PinPullResistance.class ? PinPullResistance.valueOf(value) : null)
        );
        register(name, trigger);
        return trigger;
    }

    //////////
    // GPIO //
    //////////

    public GpioTrigger gpio(int pinNumber, PinPullResistance resistance, boolean triggerState) {
        return gpio(getNextName("Gpio" + pinNumber), pinNumber, resistance, triggerState);
    }

    public GpioTrigger gpio(String name, int pinNumber, PinPullResistance resistance, boolean triggerState) {

        gpioUsed = true;

        GpioTrigger trigger = injector.getInstance(GpioTrigger.class);
        services.inject(trigger);
        trigger.setTriggerPin(pinNumber, resistance, triggerState);

        register(name, trigger);

        return trigger;
    }

    ///////////
    // Touch //
    ///////////

    public TouchTrigger touch(int pinNumber, boolean triggerState) {
        return touch(getNextName("Touch" + pinNumber), pinNumber, triggerState);
    }

    public TouchTrigger touch(String name, int pinNumber, boolean triggerState) {

        touchUsed = true;

        TouchTrigger trigger =
                injector.getInstance(TouchTrigger.class)
                        .setPin(pinNumber, triggerState);

        register(name, trigger);

        return trigger;

    }

    public TouchTriggerSet touchSet(int leftPin, int rightPin) {
        return touchSet(getNextName("TouchSet"), leftPin, rightPin);
    }

    public TouchTriggerSet touchSet(String name, int leftPin, int rightPin) {

        touchUsed = true;

        TouchTriggerSet set =
                injector.getInstance(TouchTriggerSet.class)
                    .setPins(leftPin, rightPin);

        register(name+"/TapLeft", set.getTapLeftTrigger());
        register(name+"/TapRight", set.getTapRightTrigger());
        register(name+"/HoldLeft", set.getHoldLeftTrigger());
        register(name+"/HoldRight", set.getHoldRightTrigger());
        register(name+"/DblTapLeft", set.getDblTapLeftTrigger());
        register(name+"/DblTapRight", set.getDblTapRightTrigger());
        register(name+"/SwipeLeft", set.getSwipeLeftTrigger());
        register(name+"/SwipeRight", set.getSwipeRightTrigger());

        return set;

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
