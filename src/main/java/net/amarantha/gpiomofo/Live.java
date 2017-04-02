package net.amarantha.gpiomofo;

import com.google.inject.Guice;
import net.amarantha.gpiomofo.core.GpioMofo;
import net.amarantha.gpiomofo.core.LiveModule;

import static net.amarantha.gpiomofo.core.Constants.HELP_TEXT;
import static net.amarantha.gpiomofo.core.Constants.LOGO;
import static net.amarantha.gpiomofo.service.shell.Utility.log;
import static net.amarantha.utils.properties.PropertiesService.processArgs;

public class Live {

    public static void main(String[] args) {
        log(LOGO);
        processArgs(args, HELP_TEXT);
        Guice.createInjector(new LiveModule())
            .getInstance(GpioMofo.class).start();
    }

}
