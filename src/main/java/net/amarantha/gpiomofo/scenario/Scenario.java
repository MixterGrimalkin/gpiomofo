package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.reflection.ReflectionUtils;
import net.amarantha.utils.service.ServiceFactory;
import net.amarantha.utils.time.Now;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static net.amarantha.gpiomofo.core.Constants.*;
import static net.amarantha.utils.properties.PropertiesService.isArgumentPresent;
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

    public List<ApiParam> getApiTemplate() {
        return new LinkedList<>();
    }

    public void incomingApiCall(Map<String, String> params) {

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

        log(true, _BOLD+getName()+_RESET, false);

        if ( props.isArgumentPresent(DESCRIBE) ) {

            log(_ITALIC + "Class\n\t" + _RESET + getClass().getName());
            log(_ITALIC + "Configuration\n\t" + _RESET + (configFilename == null ? "(none)" : configFilename));

            log(_ITALIC + "Parameters" + _RESET);
            if (parameters.isEmpty()) {
                log("\t(none)");
            } else {
                parameters.forEach((key, value) -> log("\t" + key + _YELLOW + "=" + _RESET + value));
            }

            log(_ITALIC + "Triggers" + _RESET);
            if (triggers.getAll().isEmpty()) {
                log("\t(none)");
            } else {
                triggers.getAll().forEach((trigger) -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\t")
                            .append(trigger.getClass().getSimpleName().replaceAll("Trigger", ""))
                            .append(": ")
                            .append(_BOLD)
                            .append(trigger.getName())
                            .append(_RESET)
                            .append(printParameters(trigger));
                    log(sb.toString());
                });
            }

            log(_ITALIC + "Targets" + _RESET);
            if (targets.getAll().isEmpty()) {
                log("\t(none)");
            } else {
                targets.getAll().forEach((target) -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\t")
                            .append(target.getClass().getSimpleName().replaceAll("Target", ""))
                            .append(": ")
                            .append(_BOLD)
                            .append(target.getName())
                            .append(_RESET)
                            .append(printParameters(target));
                    log(sb.toString());
                });
            }

            log(_ITALIC + "Links" + _RESET);
            if (links.getLinks().isEmpty()) {
                log("\t(none)");
            } else {
                links.getLinks().forEach((trig, targs) -> {
                    StringBuilder sb = new StringBuilder();
                    targs.forEach((targ) -> sb.append("[").append(_BOLD).append(targ.getName()).append(_RESET).append("]"));
                    if (trig.getCustomHandlerCount() > 0) {
                        sb.append("(+" + trig.getCustomHandlerCount() + ")");
                    }
                    log("\t[" + _BOLD + trig.getName() + _RESET + "]-->" + sb.toString());
                });
            }
        }
    }

    private static String printParameters(Object trigger) {
        StringBuilder sb = new StringBuilder();
        ReflectionUtils.iterateAnnotatedFields(trigger, Parameter.class, (field, param)->{
            try {
                Object value = field.get(trigger);
                if ( value != null ) {
                    String name = field.getName().length() > 3 ? field.getName().substring(0,4) : field.getName();
                    String valueStr = value.toString().length() > 3 ? value.toString().substring(0,4) : value.toString();
                    sb.append(" ").append(name).append("=").append(valueStr);
                }
            } catch (IllegalAccessException ignored) {}
        });
        if ( sb.length() > 0 ) {
            return new StringBuilder().append(_YELLOW).append(" (").append(sb.toString().substring(1)).append(")").append(_RESET).toString();
        } else {
            return "";
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
