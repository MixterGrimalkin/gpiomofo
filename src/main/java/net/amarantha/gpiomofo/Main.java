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

        out.println(BAR+"\n"+LOGO);

        scenario.load();

        out.println(" STARTING UP... \n" + BAR);

        startServices();

        out.println(BAR + "\n GpioMofo is Active \n" + BAR);

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
        if ( props.isWithServer() ) {
            web.start();
        }

    }

    private void waitForEnter() {
        out.println(" (Press ENTER to quit)\n" + BAR);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {}
        stop();
    }

    public void stop() {

        scenario.stop();

        out.println(BAR + "\n SHUTTING DOWN...\n" + BAR);

        stopServices();

        out.println(BAR+"\n Bye for now! \n"+BAR);

        System.exit(0);

    }

    private void stopServices() {

        if ( props.isWithServer() ) {
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

    public static final String LOGO =
            "    ________       .__          _____          _____       \n" +
            "   /  _____/______ |__| ____   /     \\   _____/ ____\\____  \n" +
            "  /   \\  ___\\____ \\|  |/  _ \\ /  \\ /  \\ /  _ \\   __\\/  _ \\ \n" +
            "  \\    \\_\\  \\  |_> >  (  <_> )    Y    (  <_> )  | (  <_> )\n" +
            "   \\______  /   __/|__|\\____/\\____|__  /\\____/|__|  \\____/ \n" +
            "          \\/|__|                     \\/                    ";

    public static final String BAR =
            "-------------------------------------------------------------";

    public static void main(String[] args) {
        processArgs(args);
        Guice.createInjector(new LiveModule())
            .getInstance(Main.class)
                .start();
    }

}
