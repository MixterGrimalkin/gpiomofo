package net.amarantha.gpiomofo.webservice;

public interface HttpPostHandler {
    String handle(String body, String param) throws HttpHandlerException;
}
