package net.amarantha.gpiomofo.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SchedulerProperties extends PropertyManager {

    public String getCuePackage() {
        return getString("cuePackage", "net.amarantha.scheduler.cue");
    }

    private String ip = null;

    public String getIp() {
        if ( ip==null ) {
            StringBuilder output = new StringBuilder();

            Process p;
            try {
                p = Runtime.getRuntime().exec("sh scripts/getip.sh");
                p.waitFor();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while ((line = reader.readLine())!= null) {
                    output.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ip = output.toString();
        }
        System.out.println(ip);
        return ip;
    }


}
