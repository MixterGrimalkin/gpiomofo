package net.amarantha.gpiomofo.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.pixeltape.PixelTape;
import net.amarantha.gpiomofo.pixeltape.PixelTapeMock;
import net.amarantha.gpiomofo.pixeltape.PixelTapeWS281x;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceMock;
import net.amarantha.gpiomofo.service.gpio.RaspberryPi3;
import net.amarantha.gpiomofo.service.http.HttpService;
import net.amarantha.gpiomofo.service.http.HttpServiceImpl;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.midi.MidiServiceImpl;
import net.amarantha.gpiomofo.service.osc.OscService;
import net.amarantha.gpiomofo.service.osc.OscServiceImpl;
import net.amarantha.gpiomofo.utility.PropertyManager;

public class SimulationModule extends LiveModule {

    private Class<? extends Scenario> scenarioClass;
    private Stage stage;

    public SimulationModule(Stage stage) {
        super();
        this.stage = stage;
    }

    @Override
    protected void configureAdditional() {
        bind(GpioService.class).to(GpioServiceMock.class).in(Scopes.SINGLETON);
        bind(PixelTape.class).to(PixelTapeMock.class).in(Scopes.SINGLETON);
        bindConstant().annotatedWith(TapeRefresh.class).to(100);
        bind(Stage.class).toInstance(stage);
    }
}
