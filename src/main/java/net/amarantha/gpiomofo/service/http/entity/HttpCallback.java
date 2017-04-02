package net.amarantha.gpiomofo.service.http.entity;

import javax.ws.rs.core.Response;

public interface HttpCallback {
    void call(Response response);
}
