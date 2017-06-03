package net.amarantha.gpiomofo.webservice;

import com.google.inject.Singleton;
import net.amarantha.utils.properties.PropertiesService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static net.amarantha.gpiomofo.core.Constants.LOCAL_IP;
import static net.amarantha.gpiomofo.core.Constants.LOG_HTTP;
import static net.amarantha.utils.properties.PropertiesService.isArgumentPresent;

@Singleton
public class WebService extends Resource {

    private HttpServer server;

    @Inject private PropertiesService props;

    @Inject private SystemResource systemResource;
    @Inject private TriggerResource triggerResource;
    @Inject private MonitorResource monitorResource;
    @Inject private CallbackResource callbackResource;

    private boolean running = false;

    public HttpServer start() {

        System.out.println("Starting Web Server...");

        String ip = isArgumentPresent(LOCAL_IP) ? "127.0.0.1" : props.getIp().trim();
        String fullUri = "http://" + ip + ":8001/gpiomofo/";

        try {
            final ResourceConfig rc = new ResourceConfig().packages("net.amarantha.gpiomofo.webservice");
            if ( isArgumentPresent(LOG_HTTP)) {
                rc.register(LoggingFilter.class);
            }
            server = GrizzlyHttpServerFactory.createHttpServer(URI.create(fullUri), rc);
            System.out.println("Web Service Online @ " + fullUri);
            running = true;
        } catch ( Exception e ) {
            System.out.println("Could not start Web Service!");
            e.printStackTrace();
        }

        return server;
    }

    public void stop() {
        System.out.println("Stopping Web Server...");
        if ( server!=null ) {
            server.shutdown();
        }
    }

    public boolean isRunning() {
        return running;
    }

    private Map<String, HttpGetHandler> getHandlers = new HashMap<>();
    private Map<String, HttpPostHandler> postHandlers = new HashMap<>();

    public WebService onGet(String path, HttpGetHandler handler) {
        getHandlers.put(path, handler);
        return this;
    }

    public WebService onPost(String path, HttpPostHandler handler) {
        postHandlers.put(path, handler);
        return this;
    }

    Response fireGet(String path, String param) throws HttpHandlerException {
        HttpGetHandler handler = getHandlers.get(path);
        if ( handler==null ) {
            throw new HttpHandlerException("No GET handler found for path '" + path + "'");
        }
        return ok(handler.handle(param));
    }

    Response firePost(String path, String body, String param) throws HttpHandlerException {
        HttpPostHandler handler = postHandlers.get(path);
        if ( handler==null ) {
            throw new HttpHandlerException("No POST handler found for path '" + path + "'");
        }
        return ok(handler.handle(body, param));
    }





}
