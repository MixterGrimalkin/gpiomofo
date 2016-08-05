package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceMock;
import net.amarantha.gpiomofo.service.http.HttpService;
import net.amarantha.gpiomofo.service.http.HttpServiceMock;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.midi.MidiServiceMock;
import net.amarantha.gpiomofo.service.osc.OscService;
import net.amarantha.gpiomofo.service.osc.OscServiceMock;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GpioService.class).to(GpioServiceMock.class).in(Scopes.SINGLETON);
        bind(MidiService.class).to(MidiServiceMock.class).in(Scopes.SINGLETON);
        bind(HttpService.class).to(HttpServiceMock.class).in(Scopes.SINGLETON);
        bind(OscService.class).to(OscServiceMock.class).in(Scopes.SINGLETON);
    }

}
