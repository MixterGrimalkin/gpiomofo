package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.pixeltape.PixelTape;
import net.amarantha.gpiomofo.pixeltape.PixelTapeMock;
import net.amarantha.gpiomofo.pixeltape.PixelTapeWS281x;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceMock;
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

    public LiveModule() {
        PropertyManager props = new PropertyManager();
        String className = props.getString("Scenario", "TestScenario");
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

//        bind(GpioService.class).to(GpioServiceMock.class).in(Scopes.SINGLETON);
        bind(GpioService.class).to(RaspberryPi3.class).in(Scopes.SINGLETON);

//        bind(PixelTape.class).to(PixelTapeMock.class).in(Scopes.SINGLETON);
        bind(PixelTape.class).to(PixelTapeWS281x.class).in(Scopes.SINGLETON);
    }

}
