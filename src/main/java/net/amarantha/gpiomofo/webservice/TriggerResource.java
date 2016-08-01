package net.amarantha.gpiomofo.webservice;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.TriggerFactory;

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
    @Path("{trigger}/fire")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fireTrigger(@PathParam("trigger") String triggerName) {
        if ( fireTrigger(triggerName, true) ) {
            return ok("Fired trigger '" + triggerName + "'");
        } else {
            return error("Trigger '" + triggerName + "' not found");
        }
    }

    @POST
    @Path("{trigger}/cancel")
    @Produces(MediaType.TEXT_PLAIN)
    public Response cancelTrigger(@PathParam("trigger") String triggerName) {
        if ( fireTrigger(triggerName, false) ) {
            return ok("Cancelled trigger '" + triggerName + "'");
        } else {
            return error("Trigger '" + triggerName + "' not found");
        }
    }

    private boolean fireTrigger(String triggerName, boolean state) {
        Trigger trigger = triggers.getTrigger(triggerName);
        if ( trigger==null ) {
            return false;
        }
        trigger.fire(state);
        return true;
    }

}
