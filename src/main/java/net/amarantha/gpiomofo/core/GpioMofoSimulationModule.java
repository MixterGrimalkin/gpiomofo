package net.amarantha.gpiomofo.core;

import com.google.inject.Scopes;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.core.annotation.TapeRefresh;
import net.amarantha.gpiomofo.service.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.pixeltape.NeoPixelGUI;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceGUI;

import java.io.PrintStream;

public class GpioMofoSimulationModule extends GpioMofoModule {

    private Stage stage;

    public GpioMofoSimulationModule(Stage stage) {
        super();
        this.stage = stage;
    }

    @Override
    protected void configureAdditional() {
        bind(GpioService.class).to(GpioServiceGUI.class).in(Scopes.SINGLETON);
        bind(NeoPixel.class).to(NeoPixelGUI.class).in(Scopes.SINGLETON);
        bindConstant().annotatedWith(TapeRefresh.class).to(13);
        bind(PrintStream.class).toInstance(System.out);
        bind(Stage.class).toInstance(stage);
    }
}
