package net.amarantha.gpiomofo.target;

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractTarget {

    public final void activate() {
        if ( !oneShot ) {
            active = true;
        }
        onActivate();
    }

    protected abstract void onActivate();

    public final void deactivate() {
        active = false;
        onDeactivate();
    }

    protected abstract void onDeactivate();

    public void processTrigger(boolean inputState) {
        if ( inputState== triggerOn) {
            if ( !active ) {
                stopTimer();
                if (deactivateTimeout != null) {
                    startTimer();
                }
                activate();
            }
        } else if (followTrigger) {
            stopTimer();
            deactivate();
        }

    }

    private void stopTimer() {
        if ( deactivationTimer!=null ) {
            deactivationTimer.cancel();
            deactivationTimer = null;
        }
    }

    private void startTimer() {
        deactivationTimer = new Timer();
        deactivationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                deactivate();
            }
        }, deactivateTimeout);
    }

    public boolean isActive() {
        return active;
    }

    private Timer deactivationTimer;
    private boolean active = false;

    ///////////
    // Setup //
    ///////////

    private boolean oneShot = false;
    private boolean triggerOn = true;
    private boolean followTrigger = true;
    private Long deactivateTimeout = null;

    public boolean isOneShot() {
        return oneShot;
    }
    public boolean getTriggerOn() {
        return triggerOn;
    }
    public boolean isFollowTrigger() {
        return followTrigger;
    }
    public Long getDeactivateTimeout() {
        return deactivateTimeout;
    }




    public AbstractTarget oneShot(boolean oneShot) {
        this.oneShot = oneShot;
        return this;
    }

    public AbstractTarget triggerState(boolean triggerOn) {
        this.triggerOn = triggerOn;
        return this;
    }

    public AbstractTarget followTrigger(boolean followTrigger) {
        this.followTrigger = followTrigger;
        return this;
    }

    public AbstractTarget clearAfter(Long timeout) {
        this.deactivateTimeout = timeout;
        return this;
    }

    ////////
    // ID //
    ////////

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
