package net.amarantha.gpiomofo.utility;

import net.amarantha.gpiomofo.pixeltape.RGB;

import javax.inject.Singleton;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Singleton
public class PropertyManager {

    public static final String PROPS_FILENAME = "application.properties";
    public static final String DEFAULT_FILENAME = "default.properties";

    protected Properties props;
    private Properties defProps;

    public PropertyManager() {
        defProps = loadProperties(DEFAULT_FILENAME, false);
        props = loadProperties(PROPS_FILENAME, true);
    }

    /////////////////
    // Persistence //
    /////////////////

    protected Properties loadProperties(String filename, boolean create) {
        Properties properties = new Properties();
        try {
            File propsFile = new File(filename);
            if ( propsFile.exists()) {
                FileInputStream in = new FileInputStream(propsFile);
                properties.load(new FileInputStream(propsFile));
                in.close();
            } else if ( create ) {
                FileWriter writer = new FileWriter(propsFile);
                writer.write("# Application Properties");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    protected void saveProperties() {
        try {
            FileOutputStream out = new FileOutputStream(PROPS_FILENAME);
            props.store(out, "Application Properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /////////////////////
    // Basic Set & Get //
    /////////////////////

    public void setProperty(String propName, String value) {
        if ( PLACEHOLDER.equals(value) ) {
            throw new IllegalArgumentException("That value is the placeholder, sorry!");
        }
        props.setProperty(propName, value);
        saveProperties();
    }

    private static final String PLACEHOLDER = "*** Please Set This Value ***";

    public String getString(String propName) throws PropertyNotFoundException {
        String propStr = props.getProperty(propName);
        if ( propStr==null || PLACEHOLDER.equals(propStr) ) {
            propStr = defProps.getProperty(propName);
            if ( propStr==null ) {
                props.setProperty(propName, PLACEHOLDER);
                saveProperties();
                throw new PropertyNotFoundException("Property '" + propName + "' not found in " + PROPS_FILENAME);
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

    /////////////
    // Integer //
    /////////////

    public Integer getInt(String propName) throws PropertyNotFoundException {
        try {
            return Integer.parseInt(getString(propName));
        } catch ( NumberFormatException e ) {
            throw new PropertyNotFoundException("Property '" + propName + "' in " + PROPS_FILENAME + " should be a number");
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

    /////////
    // RGB //
    /////////

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

    private String ip;

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

    ////////////////////////////
    // Command Line Arguments //
    ////////////////////////////

    private static Map<String, String> commandLineArgs = new HashMap<>();

    private static String helpText = "No help available, sorry!";

    public static void setHelpText(String helpText) {
        PropertyManager.helpText = helpText;
    }

    public static void processArgs(String[] args) {
        for ( String arg : args ) {
            if ( "-help".equals(arg) || "-h".equals(arg) ) {
                System.out.println(helpText);
                System.exit(0);
            }
            if ( arg.length()>1 && arg.charAt(0)=='-' ) {
                String argument = arg.substring(1);
                String[] pieces = argument.split("=");
                commandLineArgs.put(pieces[0], pieces.length==2 ? pieces[1] : "");
            } else {
                System.out.println("Bad Argument: " + arg);
            }
        }
    }

    public static void printArgs() {
        commandLineArgs.forEach((k,v)-> System.out.println(k+(v.isEmpty() ? " SET" : " = "+v)));
    }

    public boolean isArgumentPresent(String argName) {
        return commandLineArgs.containsKey(argName);
    }

    public String getArgumentValue(String argName) {
        return commandLineArgs.get(argName);
    }

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
                    result.put(propName, f.get(object).toString());
                } catch (IllegalAccessException | PropertyNotFoundException e) {
                    sb.append(propName).append("\n");
                }
            }
        }

        if ( !sb.toString().isEmpty() ) {
            throw new PropertyNotFoundException("The following properties could not be loaded from " + PROPS_FILENAME + ":\n" + sb.toString());
        }

        return result;
    }

}
