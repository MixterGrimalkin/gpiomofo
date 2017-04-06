package net.amarantha.gpiomofo.factory;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.factory.entity.ScenarioBuilderException;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.CompositeTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.PropertyNotFoundException;
import net.amarantha.utils.reflection.ReflectionUtils;
import net.amarantha.utils.service.ServiceFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.amarantha.gpiomofo.core.Constants.SCENARIO;
import static net.amarantha.utils.reflection.ReflectionUtils.iterateAnnotatedFields;
import static net.amarantha.utils.reflection.ReflectionUtils.reflectiveSet;
import static net.amarantha.utils.shell.Utility.log;

@Singleton
public class ScenarioBuilder {

    @Inject private Injector injector;
    @Inject private PropertiesService props;
    @Inject private ServiceFactory services;
    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;
    @Inject private LinkFactory links;

    private Scenario scenario;

    public Scenario getScenario() {
        return scenario;
    }

    //////////
    // Load //
    //////////

    public ScenarioBuilder loadScenario() {
        return loadScenario(getScenarioName());
    }

    public ScenarioBuilder loadScenario(String name) {
        buildScenario(name);
        services.injectServices(scenario);
        props.injectPropertiesOrExit(scenario);
        buildComponentsFromConfig();
        injectComponents();
        scenario.setup();
        logScenarioSetup();
        return this;
    }

