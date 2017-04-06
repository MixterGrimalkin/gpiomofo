package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.utils.shell.Utility;

import java.util.Timer;
import java.util.TimerTask;

public class ShellTarget extends Target {

    @Parameter("command") private String command;

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

    private void fireProcess() {
        Utility.executeCommand(new String[]{command});
    }

    private Timer timer;
    private Process shellProcess;

}
