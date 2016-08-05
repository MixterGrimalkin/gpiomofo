package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.webservice.WebService;

import java.util.Scanner;

import static net.amarantha.gpiomofo.scenario.Scenario.BAR;

@Singleton
public class GpioMoFo {

    @Inject private Scenario scenario;

    @Inject private WebService webService;
    @Inject private GpioService gpio;
    @Inject private MidiService midi;

    public void start() {

        System.out.println(BAR+"\n Starting GpioMoFo...");

        scenario.setup();

        webService.start();
        gpio.startInputMonitor();
        midi.openDevice();

        System.out.println(BAR+"\n System Active (Press ENTER to quit)\n"+BAR+"\n");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {}

        webService.stop();
        gpio.shutdown();
        midi.closeDevice();

        System.out.println("\n"+BAR+"\n Bye for now! \n"+BAR);
        System.exit(0);

    }

}
