package net.amarantha.gpiomofo.osc;

import com.google.inject.Singleton;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.net.InetAddress;

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

}
