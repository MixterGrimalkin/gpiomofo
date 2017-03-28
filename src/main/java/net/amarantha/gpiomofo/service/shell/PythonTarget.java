package net.amarantha.gpiomofo.service.shell;

import net.amarantha.gpiomofo.core.target.Target;

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

    public String getScriptFile() {
        return scriptFile;
    }

    private void fireProcess() {
        Utility.executeCommand(new String[]{"python", scriptFile});
    }

}
