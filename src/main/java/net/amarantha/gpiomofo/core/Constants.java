package net.amarantha.gpiomofo.core;

public class Constants {

    public static final int X = 0;
    public static final int Y = 1;

    // Command line arguments
    public static final String SCENARIO =       "scenario";
    public static final String LIST_SCENARIOS = "list";
    public static final String WITH_SERVER =    "http";
    public static final String LOG_HTTP =       "loghttp";
    public static final String LOCAL_IP =       "local";

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
            "Set additional configuration options in: config/application.properties" +
            "\n"
            ;

}
