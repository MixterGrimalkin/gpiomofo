package net.amarantha.gpiomofo.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.core.scenario.ScenarioBuilder;

import java.util.Scanner;

import static net.amarantha.gpiomofo.service.shell.Utility.log;

@Singleton
public class GpioMofo {

    @Inject private ScenarioBuilder builder;

    private boolean simulation = false;

    public void startSimulation() {
        simulation = true;
        start();
    }

    public void start() {
        builder.loadFromProperties().getScenario().start();
        if ( !simulation ) {
            waitForEnter();
        }
    }

    private void waitForEnter() {
        log(true, " (Press ENTER to quit)", true);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {}
        stop();
    }

    public void stop() {
        builder.getScenario().stop();
        System.exit(0);
    }

}
