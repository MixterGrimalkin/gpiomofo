package net.amarantha.gpiomofo;

import com.google.inject.Guice;
import javafx.application.Application;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.module.SimulationModule;

import static net.amarantha.gpiomofo.Main.*;
import static net.amarantha.gpiomofo.utility.PropertyManager.processArgs;
import static net.amarantha.gpiomofo.utility.PropertyManager.setHelpText;

public class Simulation extends Application {

    public static void main(String[] args) {
        log(LOGO);
        setHelpText(HELP_TEXT);
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
