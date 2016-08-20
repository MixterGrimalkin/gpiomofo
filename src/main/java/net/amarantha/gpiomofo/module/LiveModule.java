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

public class LiveModule extends AbstractModule {

    private Class<? extends Scenario> scenarioClass;

    private int pixelTapeRefresh;

    public LiveModule() {
        PropertyManager props = new PropertyManager();
        String className = props.getString("Scenario", "TestScenario");
        pixelTapeRefresh = props.getInt("PixelTapeRefresh", 50);
        try {
            scenarioClass = (Class<? extends Scenario>) Class.forName("net.amarantha.gpiomofo.scenario."+className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
