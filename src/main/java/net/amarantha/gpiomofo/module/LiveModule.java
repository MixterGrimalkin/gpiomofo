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
import net.amarantha.utils.UtilityModule;
import net.amarantha.utils.properties.PropertiesService;
import org.reflections.Reflections;

import java.io.PrintStream;
import java.util.Set;

import static net.amarantha.gpiomofo.Main.LIST_SCENARIOS;

public class LiveModule extends AbstractModule {

    private PropertiesService props;

    public LiveModule() {
        props = new PropertiesService();
        if ( props.isArgumentPresent(LIST_SCENARIOS) ) {
            listScenarios();
            System.exit(0);
        }
    }

    @Override
    protected void configure() {
        install(new UtilityModule());
        bind(PropertiesService.class).toInstance(props);
        bind(MidiService.class).to(MidiServiceImpl.class).in(Scopes.SINGLETON);
        bind(HttpService.class).to(HttpServiceImpl.class).in(Scopes.SINGLETON);
        bind(OscService.class).to(OscServiceImpl.class).in(Scopes.SINGLETON);
        bind(PrintStream.class).toInstance(System.out);
        configureAdditional();
    }

    protected void configureAdditional() {
        bind(GpioService.class).to(RaspberryPi3.class).in(Scopes.SINGLETON);
        bind(NeoPixel.class).to(NeoPixelWS281X.class).in(Scopes.SINGLETON);
        bindConstant().annotatedWith(TapeRefresh.class).to(props.getInt("PixelTapeRefresh", 50));
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

}
