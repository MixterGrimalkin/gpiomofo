package net.amarantha.gpiomofo.service.osc;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class OscServiceImpl implements OscService {

    @Override
    public void send(OscCommand command) {
        try {
            System.out.println("OSC " + command.getHost()+":"+command.getPort()+"/"+command.getAddress());
            OSCPortOut sender = new OSCPortOut(InetAddress.getByName(command.getHost()), command.getPort());
            OSCMessage msg = new OSCMessage("/"+command.getAddress(), command.getArguments());
            sender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(int port, String address, OSCListener listener) {
        try {
            OSCPortIn receiver = getInPort(port);
            receiver.addListener("/"+address, listener);
            receiver.startListening();
            System.out.println("Listener Added");
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, OSCPortIn> inPorts = new HashMap<>();

    private OSCPortIn getInPort(int port) throws SocketException {
        OSCPortIn result = inPorts.get(port);
        if ( result==null ) {
            result = new OSCPortIn(port);
            inPorts.put(port, result);
        }
        return result;
    }

}
