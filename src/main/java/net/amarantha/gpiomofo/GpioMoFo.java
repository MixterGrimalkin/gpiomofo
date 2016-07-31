package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.gpio.GpioProvider;

import java.util.Scanner;

@Singleton
public class GpioMoFo {

    @Inject private GpioProvider gpio;

    public void start() {

        System.out.println("Starting GpioMoFo...");

        gpio.onInputChange(1, (s)->gpio.digitalOutput(0, s));
        gpio.whileInputHigh(1, ()-> System.out.println("HIGH AS A BIRD!"));
        gpio.whileInputLow(1, ()-> System.out.println("^W^"));

        gpio.startInputMonitor();

        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {}

        gpio.stopInputMonitor();

        System.out.println("Goodbye");
    }

}
