package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.core.factory.ScenarioBuilder;
import net.amarantha.gpiomofo.service.pixeltape.PixelTapeService;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.touch.MPR121;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.properties.PropertiesService;

import java.util.Scanner;

import static net.amarantha.gpiomofo.Main.WITH_SERVER;
import static net.amarantha.gpiomofo.service.shell.Utility.log;

@Singleton
public class GpioMofo {

    @Inject private GpioService gpio;
    @Inject private MPR121 mpr121;
    @Inject private MidiService midi;
    @Inject private PixelTapeService pixel;
    @Inject private WebService web;
    @Inject private TaskService tasks;

    @Inject private PropertiesService props;

    @Inject private ScenarioBuilder scenarioBuilder;

    private Scenario scenario;

    public void startApplication() {

        scenario = scenarioBuilder.load().get();

        log(" STARTING UP... ", true);

        startServices();

        log(true, " GpioMofo is Active ", true);

        scenario.start();

        if ( !simulation ) {
            waitForEnter();
        }

    }

    private void startServices() {

        if ( scenario.requiresGpio() ) {
            gpio.start();
        }
        if ( scenario.requiresMpr() ) {
            mpr121.start();
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
        if ( scenario.requiresMpr() ) {
            mpr121.stop();
        }
        if ( scenario.requiresMidi() ) {
            midi.stop();
        }
        if ( scenario.requiresPixelTape() ) {
            pixel.stop();
        }

    }

    private boolean simulation = false;

    public GpioMofo inSimulation() {
        simulation = true;
        return this;
    }

}
