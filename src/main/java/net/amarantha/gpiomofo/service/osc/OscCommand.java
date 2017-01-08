package net.amarantha.gpiomofo.service.osc;

import java.util.Arrays;
import java.util.LinkedList;
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

    public static OscCommand fromString(String input) {
        String[] pieces = input.split("\\|");
        if ( pieces.length > 0 ) {
            String fullPath = pieces[0];
            String[] hostAndPort = fullPath.split("/")[0].split(":");
            String host = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            String address = fullPath.substring(fullPath.indexOf("/")+1);
            List<Object> arguments = new LinkedList<>();
            for ( int i=1; i<pieces.length; i++ ) {
                arguments.add(pieces[i]);
            }
            return new OscCommand(host, port, address, arguments.toArray(new Object[arguments.size()]));
        }
        return null;
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

    private String compileArguments() {
        StringBuilder sb = new StringBuilder();
        for ( Object argument : arguments ) {
            sb.append("|").append(argument.toString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return host + ":" + port + "/" + address + compileArguments();
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
