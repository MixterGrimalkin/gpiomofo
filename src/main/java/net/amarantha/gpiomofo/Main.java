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

import static java.lang.System.out;
import static net.amarantha.gpiomofo.utility.PropertyManager.processArgs;
import static net.amarantha.gpiomofo.utility.PropertyManager.setHelpText;

@Singleton
public class Main {

    @Inject private GpioService gpio;
    @Inject private MidiService midi;
    @Inject private PixelTapeService pixel;
    @Inject private WebService web;
    @Inject private TaskService tasks;

    @Inject private GpioMofoProperties props;

    @Inject private Scenario scenario;

    public void start() {

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
        stop();
    }

    public void stop() {

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

    public static final String LOGO =
            "\n    ________       .__          _____          _____       \n" +
            "   /  _____/______ |__| ____   /     \\   _____/ ____\\____  \n" +
            "  /   \\  ___\\____ \\|  |/  _ \\ /  \\ /  \\ /  _ \\   __\\/  _ \\ \n" +
            "  \\    \\_\\  \\  |_> >  (  <_> )    Y    (  <_> )  | (  <_> )\n" +
            "   \\______  /   __/|__|\\____/\\____|__  /\\____/|__|  \\____/ \n" +
            "          \\/|__|                     \\/                    \n";

    public static final String BAR =
            "-------------------------------------------------------------";

    public static final String HELP_TEXT =
        "GpioMofo\n" +
        "  Multiple-protocol linking system for Raspberry Pi\n" +
        "Usage:\n" +
        "  gpiomofo.sh [OPTIONS]\n" +
        "    -list       : List available Scenarios and exit\n" +
        "    -scenario=S : Start Scenario S, otherwise use value from properties\n" +
        "    -withserver : Start HTTP server\n" +
        "    -local      : Serve on 127.0.0.1\n" +
        "    -loghttp    : Log incoming HTTP requests\n" +
        "\n"
    ;

    public static final String SCENARIO = "scenario";
    public static final String WITH_SERVER = "withserver";
    public static final String LOCAL_IP = "local";
    public static final String LOG_HTTP = "loghttp";
    public static final String LIST_SCENARIOS = "list";

    public static void main(String[] args) {
        log(LOGO);
        setHelpText(HELP_TEXT);
        processArgs(args);
        Guice.createInjector(new LiveModule())
            .getInstance(Main.class)
                .start();
    }

    ////////////////////
    // Simple Logging //
    ////////////////////

    public static void log(boolean bar) {
        if ( bar ) {
            out.println(BAR);
        }
    }

    public static void log(String message) {
        log(false, message, false);
    }

    public static void log(boolean barBefore, String message) {
        log(barBefore, message, false);
    }

    public static void log(String message, boolean barAfter) {
        log(false, message, barAfter);
    }

    public static void log(boolean barBefore, String message, boolean barAfter) {
        out.println((barBefore?BAR+"\n":"")+message+(barAfter?"\n"+BAR:""));
    }

}
