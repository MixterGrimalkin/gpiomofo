package net.amarantha.gpiomofo.service.http;

import com.google.inject.Singleton;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.Timer;
import java.util.TimerTask;

import static net.amarantha.gpiomofo.service.http.HttpCommand.GET;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;
import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;

@Singleton
public class HttpServiceImpl implements HttpService {

    @Override
    public String fire(HttpCommand command) {
        switch ( command.getMethod().toUpperCase() ) {
            case GET:
                System.out.println(">>> GET http://"+command.getFullHost()+"/"+command.getFullPath());
                return get(command.getHost(), command.getPort(), command.getFullPath(), command.getParamsArray());
            case POST:
                System.out.println(">>> POST http://"+command.getFullHost()+"/"+command.getFullPath());
                return post(command.getHost(), command.getPort(), command.getFullPath(), command.getPayload(), command.getParamsArray());
        }
        return null;
    }

    @Override
    public void fireAsync(HttpCallback callback, HttpCommand command) {
        switch ( command.getMethod().toUpperCase() ) {
            case GET:
                System.out.println(">|> GET http://"+command.getFullHost()+"/"+command.getFullPath());
                getAsync(callback, command.getHost(), command.getPort(), command.getFullPath(), command.getParamsArray());
                break;
            case POST:
                System.out.println(">|> POST http://"+command.getFullHost()+"/"+command.getFullPath());
                postAsync(callback, command.getHost(), command.getPort(), command.getFullPath(), command.getPayload(), command.getParamsArray());
                break;
        }
    }

    @Override
    public String get(String host, int port, String path, Param... params) {
        String result = null;
        Response response = null;
        try {
            response = getEndpoint(host+":"+port, path, params).get();
            result = response.readEntity(String.class);
        } catch ( Exception e ) {
//            System.out.println(host + ": " + e.getMessage());
        } finally {
            if ( response!=null ) response.close();
        }
        return result;
    }

    @Override
    public void getAsync(HttpCallback callback, String host, int port, String path, Param... params) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = getEndpoint(host+":"+port, path, params).get();
                } catch ( Exception e ) {
                    System.out.println(host + ": " + e.getMessage());
                } finally {
                    if ( response!=null ) response.close();
                }
                if ( callback!=null ) {
                    callback.call(response);
                }
            }
        }, 0);
    }

    @Override
    public String post(String host, int port, String path, String payload, Param... params) {
        String result = null;
        Response response = null;
        try {
            response = getEndpoint(host+":"+port, path, params).post(Entity.entity(payload, MediaType.TEXT_PLAIN));
            result = response.readEntity(String.class);
        } catch ( Exception e ) {
//            System.out.println(host + ": " + e.getMessage());
        } finally {
            if ( response!=null ) response.close();
        }
        return result;
    }

    @Override
    public void postAsync(HttpCallback callback, String host, int port, String path, String payload, Param... params) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = getEndpoint(host+":"+port, path, params).post(Entity.entity(payload, MediaType.TEXT_PLAIN));
                } catch ( Exception e ) {
//                    System.out.println(host + ": " + e.getMessage());
                } finally {
                    if ( response!=null ) response.close();
                }
                if ( callback!=null ) {
                    callback.call(response);
                }
            }
        }, 0);
    }

    private Invocation.Builder getEndpoint(String host, String path, Param... params) throws NoRouteToHostException, ConnectException {
        Client client = ClientBuilder.newClient();
        client.property(CONNECT_TIMEOUT, 5000);
        WebTarget endpoint = client.target("http://"+host).path(path);
        for ( Param param : params ) {
            endpoint.queryParam(param.getName(), param.getValue());
        }
        return endpoint.request();
    }

}
