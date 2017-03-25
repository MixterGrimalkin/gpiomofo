package net.amarantha.gpiomofo.factory;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.http.Param;
import net.amarantha.gpiomofo.service.midi.MidiCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.PropertyNotFoundException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.amarantha.gpiomofo.Main.SCENARIO;
import static net.amarantha.gpiomofo.utility.Utility.bar;
import static net.amarantha.gpiomofo.utility.Utility.log;

@Singleton
public class ScenarioBuilder {

    private Scenario scenario;

    @Inject private Injector injector;
    @Inject private PropertiesService props;

    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;
    @Inject private LinkFactory links;

    public Scenario get() {
        return scenario;
    }

    public ScenarioBuilder load() {

        String name = getScenarioName();

        log(" LOADING SCENARIO: " + name, true);

        buildScenario(name);
        injectProperties();
        loadConfig();
        injectComponents();
        scenario.setup();

        return this;

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
        try {
            Class<? extends Scenario> clazz =
                    (Class<? extends Scenario>) Class.forName(SCENARIO_PACKAGE + "." + className);
            scenario = injector.getInstance(clazz);
        } catch (ClassNotFoundException e) {
            scenario= injector.getInstance(Scenario.class);
        }
        log("Class:\n    " + scenario.getClass().getName());
        scenario.setName(className);
    }

    ///////////////
    // Injection //
    ///////////////

