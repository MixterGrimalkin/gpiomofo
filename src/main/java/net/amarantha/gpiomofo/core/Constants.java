package net.amarantha.gpiomofo.core;

public class Constants {

    public static final int X = 0;
    public static final int Y = 1;

    public static int[] neoPixelGUIWidths = {};

    public static final String _RESET = "\u001B[0m";
    public static final String _BLACK = "\u001B[30m";
    public static final String _RED = "\u001B[31m";
    public static final String _GREEN = "\u001B[32m";
    public static final String _YELLOW = "\u001B[33m";
    public static final String _BLUE = "\u001B[34m";
    public static final String _PURPLE = "\u001B[35m";
    public static final String _CYAN = "\u001B[36m";
    public static final String _WHITE = "\u001B[37m";

    public static final String _BLACK_BG = "\u001B[40m";
    public static final String _RED_BG = "\u001B[41m";
    public static final String _GREEN_BG = "\u001B[42m";
    public static final String _YELLOW_BG = "\u001B[43m";
    public static final String _BLUE_BG = "\u001B[44m";
    public static final String _PURPLE_BG = "\u001B[45m";
    public static final String _CYAN_BG = "\u001B[46m";
    public static final String _WHITE_BG = "\u001B[47m";

    public static final String _BOLD = "\u001B[1m";
    public static final String	_LOW_INTENSITY		= "\u001B[2m";

    public static final String	_ITALIC				= "\u001B[3m";
    public static final String	_UNDERLINE			= "\u001B[4m";
    public static final String	_BLINK				= "\u001B[5m";
    public static final String	_RAPID_BLINK			= "\u001B[6m";
    public static final String	_REVERSE_VIDEO		= "\u001B[7m";
    public static final String	_INVISIBLE_TEXT		= "\u001B[8m";

    // Command line arguments
    public static final String SCENARIO =       "scenario";
    public static final String LIST_SCENARIOS = "list";
    public static final String WITH_SERVER =    "http";
    public static final String LOG_HTTP =       "loghttp";
    public static final String LOCAL_IP =       "local";
    public static final String DESCRIBE =       "describe";

    public static final String LOGO =
            _BOLD +"\n    ________       .__          _____          _____       \n" +
            "   /  _____/______ |__| ____   /     \\   _____/ ____\\____  \n" +
            "  /   \\  ___\\____ \\|  |/  _ \\ /  \\ /  \\ /  _ \\   __\\/  _ \\ \n" +
            "  \\    \\_\\  \\  |_> >  (  <_> )    Y    (  <_> )  | (  <_> )\n" +
            "   \\______  /   __/|__|\\____/\\____|__  /\\____/|__|  \\____/ \n" +
            "          \\/|__|                     \\/                    "+_RESET
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
            "Set additional configuration options in: settings.yaml" +
            "\n"
            ;


}
