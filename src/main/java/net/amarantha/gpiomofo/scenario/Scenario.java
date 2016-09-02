package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.utility.Property;
import net.amarantha.gpiomofo.utility.PropertyManager;
import net.amarantha.gpiomofo.utility.PropertyNotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import static java.lang.System.out;
import static net.amarantha.gpiomofo.Main.BAR;

public abstract class Scenario {

    @Inject private PropertyManager props;

    public void load() {
        out.println(BAR+"\n LOADING SCENARIO: " + getClass().getSimpleName());
        injectProperties();
        out.println(BAR+"\n TRIGGERS \n"+BAR);
        setupTriggers();
        out.println(BAR+"\n TARGETS \n"+BAR);
        setupTargets();
        out.println(BAR+"\n LINKS \n"+BAR);
        setupLinks();
        out.println(BAR);
    }

    private void injectProperties() {
        try {
            Map<String, String> p = props.injectProperties(this);
            p.forEach((k,v)-> System.out.println(" - " + k + " = " + v));
        } catch (PropertyNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public abstract void setupTriggers();

    public abstract void setupTargets();

    public abstract void setupLinks();

    public void start() {}

    public void stop() {}

    ///////////////
    // Factories //
    ///////////////

    @Inject protected TriggerFactory triggers;
    @Inject protected TargetFactory targets;
    @Inject protected LinkFactory links;

    ///////////////////////
    // Required Services //
    ///////////////////////

    public boolean requiresGpio() {
        return triggers.isGpioUsed() || targets.isGpioUsed();
    }

    public boolean requiresMidi() {
        return targets.isMidiUsed();
    }

    public boolean requiresPixelTape() {
        return targets.isPixelTapeUsed();
    }

    public boolean requiresGUI() {
        return requiresPixelTape();
    }

}