    private void injectProperties() {
        try {
            Map<String, String> p = props.injectProperties(scenario);
            log("Properties:");
            if (!p.isEmpty()) {
                p.forEach((k, v) -> System.out.println("    " + k + " = " + v));
            } else {
                log("    (none)");
            }
        } catch (PropertyNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void injectComponents() {
        try {
            Field[] fields = scenario.getClass().getDeclaredFields();
            for ( Field field : fields ) {
                Annotation named = field.getAnnotation(Named.class);
                if ( named!=null ) {
                    String name = ((Named)named).value();
                    field.setAccessible(true);
                    if ( field.getType()==Trigger.class ) {
                        field.set(scenario, triggers.get(name));
                    }
                    if ( field.getType()==Target.class ) {
                        field.set(scenario, targets.get(name));
                    }
                }
                Annotation param = field.getAnnotation(Parameter.class);
                if ( param!=null ) {
                    String value = parameters.get(((Parameter)param).value());
                    if ( value!=null ) {
                        field.setAccessible(true);
                        if ( field.getType()==Integer.class || field.getType()==int.class) {
                            field.set(scenario, Integer.parseInt(value));
                        }
                        if ( field.getType()==RGB.class ) {
                            field.set(scenario, RGB.parse(value));
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    ////////////////////////
    // YAML Configuration //
    ////////////////////////

    @SuppressWarnings("unchecked")
    private void loadConfig() {
        Map<String, Map> config = null;
        String filename = props.getString("ScenariosDirectory", "scenarios")+"/"+scenario.getName()+".yaml";
        try (FileReader reader =new FileReader(filename)) {
            YamlReader yaml = new YamlReader(reader);
            config = (Map<String, Map>) yaml.read();
            log("Configuration:\n    " + filename);
        } catch (FileNotFoundException e) {
            log("Configuration:\n    (none)");
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

        processParameters(config);

        log(true, " TRIGGERS ", true);
        processTriggers(config);
        processCompositeTriggers(config);
        scenario.setupTriggers();

        log(true, " TARGETS ", true);
        processTargets(config);
        scenario.setupTargets();

        log(true, " LINKS ", true);
        processLinks(config);
        scenario.setupLinks();

        bar();

    }

    private Map<String, String> parameters;

    @SuppressWarnings("unchecked")
    private void processParameters(Map<String, Map> config) {
        if ( config!=null ) {
            parameters = (HashMap<String, String>) config.get("Parameters");
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
        switch (type.toLowerCase()) {
            case "gpio":
                buildGpioTrigger(name, config);
                break;
            case "http":
                buildHttpTrigger(name, config);
                break;
            case "osc":
                buildOscTrigger(name, config);
                break;
            default:
                throw new ScenarioBuilderException("Unknown type '" + type + "' for trigger '" + name + "'");
        }
    }

    private void buildGpioTrigger(String name, Map<String, String> config) throws ScenarioBuilderException {
        Integer pin = null;
        Boolean state = null;
        PinPullResistance resistance = null;
        try {
            pin = Integer.parseInt(config.get("pin"));
            state = Boolean.parseBoolean(config.get("triggerState").toLowerCase());
            resistance = PinPullResistance.valueOf(config.get("resistance").toUpperCase());
        } catch ( Exception ignored ) {}

        if ( pin==null || state==null || resistance==null ) {
            throw new ScenarioBuilderException("GPIO trigger '" + name + "' needs pin, triggerState and resistance");
        }

        addTriggerOptions(triggers.gpio(name, pin, resistance, state), config);
    }

    private void buildHttpTrigger(String name, Map<String, String> config) throws ScenarioBuilderException {
        addTriggerOptions(triggers.http(name), config);
    }

    private void buildOscTrigger(String name, Map<String, String> config) throws ScenarioBuilderException {
        Integer port = null;
        try {
            port = Integer.parseInt(config.get("port"));
        } catch ( Exception ignored ) {}
        String address = config.get("address");

        if ( port==null || address==null ) {
            throw new ScenarioBuilderException("OSC trigger '" + name + "' needs port and address");
        }

        addTriggerOptions(triggers.osc(name, port, address), config);
    }

    private void addTriggerOptions(Trigger trigger, Map<String, String> config) throws ScenarioBuilderException {
        String holdTimeStr = config.get("holdTime");
        if ( holdTimeStr!=null ) {
            try {
                trigger.setHoldTime(Integer.parseInt(holdTimeStr));
            } catch ( NumberFormatException e ) {
                throw new ScenarioBuilderException("Invalid Hold Time on trigger '" + trigger.getName() + "'");
            }
        }
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
        final List<Trigger> ts = new LinkedList<>();
        triggerNames.forEach((tn)->ts.add(triggers.get(tn)));
        addTriggerOptions(triggers.composite(name, ts.toArray(new Trigger[ts.size()])), config);
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
        switch (type.toLowerCase()) {
            case "gpio":
                buildGpioTarget(name, config);
                break;
            case "audio":
                buildAudioTarget(name, config);
                break;
            case "http":
                buildHttpTarget(name, config);
                break;
            case "midi":
                buildMidiTarget(name, config);
                break;
            case "osc":
                buildOscTarget(name, config);
                break;
            case "python":
                buildPythonTarget(name, config);
                break;
            default:
                throw new ScenarioBuilderException("Unknown type '" + type + "' for target '" + name + "'");
        }
    }

    private void buildGpioTarget(String name, Map<String, String> config) throws ScenarioBuilderException {
        Integer pin = null;
        Boolean state = null;
        try {
            pin = Integer.parseInt(config.get("pin"));
            switch ( config.get("activeState").toLowerCase() ) {
                case "true":
                    state = true;
                    break;
                case "toggle":
                case "null":
                    state = null;
                    break;
                default:
                    state = false;
            }
        } catch ( Exception ignored ) {}

        if ( pin==null ) {
            throw new ScenarioBuilderException("GPIO target '" + name + "' needs pin and activeState");
        }

        addTargetOptions(targets.gpio(name, pin, state), config);
    }

    private void buildAudioTarget(String name, Map<String, String> config) throws ScenarioBuilderException {
        String filename = config.get("filename");
        if ( filename==null ) {
            throw new ScenarioBuilderException("Audio target '" + name + "' needs filename (and optionally loop)");
        }
        boolean loop = false;
        String loopStr = config.get("loop");
        if ( loopStr!=null ) {
            loop = loopStr.equalsIgnoreCase("true");
        }
        addTargetOptions(targets.audio(name, filename, loop), config);
    }

    private void buildHttpTarget(String name, Map<String, String> config) throws ScenarioBuilderException {
        HttpCommand on = null;
        HttpCommand off = null;
        try {
            on = HttpCommand.fromString(config.get("onCommand"));
            off = HttpCommand.fromString(config.get("offCommand"));
        } catch ( Exception ignored ) {}

        if ( on==null ) {
            throw new ScenarioBuilderException("HTTP target '" + name + "' needs onCommand (and optionally offCommand)");
        }
        addTargetOptions(targets.http(name, on, off), config);
    }

    private void buildMidiTarget(String name, Map<String, String> config) throws ScenarioBuilderException {
        MidiCommand on = null;
        MidiCommand off = null;
        try {
            on = MidiCommand.fromString(config.get("onCommand"));
            off = MidiCommand.fromString(config.get("offCommand"));
        } catch ( Exception ignored ) {}
        if ( on==null ) {
            throw new ScenarioBuilderException("HTTP target '" + name + "' needs onCommand (and optionally offCommand)");
        }
        addTargetOptions(targets.midi(name, on, off), config);
    }

    private void buildOscTarget(String name, Map<String, String> config) throws ScenarioBuilderException {
        OscCommand on = null;
        OscCommand off = null;
        try {
            on = OscCommand.fromString(config.get("onCommand"));
            off = OscCommand.fromString(config.get("offCommand"));
        } catch ( Exception ignored ) {}
        if ( on==null ) {
            throw new ScenarioBuilderException("OSC target '" + name + "' needs onCommand (and optionally offCommand)");
        }
        addTargetOptions(targets.osc(name, on, off), config);
    }

    private void buildPythonTarget(String name, Map<String, String> config) throws ScenarioBuilderException {
        String filename = config.get("filename");
        if ( filename==null ) {
            throw new ScenarioBuilderException("Python target '" + name + "' needs filename");
        }
        addTargetOptions(targets.python(name, filename), config);
    }

    private void addTargetOptions(Target target, Map<String, String> config) throws ScenarioBuilderException {
        String oneShotStr = config.get("oneShot");
        if ( oneShotStr!=null ) {
            target.oneShot(oneShotStr.equalsIgnoreCase("true"));
        }
        String triggerStateStr = config.get("triggerState");
        if ( triggerStateStr!=null ) {
            target.triggerState(triggerStateStr.equalsIgnoreCase("true"));
        }
        String followTriggerStr = config.get("followTrigger");
        if ( followTriggerStr!=null ) {
            target.followTrigger(followTriggerStr.equalsIgnoreCase("true"));
        }
        String clearDelayStr = config.get("clearDelay");
        if ( clearDelayStr!=null ) {
            try {
                target.clearDelay(Long.parseLong(clearDelayStr));
            } catch ( NumberFormatException e ) {
                throw new ScenarioBuilderException("Invalid Clear Delay on target '" + target.getName() + "'");
            }
        }
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

    private static final String SCENARIO_PACKAGE = "net.amarantha.gpiomofo.scenario";

}