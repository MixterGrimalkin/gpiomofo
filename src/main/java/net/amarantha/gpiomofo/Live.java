package net.amarantha.gpiomofo;

import net.amarantha.gpiomofo.core.GpioMofo;
import net.amarantha.gpiomofo.core.LiveModule;

import static com.google.inject.Guice.createInjector;
import static net.amarantha.gpiomofo.core.Constants.HELP_TEXT;
import static net.amarantha.gpiomofo.core.Constants.LOGO;
import static net.amarantha.utils.properties.PropertiesService.processArgs;
import static net.amarantha.utils.shell.Utility.log;

public class Live {

    public static void main(String[] args) {
        log(LOGO);
        processArgs(args, HELP_TEXT);
        createInjector(new LiveModule())
            .getInstance(GpioMofo.class)
                .start();
    }

}
