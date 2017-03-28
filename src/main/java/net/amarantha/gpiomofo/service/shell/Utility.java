package net.amarantha.gpiomofo.service.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.System.out;

public class Utility {

    public static void executeCommand(String[] command) {
        try {
            Process shellProcess = Runtime.getRuntime().exec(command);
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(shellProcess.getInputStream()));
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(shellProcess.getErrorStream()));
            String line;
            while ((line = reader1.readLine())!= null) {
                System.out.println(line);
            }
            while ((line = reader2.readLine())!= null) {
                System.out.println(line);
            }
            shellProcess.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////
    // Log to Console //
    ////////////////////

    public static final String BAR = "-------------------------------------------------------------";

    public static void bar() {
        out.println(BAR);
    }

    public static void log(String message) {
        log(false, message, false);
    }

    public static void log(boolean barBefore, String message) {
        log(barBefore, message, false);
    }

    public static void log(String message, boolean barAfter) {
        log(false, message, barAfter);
    }

    public static void log(boolean barBefore, String message, boolean barAfter) {
        out.println((barBefore?BAR+"\n":"")+message+(barAfter?"\n"+BAR:""));
    }


}
