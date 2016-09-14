package net.amarantha.gpiomofo.service;

import net.amarantha.gpiomofo.utility.PropertyNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesMock extends PropertiesService {

    private Map<String, String> properties = new HashMap<>();

    @Override
    protected Properties loadProperties(String filename, boolean create) {
        return new Properties();
    }

    @Override
    public void setProperty(String propName, String value) {
        properties.put(propName, value);
    }

    @Override
    protected void saveProperties() {
        // do nothing
    }

    @Override
    public String getString(String propName) throws PropertyNotFoundException {
        return properties.get(propName);
    }

    @Override
    public String getIp() {
        return "127.0.0.1";
    }
}
