package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.gpio.GpioProvider;
import net.amarantha.gpiomofo.gpio.GpioProviderMock;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GpioProvider.class).to(GpioProviderMock.class).in(Scopes.SINGLETON);
    }

}
