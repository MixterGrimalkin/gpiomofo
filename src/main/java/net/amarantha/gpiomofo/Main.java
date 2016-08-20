package net.amarantha.gpiomofo;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.module.LiveModule;
import net.amarantha.gpiomofo.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.utility.PropertyManager;
import net.amarantha.gpiomofo.webservice.WebService;

import java.util.Scanner;

import static net.amarantha.gpiomofo.utility.PropertyManager.isSimulationMode;
import static net.amarantha.gpiomofo.utility.PropertyManager.processArgs;

@Singleton
public class Main {

    public static void main(String[] args) {
        processArgs(args);
        Guice.createInjector(new LiveModule())
            .getInstance(Main.class)
                .start();
    }

    @Inject private Scenario scenario;

    @Inject private WebService webService;
    @Inject private GpioService gpio;
    @Inject private MidiService midi;
    @Inject private TaskService tasks;
    @Inject private PropertyManager props;
    @Inject private NeoPixel neoPixel;

    public static final String LOGO =
        "    ________       .__          _____          _____       \n" +
        "   /  _____/______ |__| ____   /     \\   _____/ ____\\____  \n" +
        "  /   \\  ___\\____ \\|  |/  _ \\ /  \\ /  \\ /  _ \\   __\\/  _ \\ \n" +
        "  \\    \\_\\  \\  |_> >  (  <_> )    Y    (  <_> )  | (  <_> )\n" +
        "   \\______  /   __/|__|\\____/\\____|__  /\\____/|__|  \\____/ \n" +
        "          \\/|__|                     \\/                    ";

    public static final String BAR =
        "-------------------------------------------------------------";

    public void start() {

        System.out.println(BAR+"\n"+LOGO);


        scenario.setup();

        System.out.println(" STARTING UP... \n" + BAR);

        gpio.startInputMonitor();
        midi.openDevice();
        tasks.start();
        if ( props.isWithServer() ) {
            webService.start();
        }

        System.out.println(BAR + "\n GpioMofo is Active \n" + BAR);

        if ( !isSimulationMode() ) {
            System.out.println(" (Press ENTER to quit)\n" + BAR);
            Scanner scanner = new Scanner(System.in);
            while (!scanner.hasNextLine()) {}
            stop();
        }

    }

    public void stop() {
        System.out.println(BAR + "\n SHUTTING DOWN...\n" + BAR);
        tasks.stop();
        neoPixel.close();
        scenario.stop();
        gpio.shutdown();
        midi.closeDevice();
        if ( props.isWithServer() ) {
            webService.stop();
        }

        System.out.println(BAR+"\n Bye for now! \n"+BAR);
        System.exit(0);
    }

}
