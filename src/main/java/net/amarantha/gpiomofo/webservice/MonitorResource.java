package net.amarantha.gpiomofo.webservice;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.core.factory.TargetFactory;
import net.amarantha.gpiomofo.core.factory.TriggerFactory;
import net.amarantha.gpiomofo.core.trigger.Trigger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static net.amarantha.gpiomofo.scenario.GingerlinePanic.*;

@Path("monitor")
public class MonitorResource extends Resource {

    private static TargetFactory targets;
    private static TriggerFactory triggers;

    public MonitorResource() {
    }

    @Inject
    public MonitorResource(TriggerFactory triggers, TargetFactory targets) {
        MonitorResource.triggers = triggers;
        MonitorResource.targets = targets;
    }

    @GET
    public Response getMonitorFlags() {
        Trigger trigger1 = triggers.get(URL_PANIC_BRIEFING);
        Trigger trigger2 = triggers.get(URL_PANIC_GAMESHOW);
        Trigger trigger3 = triggers.get(URL_PANIC_UNDERWATER);
        Trigger trigger4 = triggers.get(URL_PANIC_BIKES);
        Trigger trigger5 = triggers.get(URL_PANIC_KITCHEN);
        Trigger trigger6 = triggers.get(URL_PANIC_TOYBOX);
        String result = "";
        result += trigger1.isActive() ? "0," : "";
        result += trigger2.isActive() ? "1," : "";
        result += trigger3.isActive() ? "2," : "";
        result += trigger4.isActive() ? "3," : "";
        result += trigger5.isActive() ? "4," : "";
        result += trigger6.isActive() ? "5," : "";
        return ok(result);
    }

    @POST
    @Path("reset")
    public Response reset() {
        Trigger trigger1 = triggers.get(URL_PANIC_BRIEFING);
        Trigger trigger2 = triggers.get(URL_PANIC_GAMESHOW);
        Trigger trigger3 = triggers.get(URL_PANIC_UNDERWATER);
        Trigger trigger4 = triggers.get(URL_PANIC_BIKES);
        Trigger trigger5 = triggers.get(URL_PANIC_KITCHEN);
        Trigger trigger6 = triggers.get(URL_PANIC_TOYBOX);
        trigger1.fire(false);
        trigger2.fire(false);
        trigger3.fire(false);
        trigger4.fire(false);
        trigger5.fire(false);
        trigger6.fire(false);
        return ok("OK");
    }

}
