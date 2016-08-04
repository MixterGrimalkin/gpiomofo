package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.http.HttpCommand;
import net.amarantha.gpiomofo.http.HttpService;

public class HttpTarget extends Target {

    @Inject private HttpService http;

    @Override
    protected void onActivate() {
        if ( onCommand!=null ) {
            http.fire(onCommand);
        }
    }

    @Override
    protected void onDeactivate() {
        if ( offCommand!=null ) {
            http.fire(offCommand);
        }
    }

    private HttpCommand onCommand;
    private HttpCommand offCommand;

    public HttpTarget onCommand(HttpCommand command) {
        this.onCommand = command;
        return this;
    }

    public HttpTarget offCommand(HttpCommand command) {
        this.offCommand = command;
        return this;
    }

}
