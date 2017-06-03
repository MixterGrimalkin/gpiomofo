package net.amarantha.gpiomofo.webservice;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/")
@Singleton
@Encoded
public class CallbackResource extends Resource {

    private static WebService webService;

    public CallbackResource() {}

    @Inject
    public CallbackResource(WebService webService) {
        CallbackResource.webService = webService;
    }

    @GET
    @Path("{path}")
    public Response processGet(@PathParam("path") String path, @QueryParam("p") String param) {
        try {
            return webService.fireGet(path, param);
        } catch (HttpHandlerException e) {
            return error(e.getMessage());
        }
    }

    @POST
    @Path("{path}")
    public Response processPost(@PathParam("path") String path, @QueryParam("p") String param, String body) {
        try {
            return webService.firePost(path, body, param);
        } catch (HttpHandlerException e) {
            return error(e.getMessage());
        }
    }



}
