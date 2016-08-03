package net.amarantha.gpiomofo.osc;

import java.util.Arrays;
import java.util.List;

public class OscCommand {

    private final String host;
    private final int port;
    private final String address;
    private final List<Object> arguments;

    public OscCommand(String host, int port, String address, Object... arguments) {
        this.host = host;
        this.port = port;
        this.address = address;
        this.arguments = Arrays.asList(arguments);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public List<Object> getArguments() {
        return arguments;
    }

}
