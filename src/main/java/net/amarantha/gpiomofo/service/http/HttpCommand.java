package net.amarantha.gpiomofo.service.http;

import java.util.Arrays;
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

    @Override
    public String toString() {
        return method + "-http://" + getFullHost() + "/" + getFullPath();
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
