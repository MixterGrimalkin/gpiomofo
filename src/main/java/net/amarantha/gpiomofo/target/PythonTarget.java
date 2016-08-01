package net.amarantha.gpiomofo.target;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class PythonTarget extends Target {

    private Timer timer;
    private Process shellProcess;

    @Override
    protected void onActivate() {
        if ( timer!=null ) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fireProcess();
            }
        }, 0);
    }

    @Override
    protected void onDeactivate() {
        if ( shellProcess !=null ) {
            shellProcess.destroy();
        }
        if ( timer!=null ) {
            timer.cancel();
        }
        shellProcess = null;
        timer = null;
    }

    private String scriptFile;

    public PythonTarget scriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
        return this;
    }

    private void fireProcess() {
        try {
            shellProcess = Runtime.getRuntime().exec("python " + scriptFile);
//            shellProcess.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(shellProcess.getInputStream()));
            String line = "";
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine())!= null) {
                System.out.println(line);
                output.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
