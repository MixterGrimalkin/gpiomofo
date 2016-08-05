package net.amarantha.gpiomofo.service.osc;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import java.net.InetAddress;
import java.net.SocketException;

public class OscServiceImpl implements OscService {

    @Override
    public void send(OscCommand command) {
        try {
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
            OSCPortIn receiver = new OSCPortIn(port);
            receiver.addListener("/"+address, listener);
            receiver.startListening();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}
