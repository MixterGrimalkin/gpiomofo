package net.amarantha.gpiomofo.service;

import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.utility.Property;
import net.amarantha.gpiomofo.utility.PropertyNotFoundException;

import javax.inject.Singleton;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Singleton
public class PropertiesService {

    ////////////////////////////
    // Command Line Arguments //
    ////////////////////////////

    public static void processArgs(String[] args) {
        for ( String arg : args ) {
            if ( "-help".equals(arg) || "-h".equals(arg) ) {
                System.out.println(helpText);
                System.exit(0);
            }
            if ( arg.length()>1 && arg.charAt(0)=='-' ) {
                String[] pieces = arg.substring(1).split("=");
                commandLineArgs.put(pieces[0], pieces.length==2 ? pieces[1] : "");
            } else {
                System.out.println("Bad Argument: " + arg);
            }
        }
    }

    public boolean isArgumentPresent(String argName) {
        return commandLineArgs.containsKey(argName);
    }

    public String getArgumentValue(String argName) {
        return commandLineArgs.get(argName);
    }

    public static void setHelpText(String helpText) {
        PropertiesService.helpText = helpText;
    }

    public static void printArgs() {
        commandLineArgs.forEach((k,v)-> System.out.println(k+(v.isEmpty() ? " SET" : " = "+v)));
    }

    private static String helpText = "No help available, sorry!";
    private static Map<String, String> commandLineArgs = new HashMap<>();

    //////////////////////
    // Properties Files //
    //////////////////////

    public PropertiesService() {
        defProps = loadProperties(DEF_FILENAME, false);
        appProps = loadProperties(APP_FILENAME, true);
    }

    protected Properties loadProperties(String filename, boolean create) {

        Properties properties = new Properties();
        File propsFile = new File(filename);

        if ( propsFile.exists()) {
            try (FileInputStream in = new FileInputStream(propsFile)) {
                properties.load(in);
            } catch ( IOException ignored ) {}

        } else if ( create ) {
            try (FileWriter writer = new FileWriter(propsFile)) {
                writer.write("# Application Properties");
            } catch ( IOException ignored ) {}
        }

        return properties;
    }

    private Properties appProps;
    private Properties defProps;

    ////////////////
    // Set & Save //
    ////////////////

    public void setProperty(String propName, String value) {
        if ( PLACEHOLDER.equals(value) ) {
            throw new IllegalArgumentException("That value is the placeholder, sorry!");
        }
        appProps.setProperty(propName, value);
        saveProperties();
    }

    protected void saveProperties() {
        try (FileOutputStream out = new FileOutputStream(APP_FILENAME)) {
            appProps.store(out, "Application Properties");
        } catch (IOException ignored) {}
    }

    ////////////////
    // Get String //
    ////////////////

    public String getString(String propName) throws PropertyNotFoundException {
        String propStr = appProps.getProperty(propName);
        if ( propStr==null || PLACEHOLDER.equals(propStr) ) {
            propStr = defProps.getProperty(propName);
            if ( propStr==null ) {
                appProps.setProperty(propName, PLACEHOLDER);
                saveProperties();
                throw new PropertyNotFoundException("Property '" + propName + "' not found in " + APP_FILENAME);
            } else {
                setProperty(propName, propStr);
            }
        }
        return propStr;
    }

    public String getString(String propName, String defaultValue) {
        try {
            return getString(propName);
        } catch (PropertyNotFoundException e) {
            setProperty(propName, defaultValue);
            return defaultValue;
        }
    }

    /////////////////
    // Get Integer //
    /////////////////

    public Integer getInt(String propName) throws PropertyNotFoundException {
        try {
            return Integer.parseInt(getString(propName));
        } catch ( NumberFormatException e ) {
            throw new PropertyNotFoundException("Property '" + propName + "' in " + APP_FILENAME + " should be a number");
        }
    }

    public Integer getInt(String propName, Integer defaultValue) {
        try {
            return getInt(propName);
        } catch (PropertyNotFoundException e) {
            setProperty(propName, defaultValue.toString());
            saveProperties();
            return defaultValue;
        }
    }

    /////////////
    // Get RGB //
    /////////////

    public RGB getRGB(String propName) throws PropertyNotFoundException {
        String[] rgb = getString(propName).split(",");
        if ( rgb.length==3 ) {
            try {
                int r = Integer.parseInt(rgb[0].trim());
                int g = Integer.parseInt(rgb[1].trim());
                int b = Integer.parseInt(rgb[2].trim());
                return new RGB(r, g, b);
            } catch ( NumberFormatException ignored ) {}
        }
        throw new PropertyNotFoundException("Property '" + propName + "' is not a valid RGB colour");
    }

    ////////////////
    // IP Address //
    ////////////////

    public String getIp() {
        if ( ip==null ) {
            StringBuilder output = new StringBuilder();
            Process p;
            try {
                p = Runtime.getRuntime().exec("sh ip.sh");
                p.waitFor();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while ((line = reader.readLine())!= null) {
                    output.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ip = output.toString().trim();
        }
        return ip;
    }

    private String ip;

    ////////////////////////
    // Property Injection //
    ////////////////////////

    public Map<String, String> injectProperties(Object object) throws PropertyNotFoundException {

        Map<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (Field f : object.getClass().getDeclaredFields() ) {
            Annotation a = f.getAnnotation(Property.class);
            if ( a!=null ) {
                String propName = ((Property)a).value();
                try {
                    f.setAccessible(true);
                    if ( f.getType()==String.class ) {
                        if ( propName.equals("IP") ) {
                            f.set(object, getIp());
                        } else {
                            f.set(object, getString(propName));
                        }
                    } else if (f.getType() == int.class || f.getType() == Integer.class) {
                        f.set(object, getInt(propName));
                    } else if (f.getType() == RGB.class ) {
                        f.set(object, getRGB(propName));
                    } else {
                        getString(propName);
                    }
                    Object value = f.get(object);
                    if ( value==null ) {
                        throw new PropertyNotFoundException("Null value");
                    }
                    result.put(propName, value.toString());
                } catch (IllegalAccessException | PropertyNotFoundException e) {
                    sb.append(propName).append("\n");
                }
            }
        }

        if ( !sb.toString().isEmpty() ) {
            throw new PropertyNotFoundException("The following properties could not be loaded from " + APP_FILENAME + ":\n" + sb.toString());
        }

        return result;
    }

    public static final String APP_FILENAME = "application.properties";
    public static final String DEF_FILENAME = "default.properties";

    private static final String PLACEHOLDER = "*** Please Set This Value ***";

}
