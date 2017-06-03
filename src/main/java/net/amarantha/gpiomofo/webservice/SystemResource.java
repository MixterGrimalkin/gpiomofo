package net.amarantha.gpiomofo.webservice;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.core.GpioMofo;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Path("system")
public class SystemResource extends Resource {

    private static GpioMofo application;

    public SystemResource() {}

    @Inject
    public SystemResource(GpioMofo application) {
        SystemResource.application = application;
    }

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScenarioName() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("scenario", application.getScenario().getName());
            return ok(obj.toString());
        } catch (JSONException e) {
            return error(e.getMessage());
        }
    }

    @GET
    @Path("template")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiTemplate() {
        JSONObject obj = new JSONObject();
        try {
            for (Entry<String, String> pair : application.getScenario().getApiTemplate().entrySet() ) {
                obj.put(pair.getKey(), pair.getValue());
            }
        } catch (JSONException e) {
            return error("Could not get template from scenario");
        }
        return ok(obj.toString());
    }

    @POST
    @Path("api")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response fireApi(String body) {
        try {
            Map<String, String> params = new HashMap<>();
            JSONObject obj = new JSONObject(body);
            Iterator it = obj.keys();
            while ( it.hasNext() ) {
                String key = it.next().toString();
                params.put(key, obj.get(key).toString());
            }
            application.getScenario().incomingApiCall(params);
            return ok("Fired API");
        } catch (JSONException e) {
            return error("Bad JSON: " + e.getMessage());
        }
    }


}
