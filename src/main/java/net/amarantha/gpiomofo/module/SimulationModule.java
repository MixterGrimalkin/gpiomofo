package net.amarantha.gpiomofo.module;

import com.google.inject.Scopes;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.pixeltape.NeoPixelGUI;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceMock;

public class SimulationModule extends LiveModule {

    private Stage stage;

    public SimulationModule(Stage stage) {
        super();
        this.stage = stage;
    }

    @Override
    protected void configureAdditional() {
        bind(GpioService.class).to(GpioServiceMock.class).in(Scopes.SINGLETON);
        bind(NeoPixel.class).to(NeoPixelGUI.class).in(Scopes.SINGLETON);
        bindConstant().annotatedWith(TapeRefresh.class).to(13);
        bind(Stage.class).toInstance(stage);
    }
}
