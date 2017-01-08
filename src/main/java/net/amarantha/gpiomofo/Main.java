package net.amarantha.gpiomofo;

import com.google.inject.Guice;
import net.amarantha.gpiomofo.module.LiveModule;

import static net.amarantha.gpiomofo.utility.Utility.log;
import static net.amarantha.utils.properties.PropertiesService.processArgs;

public class Main {

    public static final String LOGO =
            "\n    ________       .__          _____          _____       \n" +
            "   /  _____/______ |__| ____   /     \\   _____/ ____\\____  \n" +
            "  /   \\  ___\\____ \\|  |/  _ \\ /  \\ /  \\ /  _ \\   __\\/  _ \\ \n" +
            "  \\    \\_\\  \\  |_> >  (  <_> )    Y    (  <_> )  | (  <_> )\n" +
            "   \\______  /   __/|__|\\____/\\____|__  /\\____/|__|  \\____/ \n" +
            "          \\/|__|                     \\/                    \n"
    ;

    public static final String HELP_TEXT =
            "GpioMofo\n" +
            "    A connectivity system for the Raspberry Pi\n" +
            "\n" +
            "Usage:\n" +
            "    gpiomofo.sh <options>\n" +
            "\n" +
            "Options:\n" +
            "    -scenario=<name>   : Load specified Scenario\n" +
            "    -list              : List available Scenarios and exit\n" +
            "    -http              : Enable HTTP triggers\n" +
            "    -loghttp           : Log incoming HTTP requests\n" +
            "    -local             : Serve on 127.0.0.1\n" +
            "    -help | -h         : Display this message and exit\n" +
            "\n" +
            "Set additional configuration options in: application.properties" +
            "\n"
    ;

    public static final String SCENARIO =       "scenario";
    public static final String LIST_SCENARIOS = "list";
    public static final String WITH_SERVER =    "http";
    public static final String LOG_HTTP =       "loghttp";
    public static final String LOCAL_IP =       "local";

    public static void main(String[] args) {
        log(LOGO);
        processArgs(args, HELP_TEXT);

        Guice.createInjector(new LiveModule())
            .getInstance(GpioMofo.class)
                .startApplication();
    }

}
