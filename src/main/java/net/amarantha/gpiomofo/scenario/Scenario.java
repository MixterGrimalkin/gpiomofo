package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;

public abstract class Scenario {

    @Inject protected TriggerFactory triggers;
    @Inject protected TargetFactory targets;
    @Inject protected LinkFactory links;

    public void setup() {
        System.out.println(BAR+"\n TRIGGERS \n"+BAR);
        setupTriggers();
        System.out.println(BAR+"\n TARGETS \n"+BAR);
        setupTargets();
        System.out.println(BAR+"\n LINKS \n"+BAR);
        setupLinks();
        System.out.println(BAR);
    }

    public void stop() {}

    public abstract void setupTriggers();

    public abstract void setupTargets();

    public abstract void setupLinks();

    public static final String BAR = "--------------------------------------------------";

}
