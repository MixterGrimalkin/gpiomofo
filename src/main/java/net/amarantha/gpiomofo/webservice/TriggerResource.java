package net.amarantha.gpiomofo.webservice;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.trigger.Trigger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

@Path("trigger")
public class TriggerResource extends Resource {

    private static TriggerFactory triggers;

    public TriggerResource() {}

    @Inject
    public TriggerResource(TriggerFactory triggers) {
        TriggerResource.triggers = triggers;
    }

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTriggers() {
        try {
            JSONArray ja = new JSONArray();
            triggers.getAll().forEach((t)->ja.put(t.getName()));
            JSONObject obj = new JSONObject();
            obj.put("triggers", ja);
            return ok(obj.toString());
        } catch (JSONException e) {
            return error(e.getMessage());
        }
    }

    @POST
    @Path("{trigger}/fire")
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
