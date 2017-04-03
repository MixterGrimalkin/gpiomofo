package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.utils.http.HttpService;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.properties.Property;

public class HttpTarget extends Target {

    @Inject private HttpService http;

    @Property("HttpAsyncTargets") private boolean async = true;

    @Override
    protected void onActivate() {
        if ( onCommand!=null ) {
            if ( async ) {
                http.fireAsync(null, onCommand);
            } else {
                http.fire(onCommand);
            }
        }
    }

    @Override
    protected void onDeactivate() {
        if ( offCommand!=null ) {
            if ( async ) {
                http.fireAsync(null, offCommand);
            } else {
                http.fire(offCommand);
            }
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

    public HttpCommand getOnCommand() {
        return onCommand;
    }

    public HttpCommand getOffCommand() {
        return offCommand;
    }
}
