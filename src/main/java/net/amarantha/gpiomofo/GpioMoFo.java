package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.config.Config;
import net.amarantha.gpiomofo.gpio.GpioService;
import net.amarantha.gpiomofo.midi.MidiService;
import net.amarantha.gpiomofo.webservice.WebService;

import java.util.Scanner;

import static net.amarantha.gpiomofo.config.Config.BAR;

@Singleton
public class GpioMoFo {

    @Inject private Config config;

    @Inject private WebService webService;
    @Inject private GpioService gpio;
    @Inject private MidiService midi;

    public void start() {

        System.out.println(BAR+"\n Starting GpioMoFo...");

        config.setup();

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
