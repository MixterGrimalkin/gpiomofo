package net.amarantha.gpiomofo;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.module.LiveModule;
import net.amarantha.gpiomofo.pixeltape.PixelTapeService;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.utility.GpioMofoProperties;
import net.amarantha.gpiomofo.webservice.WebService;

import java.util.Scanner;

import static net.amarantha.gpiomofo.utility.PropertyManager.processArgs;
import static net.amarantha.gpiomofo.utility.PropertyManager.setHelpText;
import static net.amarantha.gpiomofo.utility.Utility.log;

@Singleton
public class Main {

    @Inject private GpioService gpio;
    @Inject private MidiService midi;
    @Inject private PixelTapeService pixel;
    @Inject private WebService web;
    @Inject private TaskService tasks;

    @Inject private GpioMofoProperties props;

    @Inject private Scenario scenario;

    public void startApplication() {

        scenario.load();

        log(" STARTING UP... ", true);

        startServices();

        log(true, " GpioMofo is Active ", true);

        scenario.start();

        if ( !scenario.requiresGUI() ) {
            waitForEnter();
        }

    }

    private void startServices() {

        if ( scenario.requiresGpio() ) {
            gpio.start();
        }
        if ( scenario.requiresMidi() ) {
            midi.start();
        }
        if ( scenario.requiresPixelTape() ) {
            pixel.start();
        }
        tasks.start();
        if ( props.isArgumentPresent(WITH_SERVER) ) {
            web.start();
        }

    }

    private void waitForEnter() {
        log(true, " (Press ENTER to quit)", true);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {}
        stopApplication();
    }

    public void stopApplication() {

        scenario.stop();

        log(true, " SHUTTING DOWN...", true);

        stopServices();

        log(true, " Bye for now! ", true);

        System.exit(0);

    }

    private void stopServices() {

        if ( props.isArgumentPresent(WITH_SERVER) ) {
            web.stop();
        }
        tasks.stop();
        if ( scenario.requiresGpio() ) {
            gpio.stop();
        }
        if ( scenario.requiresMidi() ) {
            midi.stop();
        }
        if ( scenario.requiresPixelTape() ) {
            pixel.stop();
        }

    }

    /////////////
    // Startup //
    /////////////

    public static void main(String[] args) {
        log(LOGO);
        setHelpText(HELP_TEXT);
        processArgs(args);

        Guice.createInjector(new LiveModule())
            .getInstance(Main.class)
                .startApplication();
    }

    public static final String LOGO =
        "\n    ________       .__          _____          _____       \n" +
        "   /  _____/______ |__| ____   /     \\   _____/ ____\\____  \n" +
        "  /   \\  ___\\____ \\|  |/  _ \\ /  \\ /  \\ /  _ \\   __\\/  _ \\ \n" +
        "  \\    \\_\\  \\  |_> >  (  <_> )    Y    (  <_> )  | (  <_> )\n" +
        "   \\______  /   __/|__|\\____/\\____|__  /\\____/|__|  \\____/ \n" +
        "          \\/|__|                     \\/                    \n"
    ;

    public static final String HELP_TEXT =
        "GpioMofo\n" +
        "    A connectivity system for the Raspberry Pi\n" +
        "\n" +
        "Usage:\n" +
        "    gpiomofo.sh <options>\n" +
        "\n" +
        "Options:\n" +
        "    -scenario=<name>   : Load specified Scenario\n" +
        "    -list              : List available Scenarios and exit\n" +
        "    -http              : Enable HTTP triggers\n" +
        "    -loghttp           : Log incoming HTTP requests\n" +
        "    -local             : Serve on 127.0.0.1\n" +
        "    -help | -h         : Display this message and exit\n" +
        "\n" +
        "Set additional configuration options in: application.properties" +
        "\n"
    ;

    public static final String SCENARIO =       "scenario";
    public static final String LIST_SCENARIOS = "list";
    public static final String WITH_SERVER =    "http";
    public static final String LOG_HTTP =       "loghttp";
    public static final String LOCAL_IP =       "local";

}
