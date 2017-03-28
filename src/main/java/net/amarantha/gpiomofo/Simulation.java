package net.amarantha.gpiomofo;

import com.google.inject.Guice;
import javafx.application.Application;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.core.GpioMofoSimulationModule;

import static net.amarantha.gpiomofo.Main.HELP_TEXT;
import static net.amarantha.gpiomofo.Main.LOGO;
import static net.amarantha.gpiomofo.service.shell.Utility.log;
import static net.amarantha.utils.properties.PropertiesService.processArgs;

public class Simulation extends Application {

    public static void main(String[] args) {
        log(LOGO);
        processArgs(args, HELP_TEXT);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Guice.createInjector(new GpioMofoSimulationModule(primaryStage))
            .getInstance(GpioMofo.class)
                .inSimulation().startApplication();
    }

}
