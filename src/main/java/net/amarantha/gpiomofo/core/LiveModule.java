package net.amarantha.gpiomofo.core;

import com.google.inject.AbstractModule;
import net.amarantha.gpiomofo.display.DisplayLive;
import net.amarantha.gpiomofo.service.ServicesModule;
import net.amarantha.utils.UtilityModule;

public class LiveModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new UtilityModule());
        install(new ServicesModule());
        install(new DisplayLive());
    }

}
