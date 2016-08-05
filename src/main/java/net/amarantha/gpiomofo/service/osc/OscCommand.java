package net.amarantha.gpiomofo.service.osc;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OscCommand command = (OscCommand) o;

        if (port != command.port) return false;
        if (host != null ? !host.equals(command.host) : command.host != null) return false;
        if (address != null ? !address.equals(command.address) : command.address != null) return false;
        return arguments != null ? arguments.equals(command.arguments) : command.arguments == null;

    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        return result;
    }
}
