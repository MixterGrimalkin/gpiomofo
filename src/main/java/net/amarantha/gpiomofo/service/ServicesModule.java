package net.amarantha.gpiomofo.service;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.service.http.HttpService;
import net.amarantha.gpiomofo.service.http.HttpServiceImpl;
import net.amarantha.gpiomofo.service.http.HttpServiceMock;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.midi.MidiServiceImpl;
import net.amarantha.gpiomofo.service.midi.MidiServiceMock;
import net.amarantha.gpiomofo.service.osc.OscService;
import net.amarantha.gpiomofo.service.osc.OscServiceImpl;
import net.amarantha.gpiomofo.service.osc.OscServiceMock;

public class ServicesModule extends AbstractModule {

    private final boolean live;

    public ServicesModule() {
        this(true);
    }

    public ServicesModule(boolean live) {
        this.live = live;
    }

    @Override
    protected void configure() {
        if ( live ) {
            bind(MidiService.class).to(MidiServiceImpl.class).in(Scopes.SINGLETON);
            bind(HttpService.class).to(HttpServiceImpl.class).in(Scopes.SINGLETON);
            bind(OscService.class).to(OscServiceImpl.class).in(Scopes.SINGLETON);
        } else {
            bind(MidiService.class).to(MidiServiceMock.class).in(Scopes.SINGLETON);
            bind(HttpService.class).to(HttpServiceMock.class).in(Scopes.SINGLETON);
            bind(OscService.class).to(OscServiceMock.class).in(Scopes.SINGLETON);
        }
    }

}