    private void logScenarioSetup() {

        log(true, " "+scenario.getName(), true);

        log("Class:\n\t"+scenario.getClass().getName());
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

    private String getScenarioName() {
        String scenarioName = "";
        try {
            String commandLineClassName = props.getArgumentValue(SCENARIO);
            if (commandLineClassName == null) {
                scenarioName = props.getString("Scenario");
            } else {
                scenarioName = commandLineClassName;
            }
            props.getString("Scenario", scenarioName);
        } catch (PropertyNotFoundException e) {
            System.out.println("No Scenario specified\n\nSee: gpiomofo.sh -help\n");
            System.exit(1);
        }
        return scenarioName;
    }

    @SuppressWarnings("unchecked")
    private void buildScenario(String className) {
        Class<Scenario> clazz = ReflectionUtils.getClass(props.getString("ScenarioPackage", DEFAULT_SCENARIO_PACKAGE) + "." + className);
        if ( clazz==null ) {
            clazz = ReflectionUtils.getClass(DEFAULT_SCENARIO_PACKAGE + "." + className);
            if ( clazz==null ) {
                clazz = Scenario.class;
            }
        }
        scenario = injector.getInstance(clazz);
        scenario.setName(className);
    }

    ///////////////
    // Injection //
    ///////////////

    private void injectComponents() {
        iterateAnnotatedFields(scenario, Named.class,
            (field, annotation)->
                reflectiveSet(scenario, field, annotation.value(),
                    (type, name)->{
                        if ( type==Trigger.class ) {
                            return triggers.get(name);
                        } else if ( type==Target.class ) {
                            return targets.get(name);
                        }
                        return null;
                    }
                )
        );
    }

    ////////////////////////
    // YAML Configuration //
    ////////////////////////

    private String configFilename;

    @SuppressWarnings("unchecked")
    private void buildComponentsFromConfig() {
        Map<String, Map> config = null;
        String filename = props.getString("ScenariosDirectory", "scenarios")+"/"+scenario.getName()+".yaml";
        try (FileReader reader =new FileReader(filename)) {
            YamlReader yaml = new YamlReader(reader);
            config = (Map<String, Map>) yaml.read();
            configFilename = filename;
        } catch (FileNotFoundException e) {
            if ( scenario.getClass()==Scenario.class ) {
                log("No configuration found for custom scenario '"+scenario.getName()+"'");
                System.exit(1);
            }
        } catch (IOException e) {
            log("Could not read configuration.\n" + e.getMessage());
            System.exit(1);
        }
        processConfig(config);
    }

    private void processConfig(Map<String, Map> config) {

        injectParameters(config);

        processTriggers(config);
        processCompositeTriggers(config);
        scenario.setupTriggers();

        processTargets(config);
        scenario.setupTargets();

        processLinks(config);
        scenario.setupLinks();

    }

    private Map<String, String> parameters = new HashMap<>();

    @SuppressWarnings("unchecked")
    private void injectParameters(Map<String, Map> config) {
        if ( config!=null && config.get("Parameters")!=null ) {
            parameters = (HashMap<String, String>) config.get("Parameters");
            iterateAnnotatedFields(scenario, Parameter.class,
                    (field,annotation)-> reflectiveSet(scenario, field, parameters.get(annotation.value()))
            );
        }
    }

    //////////////
    // Triggers //
    //////////////

    @SuppressWarnings("unchecked")
    private void processTriggers(Map<String, Map> config) {
        List<ScenarioBuilderException> errors = new LinkedList<>();
        if ( config!=null ) {
            HashMap<String, Map> triggerConfig = (HashMap<String, Map>) config.get("Triggers");
            if ( triggerConfig!=null ) {
                for (Entry<String, Map> entry : triggerConfig.entrySet()) {
                    try {
                        buildTrigger(entry.getKey(), (HashMap<String, String>) entry.getValue());
                    } catch (ScenarioBuilderException e) {
                        errors.add(e);
                    }
                }
            }
        }
        checkErrors(errors);
    }

    private void buildTrigger(String name, Map<String, String> config) throws ScenarioBuilderException {
        String type = config.get("type");
        Class<Trigger> triggerClass = ReflectionUtils.getClass(props.getString("TriggerPackage", DEFAULT_TRIGGER_PACKAGE)+"."+type+"Trigger");
        if ( triggerClass==null ) {
            triggerClass = ReflectionUtils.getClass(DEFAULT_TRIGGER_PACKAGE+"."+type+"Trigger");
            if ( triggerClass==null ) {
                throw new ScenarioBuilderException("Unknown type '" + type + "' for trigger '" + name + "'");
            }
        }
        triggers.create(name, triggerClass, config).enable();
    }

    ////////////////////////
    // Composite Triggers //
    ////////////////////////

    @SuppressWarnings("unchecked")
    private void processCompositeTriggers(Map<String, Map> config) {
        List<ScenarioBuilderException> errors = new LinkedList<>();
        if ( config!=null ) {
            HashMap<String, Map> compTriggerConfigs = (HashMap<String, Map>) config.get("CompositeTriggers");
            if ( compTriggerConfigs!=null ) {
                for (Entry<String, Map> entry : compTriggerConfigs.entrySet()) {
                    try {
                        List<String> triggerNames = ((HashMap<String, List>)entry.getValue()).get("triggers");
                        buildCompositeTrigger(entry.getKey(), triggerNames, (HashMap<String, String>) entry.getValue());
                    } catch (ScenarioBuilderException e) {
                        errors.add(e);
                    }
                }
            }
        }
        checkErrors(errors);
    }

    private void buildCompositeTrigger(String name, List<String> triggerNames, Map<String, String> config) throws ScenarioBuilderException {
        CompositeTrigger trigger = triggers.create(name, CompositeTrigger.class, config);
        triggerNames.forEach((tn) -> trigger.addTriggers(triggers.get(tn)));
    }

    /////////////
    // Targets //
    /////////////

    @SuppressWarnings("unchecked")
    private void processTargets(Map<String, Map> config) {
        List<ScenarioBuilderException> errors = new LinkedList<>();
        if ( config!=null ) {
            HashMap<String, Map> targetConfigs = (HashMap<String, Map>) config.get("Targets");
            if ( targetConfigs!=null ) {
                for (Entry<String, Map> entry : targetConfigs.entrySet() ) {
                    try {
                        buildTarget(entry.getKey(), (HashMap<String, String>) entry.getValue());
                    } catch (ScenarioBuilderException e) {
                        errors.add(e);
                    }
                }
            }
        }
        checkErrors(errors);
    }

    private void buildTarget(String name, Map<String, String> config) throws ScenarioBuilderException {
        String type = config.get("type");
        Class<Target> targetClass = ReflectionUtils.getClass(props.getString("TargetPackage", DEFAULT_TARGET_PACKAGE)+"."+type+"Target");
        if ( targetClass==null ) {
            targetClass = ReflectionUtils.getClass(DEFAULT_TARGET_PACKAGE+"."+type+"Target");
            if ( targetClass==null ) {
                throw new ScenarioBuilderException("Unknown type '" + type + "' for target '" + name + "'");
            }
        }
        targets.create(name, targetClass, config).enable();
    }

    ///////////
    // Links //
    ///////////

    @SuppressWarnings("unchecked")
    private void processLinks(Map<String, Map> config) {
        if ( config!=null ) {
            HashMap<String, List<String>> linkConfigs = (HashMap<String, List<String>>) config.get("Links");
            if ( linkConfigs!=null ) {
                for (Entry<String, List<String>> entry : linkConfigs.entrySet() ) {
                    buildLink(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void buildLink(String triggerName, List<String> targetNames) {
        links.link(triggerName, targetNames.toArray(new String[targetNames.size()]));
    }

    /////////////
    // Utility //
    /////////////

    private void checkErrors(List<ScenarioBuilderException> errors ) {
        if ( !errors.isEmpty() ) {
            log("ERRORS!");
            for ( ScenarioBuilderException e : errors ) {
                log("    "+e.getMessage());
            }
            System.exit(1);
        }
    }

    private static final String DEFAULT_SCENARIO_PACKAGE = "net.amarantha.gpiomofo.scenario";
    private static final String DEFAULT_TRIGGER_PACKAGE = "net.amarantha.gpiomofo.trigger";
    private static final String DEFAULT_TARGET_PACKAGE = "net.amarantha.gpiomofo.target";

}