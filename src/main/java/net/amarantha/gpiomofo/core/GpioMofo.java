package net.amarantha.gpiomofo.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.factory.ScenarioBuilder;
import net.amarantha.gpiomofo.scenario.Scenario;

import java.util.Scanner;

import static net.amarantha.utils.shell.Utility.log;

@Singleton
public class GpioMofo {

    public static GpioMofo build(AbstractModule module) {
        return Guice.createInjector(module).getInstance(GpioMofo.class);
    }

    @Inject private ScenarioBuilder builder;

    private Scenario scenario;

    public void start() {
        scenario = builder.loadScenario();
        scenario.start();
    }

    public void stop() {
        scenario.stop();
    }

    public Scenario getScenario() {
        return scenario;
    }
}
