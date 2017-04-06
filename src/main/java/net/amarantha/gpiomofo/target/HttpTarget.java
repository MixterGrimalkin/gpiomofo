package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.utils.http.HttpService;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.service.Service;

public class HttpTarget extends Target {

    @Service private HttpService http;

    @Property("HttpAsyncTargets") private boolean async = true;

    @Parameter("onCommand") private HttpCommand onCommand;
    @Parameter("offCommand") private HttpCommand offCommand;

    @Override
    public void enable() {
        if ( offCommand==null ) {
            oneShot(true);
        }
    }

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

}
