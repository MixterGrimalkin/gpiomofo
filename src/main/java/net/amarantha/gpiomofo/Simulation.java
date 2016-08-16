package net.amarantha.gpiomofo;

import com.google.inject.Guice;
import javafx.application.Application;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.module.SimulationModule;

import static net.amarantha.gpiomofo.utility.PropertyManager.processArgs;

public class Simulation extends Application {

    public static void main(String[] args) {
        processArgs(args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Guice.createInjector(new SimulationModule(primaryStage))
            .getInstance(Main.class)
                .start();
    }

}
