package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.pixeltape.NeoPixelGUI;
import net.amarantha.gpiomofo.utility.PropertyManager;
import net.amarantha.gpiomofo.utility.PropertyNotFoundException;

import java.util.Map;

import static net.amarantha.gpiomofo.utility.Utility.bar;
import static net.amarantha.gpiomofo.utility.Utility.log;

public abstract class Scenario {

    @Inject private PropertyManager props;

    public void load() {
        log(" LOADING SCENARIO: " + getClass().getSimpleName());
        injectProperties();
        log(true, " TRIGGERS ", true);
        setupTriggers();
        log(true, " TARGETS ", true);
        setupTargets();
        log(true, " LINKS ", true);
        setupLinks();
        bar();
    }

    private void injectProperties() {
        try {
            Map<String, String> p = props.injectProperties(this);
            if ( !p.isEmpty() ) {
                bar();
                p.forEach((k, v) -> System.out.println(k + " = " + v));
            }
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

    @Inject private NeoPixel neoPixel;

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
        return requiresPixelTape() && neoPixel instanceof NeoPixelGUI;
    }

}
