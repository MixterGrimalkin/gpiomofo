package net.amarantha.gpiomofo.service.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HttpCommand {

    public static final String GET = "GET";
    public static final String POST = "POST";

    private final String method;
    private final String host;
    private final int port;
    private final String basePath;
    private final String path;
    private final List<Param> params;
    private final String payload;

    public HttpCommand(String method, String host, int port, String basePath, String path, String payload, Param... params) {
        this(method, host, port, basePath, path, payload, Arrays.asList(params));
    }

    public HttpCommand(String method, String host, int port, String basePath, String path, String payload, List<Param> params) {
        this.method = method;
        this.host = host;
        this.port = port;
        this.basePath = basePath;
        this.path = path;
        this.payload = payload;
        this.params = params;
    }

    public static HttpCommand fromString(String input) {
        String[] pieces = input.split("\\|");
        if ( pieces.length > 1 ) {
            String method = pieces[0].toUpperCase();
            String fullURL = pieces[1];
            String body = pieces.length==3 ? pieces[2] : "";
            String[] hostAndPort = fullURL.split("/")[0].split(":");
            String host = hostAndPort[0];
            int port = hostAndPort.length==2 ? Integer.parseInt(hostAndPort[1]) : 80;
            String[] pathAndParams = fullURL.substring(fullURL.indexOf("/")+1).split("\\?");
            String path = pathAndParams[0];
            List<Param> params = new LinkedList<>();
            if ( pathAndParams.length==2 ) {
                String[] paramPairs = pathAndParams[1].split("&");
                for ( String pair : paramPairs ) {
                    String[] keyAndValue = pair.split("=");
                    if ( keyAndValue.length==2 ) {
                        params.add(new Param(keyAndValue[0], keyAndValue[1]));
                    }
                }
            }
            return new HttpCommand(method, host, port, path, "", body, params);
        }
        return null;
    }

    ///////////////
    // Overrides //
    ///////////////

    public HttpCommand withMethod(String method) {
        return new HttpCommand(method, host, port, basePath, path, payload, params);
    }

    public HttpCommand withHost(String host) {
        return new HttpCommand(method, host, port, basePath, path, payload, params);
    }

    public HttpCommand withPort(int port) {
        return new HttpCommand(method, host, port, basePath, path, payload, params);
    }

    public HttpCommand withBasePath(String basePath) {
        return new HttpCommand(method, host, port, basePath, path, payload, params);
    }

    public HttpCommand withPath(String path) {
        return new HttpCommand(method, host, port, basePath, path, payload, params);
    }

    public HttpCommand withPayload(String payload) {
        return new HttpCommand(method, host, port, basePath, path, payload, params);
    }

    public HttpCommand withParams(List<Param> params) {
        return new HttpCommand(method, host, port, basePath, path, payload, params);
    }

    public HttpCommand withParams(Param... params) {
        return new HttpCommand(method, host, port, basePath, path, payload, params);
    }

    /////////////
    // Getters //
    /////////////

    public String getFullHost() {
        return host + ":" + port;
    }

    public String getFullPath() {
        return basePath + (basePath.isEmpty()||path.isEmpty()?"":"/") + path;
    }

    public String getMethod() {
        return method;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getPath() {
        return path;
    }

    public String getPayload() {
        return payload;
    }

    public List<Param> getParams() {
        return params;
    }

    public Param[] getParamsArray() {
        return params.toArray(new Param[params.size()]);
    }

    ////////////
    // Object //
    ////////////

    private String compileParams() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for ( Param param : params ) {
            sb.append(first?"":"&").append(param.getName()).append("=").append(param.getValue());
            first = false;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String paramStr = compileParams();
        return
                method + "|http://" + getFullHost() + "/" + getFullPath()
                        + (paramStr.isEmpty() ? "" : "?" + paramStr )
                        + (payload.isEmpty()  ? "" : "|" + payload + "}" );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpCommand that = (HttpCommand) o;
        if (port != that.port) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        String thisFullPath = getFullPath();
        String thatFullPath = that.getFullPath();
        if (getFullPath()!=null ? !getFullPath().equals(that.getFullPath()) : that.getFullPath()!=null ) return false;
        if (params != null ? !params.equals(that.params) : that.params != null) return false;
        return payload != null ? payload.equals(that.payload) : that.payload == null;
    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (basePath != null ? basePath.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }

}
