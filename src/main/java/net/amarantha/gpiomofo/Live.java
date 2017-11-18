package net.amarantha.gpiomofo;

import net.amarantha.gpiomofo.core.GpioMofo;
import net.amarantha.gpiomofo.core.LiveModule;

import java.util.Scanner;

import static net.amarantha.gpiomofo.core.Constants.HELP_TEXT;
import static net.amarantha.gpiomofo.core.Constants.LOGO;
import static net.amarantha.utils.properties.PropertiesService.processArgs;
import static net.amarantha.utils.shell.Utility.log;

public class Live {

    public static void main(String[] args) {
        log(LOGO);
        processArgs(args, HELP_TEXT);

        GpioMofo mofo = GpioMofo.build(new LiveModule());
        mofo.start();

        log(true, " (Press ENTER to quit) ", true);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine());

        mofo.stop();
        System.exit(0);
    }

}
