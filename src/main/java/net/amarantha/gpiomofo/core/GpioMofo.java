package net.amarantha.gpiomofo.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.factory.ScenarioBuilder;
import net.amarantha.gpiomofo.scenario.Scenario;

import java.util.Scanner;

import static net.amarantha.utils.shell.Utility.log;

@Singleton
public class GpioMofo {

    @Inject private ScenarioBuilder builder;

    private Scenario scenario;

    private boolean simulation = false;

    public void startSimulation() {
        simulation = true;
        start();
    }

    public void start() {
        scenario = builder.loadScenario();
        scenario.start();
        if ( !simulation ) {
            waitForEnter();
        }
    }

    private void waitForEnter() {
        log(true, " (Press ENTER to quit) ", true);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {}
        stop();
    }

    public void stop() {
        scenario.stop();
        System.exit(0);
    }

}
