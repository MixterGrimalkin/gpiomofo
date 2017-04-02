package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.core.scenario.ScenarioBuilder;

import java.util.Scanner;

import static net.amarantha.gpiomofo.service.shell.Utility.log;

@Singleton
public class GpioMofo {

    @Inject private ScenarioBuilder builder;

    void start() {
        builder.loadFromProperties().getScenario().start();
        if ( !simulation ) {
            waitForEnter();
        }
    }

    void stop() {
        builder.getScenario().stop();
        System.exit(0);
    }

    private void waitForEnter() {
        log(true, " (Press ENTER to quit)", true);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {}
        stop();
    }

    void startSimulation() {
        simulation = true;
        start();
    }

    private boolean simulation = false;

}
