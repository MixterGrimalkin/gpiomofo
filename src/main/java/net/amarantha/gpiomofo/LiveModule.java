package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.config.Config;
import net.amarantha.gpiomofo.config.TestConfig;
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
        bind(Config.class).to(TestConfig.class).in(Scopes.SINGLETON);
    }

}
