package net.amarantha.gpiomofo.osc;

import com.google.inject.Singleton;
import com.illposed.osc.*;

import java.net.InetAddress;
import java.net.SocketException;

@Singleton
public class OscService {

    public void send(OscCommand command) {
        try {

            OSCPortOut sender = new OSCPortOut(InetAddress.getByName(command.getHost()), command.getPort());
            OSCMessage msg = new OSCMessage("/"+command.getAddress(), command.getArguments());
            sender.send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
