package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.service.ServiceFactory;
import net.amarantha.utils.time.Now;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static net.amarantha.gpiomofo.core.Constants.WITH_SERVER;
import static net.amarantha.utils.properties.PropertiesService.isArgumentPresent;
import static net.amarantha.utils.shell.Utility.log;

public class Scenario {

    @Inject protected TriggerFactory triggers;
    @Inject protected TargetFactory targets;
    @Inject protected LinkFactory links;

    @Inject private ServiceFactory services;
    @Inject private WebService web;
    @Inject private Now now;

    public final void start() {
        log(true, " Starting Up... ", true);
        services.startAll();
        startup();
        if ( isArgumentPresent(WITH_SERVER) ) {
            web.start();
        }
        log(true, now.time().toString() + ": Ready");
    }

    public final void stop() {
        log(true, " Shutting Down... ", true);
        if ( isArgumentPresent(WITH_SERVER) ) {
            web.stop();
        }
        shutdown();
        services.stopAll();
        log(true, " So long, and thanks for all the fish! ", true);
    }

    /////////
    // API //
    /////////

    public Map<String, String> getApiTemplate() {
        return new HashMap<>();
    }

    public void incomingApiCall(Map<String, String> json) {

    }

    ///////////////
    // Lifecycle //
    ///////////////

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

    ///////////////
    // Log Setup //
    ///////////////

    public void logSetup() {

        log(true, " "+getName(), true);

        log("Class:\n\t"+getClass().getName());
        log("Configuration:\n\t"+(configFilename==null?"(none)":configFilename));

        log("Parameters:");
        if ( parameters.isEmpty() ) {
            log("\t(none)");
        } else {
            parameters.forEach((key,value)->log("\t"+key+"="+value));
        }

        log("Triggers:");
        if ( triggers.getAll().isEmpty() ) {
            log("\t(none)");
        } else {
            triggers.getAll().forEach((trigger) ->
                    log("\t" + trigger.getClass().getSimpleName().replaceAll("Trigger", "") + ": " + trigger.getName()));
        }

        log("Targets:");
        if ( targets.getAll().isEmpty() ) {
            log("\t(none)");
        } else {
            targets.getAll().forEach((target) ->
                    log("\t" + target.getClass().getSimpleName().replaceAll("Target", "") + ": " + target.getName()));
        }

        log("Links:");
        if ( links.getLinks().isEmpty() ) {
            log("\t(none)");
        } else {
            links.getLinks().forEach((trig, targs) -> {
                StringBuilder sb = new StringBuilder();
                targs.forEach((targ)->sb.append("[").append(targ.getName()).append("]"));
                log("\t["+trig.getName()+"]-->"+sb.toString());
            });
        }
    }

    private String configFilename;
    private Map<String, String> parameters = new HashMap<>();

    public void setConfigFilename(String configFilename) {
        this.configFilename = configFilename;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
