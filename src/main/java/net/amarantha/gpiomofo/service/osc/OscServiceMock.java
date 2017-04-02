package net.amarantha.gpiomofo.service.osc;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import java.util.*;

public class OscServiceMock implements OscService {

    private OscCommand lastCommand;
    private Map<String, List<OSCListener>> allListeners = new HashMap<>();

    @Override
    public void send(OscCommand command) {
        lastCommand = command;
    }

    @Override
    public void onReceive(int port, String address, OSCListener listener) {
        List<OSCListener> listeners = allListeners.get(address);
        if ( listeners==null ) {
            listeners = new LinkedList<>();
            allListeners.put(address, listeners);
        }
        listeners.add(listener);
    }

    public void receive(String address, OSCMessage message) {
        List<OSCListener> listeners = allListeners.get(address);
        if ( listeners!=null ) {
            for (OSCListener listener : listeners) {
                listener.acceptMessage(new Date(), message);
            }
        }
    }

    public OscCommand getLastCommand() {
        return lastCommand;
    }

    public void clearLastCommand() {
        lastCommand = null;
    }


}
