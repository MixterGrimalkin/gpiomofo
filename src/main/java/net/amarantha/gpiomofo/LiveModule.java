package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.scenario.TestScenario;
import net.amarantha.gpiomofo.gpio.GpioService;
import net.amarantha.gpiomofo.gpio.RaspberryPi3;
import net.amarantha.gpiomofo.http.HttpService;
import net.amarantha.gpiomofo.http.HttpServiceImpl;
import net.amarantha.gpiomofo.midi.MidiService;
import net.amarantha.gpiomofo.midi.MidiServiceImpl;

public class LiveModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GpioService.class).to(RaspberryPi3.class).in(Scopes.SINGLETON);
        bind(MidiService.class).to(MidiServiceImpl.class).in(Scopes.SINGLETON);
        bind(HttpService.class).to(HttpServiceImpl.class).in(Scopes.SINGLETON);
        bind(Scenario.class).to(TestScenario.class).in(Scopes.SINGLETON);
    }

}
