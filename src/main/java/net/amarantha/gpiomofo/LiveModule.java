package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.gpio.GpioProvider;
import net.amarantha.gpiomofo.gpio.RaspberryPi3;

public class LiveModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GpioProvider.class).to(RaspberryPi3.class).in(Scopes.SINGLETON);
    }

}
