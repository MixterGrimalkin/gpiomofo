package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.gpio.GpioService;
import net.amarantha.gpiomofo.gpio.GpioServiceMock;
import net.amarantha.gpiomofo.midi.MidiService;
import net.amarantha.gpiomofo.midi.MidiServiceMock;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GpioService.class).to(GpioServiceMock.class).in(Scopes.SINGLETON);
        bind(MidiService.class).to(MidiServiceMock.class).in(Scopes.SINGLETON);
    }

}
