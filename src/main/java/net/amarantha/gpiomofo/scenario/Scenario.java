package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.service.ServiceFactory;
import net.amarantha.utils.time.Now;

import static net.amarantha.gpiomofo.core.Constants.WITH_SERVER;
import static net.amarantha.utils.shell.Utility.log;

public class Scenario {

    @Inject protected TriggerFactory triggers;
    @Inject protected TargetFactory targets;
    @Inject protected LinkFactory links;

    @Inject private PropertiesService props;
    @Inject private ServiceFactory services;
    @Inject private WebService web;
    @Inject private Now now;

    public final void start() {
        log(true, " Starting Up... ", true);
        services.startAll();
        startup();
        if ( props.isArgumentPresent(WITH_SERVER) ) {
            web.start();
        }
        log(true, now.time().toString() + ": Ready");
    }

    public final void stop() {
        log(true, " Shutting Down... ", true);
        if ( props.isArgumentPresent(WITH_SERVER) ) {
            web.stop();
        }
        shutdown();
        services.stopAll();
        log(true, " So long, and thanks for all the fish! ", true);
    }

    ///////////////
    // Lifecycle //
    ///////////////

    public void setupTriggers() {}

    public void setupTargets() {}

    public void setupLinks() {}

    public void setup() {}

    public void startup() {}

    public void shutdown() {}

    //////////
    // Name //
    //////////

    private String name;

    public final String getName() {
        return name;
    }

    public final Scenario setName(String name) {
        this.name = name;
        return this;
    }

}
