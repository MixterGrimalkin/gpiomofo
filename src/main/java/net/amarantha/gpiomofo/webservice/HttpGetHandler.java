package net.amarantha.gpiomofo.webservice;

public interface HttpGetHandler {
    String handle(String param) throws HttpHandlerException;
}
