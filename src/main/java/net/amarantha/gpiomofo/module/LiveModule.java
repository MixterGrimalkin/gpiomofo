package net.amarantha.gpiomofo.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.pixeltape.NeoPixelWS281X;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.RaspberryPi3;
import net.amarantha.gpiomofo.service.http.HttpService;
import net.amarantha.gpiomofo.service.http.HttpServiceImpl;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.midi.MidiServiceImpl;
import net.amarantha.gpiomofo.service.osc.OscService;
import net.amarantha.gpiomofo.service.osc.OscServiceImpl;
import net.amarantha.gpiomofo.utility.PropertyManager;
import net.amarantha.gpiomofo.utility.PropertyNotFoundException;
import org.reflections.Reflections;

import java.util.Set;

import static net.amarantha.gpiomofo.Main.LIST_SCENARIOS;
import static net.amarantha.gpiomofo.Main.SCENARIO;

public class LiveModule extends AbstractModule {

    private Class<? extends Scenario> scenarioClass;

    private int pixelTapeRefresh;

    public LiveModule() {
        PropertyManager props = new PropertyManager();
        pixelTapeRefresh = props.getInt("PixelTapeRefresh", 50);
        if ( props.isArgumentPresent(LIST_SCENARIOS) ) {
            listScenarios();
            System.exit(0);
        }
        String className = "";
        try {
            String commandLineClassName = props.getArgumentValue(SCENARIO);
            if ( commandLineClassName==null ) {
                className = props.getString("Scenario");
            } else {
                className = commandLineClassName;
                props.getString("Scenario", className);
            }
            scenarioClass = (Class<? extends Scenario>) Class.forName("net.amarantha.gpiomofo.scenario."+className);
        } catch (ClassNotFoundException e) {
            System.out.println("Scenario '" + className + "' not found\n\nUse: gpiomofo.sh -list\n");
            System.exit(1);
        } catch (PropertyNotFoundException e) {
            System.out.println("No Scenario specified\n\nUse: gpiomofo.sh -list\n");
            System.exit(1);
        }
    }

    private void listScenarios() {
        System.out.println("Scanning Scenarios...");
        Reflections reflections = new Reflections("net.amarantha.gpiomofo.scenario");
        Set<Class<? extends Scenario>> allClasses = reflections.getSubTypesOf(Scenario.class);
        if ( allClasses.isEmpty() ) {
            System.out.println("\nNo Scenarios found");
        } else {
            System.out.println("\nAvailable Scenarios:");
            allClasses.forEach((v)-> System.out.println(" " + v.getSimpleName()));
            System.out.println("\nUse: gpiomofo.sh -scenario=<name>\n");
        }
    }

    @Override
    protected void configure() {
        bind(Scenario.class).to(scenarioClass).in(Scopes.SINGLETON);
        bind(MidiService.class).to(MidiServiceImpl.class).in(Scopes.SINGLETON);
        bind(HttpService.class).to(HttpServiceImpl.class).in(Scopes.SINGLETON);
        bind(OscService.class).to(OscServiceImpl.class).in(Scopes.SINGLETON);
        configureAdditional();
    }

    protected void configureAdditional() {
        bind(GpioService.class).to(RaspberryPi3.class).in(Scopes.SINGLETON);
        bind(NeoPixel.class).to(NeoPixelWS281X.class).in(Scopes.SINGLETON);
        bindConstant().annotatedWith(TapeRefresh.class).to(pixelTapeRefresh);
    }

}
