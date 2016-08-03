package net.amarantha.gpiomofo.http;

public interface HttpService {

    String fire(HttpCommand command);

    void fireAsync(HttpCallback callback, HttpCommand command);

    String get(String host, String path, Param... params);

    void getAsync(HttpCallback callback, String host, String path, Param... params);

    String post(String host, String path, String payload, Param... params);

    void postAsync(HttpCallback callback, String host, String path, String payload, Param... params);

}
