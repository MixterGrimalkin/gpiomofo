package net.amarantha.gpiomofo.service.http;

import javax.ws.rs.core.Response;

public interface HttpCallback {
    void call(Response response);
}
