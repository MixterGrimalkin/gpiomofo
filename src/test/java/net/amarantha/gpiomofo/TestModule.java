package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.gpio.GpioService;
import net.amarantha.gpiomofo.gpio.GpioServiceMock;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GpioService.class).to(GpioServiceMock.class).in(Scopes.SINGLETON);
    }

}
