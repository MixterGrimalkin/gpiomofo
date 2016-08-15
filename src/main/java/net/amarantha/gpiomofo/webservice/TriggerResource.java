package net.amarantha.gpiomofo.webservice;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.trigger.Trigger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("trigger")
public class TriggerResource extends Resource {

    private static TriggerFactory triggers;

    public TriggerResource() {}

    @Inject
    public TriggerResource(TriggerFactory triggers) {
        TriggerResource.triggers = triggers;
    }

    @POST
    @Path("{trigger}/fireTriggers")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fireTrigger(@PathParam("trigger") String triggerName) {
        if ( fireTrigger(triggerName, true) ) {
            return ok("Fired trigger '" + triggerName + "'\n");
        } else {
            return error("Trigger '" + triggerName + "' not found\n");
        }
    }

    @POST
    @Path("{trigger}/cancel")
    @Produces(MediaType.TEXT_PLAIN)
    public Response cancelTrigger(@PathParam("trigger") String triggerName) {
        if ( fireTrigger(triggerName, false) ) {
            return ok("Cancelled trigger '" + triggerName + "'\n");
        } else {
            return error("Trigger '" + triggerName + "' not found\n");
        }
    }

    private boolean fireTrigger(String triggerName, boolean state) {
        Trigger trigger = triggers.get(triggerName);
        if ( trigger==null ) {
            return false;
        }
        trigger.fire(state);
        return true;
    }

}
