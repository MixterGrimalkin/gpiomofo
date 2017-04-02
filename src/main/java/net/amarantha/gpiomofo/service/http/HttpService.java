package net.amarantha.gpiomofo.service.http;

import net.amarantha.gpiomofo.service.http.entity.HttpCallback;
import net.amarantha.gpiomofo.service.http.entity.HttpCommand;
import net.amarantha.gpiomofo.service.http.entity.Param;

public interface HttpService {

    String fire(HttpCommand command);

    void fireAsync(HttpCallback callback, HttpCommand command);

    String get(String host, int port, String path, Param... params);

    void getAsync(HttpCallback callback, String host, int port, String path, Param... params);

    String post(String host, int port, String path, String payload, Param... params);

    void postAsync(HttpCallback callback, String host, int port, String path, String payload, Param... params);

}
