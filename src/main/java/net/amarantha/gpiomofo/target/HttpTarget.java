package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.http.HttpService;

public class HttpTarget extends Target {

    @Inject private HttpService http;

    @Override
    protected void onActivate() {
        if ( "GET".equals(methodOn) ) {
            http.getAsync(null, hostOn, pathOn);
        } else if ( "POST".equals(methodOn) ) {
            http.postAsync(null, hostOn, pathOn, payloadOn);
        }
    }

    @Override
    protected void onDeactivate() {
        if ( "GET".equals(methodOff) ) {
            http.getAsync(null, hostOff, pathOff);
        } else if ( "POST".equals(methodOff) ) {
            http.postAsync(null, hostOff, pathOff, payloadOff);
        }
    }

    private String methodOn;
    private String hostOn;
    private String pathOn;
    private String payloadOn;

    public HttpTarget onCommand(String method, String host, String path, String payload) {
        this.methodOn = method.toUpperCase();
        this.hostOn = host;
        this.pathOn = path;
        this.payloadOn = payload;
        return this;
    }

    private String methodOff;
    private String hostOff;
    private String pathOff;
    private String payloadOff;

    public HttpTarget offCommand(String method, String host, String path, String payload) {
        this.methodOff = method.toUpperCase();
        this.hostOff = host;
        this.pathOff = path;
        this.payloadOff = payload;
        return this;
    }

}
