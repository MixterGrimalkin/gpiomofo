package net.amarantha.gpiomofo.utility;

import javax.inject.Singleton;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Singleton
public class PropertyManager {

    public static final String PROPS_FILENAME = "application.properties";

    protected Properties props;

    public PropertyManager() {
        props = new Properties();
        loadProperties();
    }

    protected void loadProperties() {
        try {
            File propsFile = new File(PROPS_FILENAME);
            if ( propsFile.exists()) {
                FileInputStream in = new FileInputStream(propsFile);
                props.load(new FileInputStream(propsFile));
                in.close();
            } else {
                FileWriter writer = new FileWriter(propsFile);
                writer.write("# Application Properties");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void saveProperties() {
        try {
            FileOutputStream out = new FileOutputStream(PROPS_FILENAME);
            props.store(out, "Application Properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProperty(String propName, int value) {
        setProperty(propName, ""+value);
    }

    public void setProperty(String propName, String value) {
        props.setProperty(propName, value);
        saveProperties();
    }

    public String getString(String propName, String defaultValue) {
        String propStr = props.getProperty(propName);
        if ( propStr==null ) {
            propStr = defaultValue;
            setProperty(propName, defaultValue);
        }
        return propStr;
    }

    public Long getLong(String propName, Long defaultValue) {
        String propStr = props.getProperty(propName);
        if ( propStr==null ) {
            propStr = defaultValue.toString();
            setProperty(propName, propStr);
        }
        try {
            return Long.parseLong(propStr);
        } catch ( NumberFormatException e ) {
            return defaultValue;
        }
    }

    public Integer getInt(String propName, Integer defaultValue) {
        String propStr = props.getProperty(propName);
        if ( propStr==null ) {
            propStr = defaultValue.toString();
            setProperty(propName, propStr);
        }
        try {
            return Integer.parseInt(propStr);
        } catch ( NumberFormatException e ) {
            return defaultValue;
        }
    }

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

    private static boolean withServer = true;

    public boolean isWithServer() {
        return withServer;
    }

    public static void processArgs(String[] args) {
        List<String> arguments = Arrays.asList(args);
        withServer = !arguments.contains("-noserver");
    }

}
