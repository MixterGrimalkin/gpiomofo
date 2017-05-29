package net.amarantha.gpiomofo.webservice;

import com.google.inject.Singleton;
import net.amarantha.utils.properties.PropertiesService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import java.net.URI;

import static net.amarantha.gpiomofo.core.Constants.LOCAL_IP;
import static net.amarantha.gpiomofo.core.Constants.LOG_HTTP;
import static net.amarantha.utils.properties.PropertiesService.isArgumentPresent;

@Singleton
public class WebService {

    private HttpServer server;

    @Inject private PropertiesService props;

    @Inject private TriggerResource triggerResource;
    @Inject private MonitorResource monitorResource;

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
}
