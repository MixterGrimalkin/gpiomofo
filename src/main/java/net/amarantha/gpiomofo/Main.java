package net.amarantha.gpiomofo;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Singleton;
import javafx.application.Application;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.module.LiveModule;
import net.amarantha.gpiomofo.module.SimulationModule;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.utility.PropertyManager;
import net.amarantha.gpiomofo.webservice.WebService;

import java.util.Scanner;

import static net.amarantha.gpiomofo.scenario.Scenario.BAR;
import static net.amarantha.gpiomofo.utility.PropertyManager.isSimulationMode;
import static net.amarantha.gpiomofo.utility.PropertyManager.processArgs;

@Singleton
public class Main extends Application {

    public static void main(String[] args) {
        processArgs(args);
        if ( isSimulationMode() ) {
            launch(args);
        } else {
            Guice.createInjector(new LiveModule())
                .getInstance(Main.class)
                    .start();
        }
    }

    @Inject private Scenario scenario;

    @Inject private WebService webService;
    @Inject private GpioService gpio;
    @Inject private MidiService midi;
    @Inject private TaskService tasks;
    @Inject private PropertyManager props;

    public void start() {

        System.out.println(BAR+"\n Starting GpioMoFo...");

        scenario.setup();

        gpio.startInputMonitor();
        midi.openDevice();
        tasks.start();
        if ( props.isWithServer() ) {
            webService.start();
        }

        if ( !isSimulationMode() ) {
            System.out.println(BAR + "\n System Active (Press ENTER to quit)\n" + BAR + "\n");
            Scanner scanner = new Scanner(System.in);
            while (!scanner.hasNextLine()) {}
            stop();
        }

    }

    public void stop() {
        System.out.println("Shutting Down...");
        tasks.stop();
        scenario.stop();
        gpio.shutdown();
        midi.closeDevice();
        if ( props.isWithServer() ) {
            webService.stop();
        }

        System.out.println("\n"+BAR+"\n Bye for now! \n"+BAR);
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Guice.createInjector(new SimulationModule(primaryStage))
            .getInstance(Main.class)
                .start();
    }
}
