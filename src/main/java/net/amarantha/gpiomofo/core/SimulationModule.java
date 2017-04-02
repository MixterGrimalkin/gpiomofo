package net.amarantha.gpiomofo.core;

import com.google.inject.AbstractModule;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.display.DisplaySimulation;
import net.amarantha.utils.UtilityModule;

public class SimulationModule extends AbstractModule {

    private Stage stage;

    public SimulationModule(Stage stage) {
        super();
        this.stage = stage;
    }

    @Override
    protected void configure() {
        install(new UtilityModule());
        install(new DisplaySimulation(stage));
    }
}
