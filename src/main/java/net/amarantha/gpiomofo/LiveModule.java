package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.gpio.GpioProvider;
import net.amarantha.gpiomofo.gpio.GpioProviderImpl;

public class LiveModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GpioProvider.class).to(GpioProviderImpl.class).in(Scopes.SINGLETON);
    }

}
