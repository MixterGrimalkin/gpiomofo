package net.amarantha.gpiomofo.core.target;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChainedTarget extends Target {

    @Override
    protected void onActivate() {
        activateTarget(0);
    }

    @Override
    protected void onDeactivate() {
        stopTimer();
        if ( offTarget!=null ) {
            offTarget.activate();
        }
        for ( int i=0; i<nextId; i++ ) {
            componentTargets.get(i).deactivate(true);
        }
    }

    private Timer activationTimer;
    private boolean repeat;

    private void activateTarget(final int i) {
        if ( i < nextId ) {
            Target target = componentTargets.get(i);
            Integer delay = targetDelays.get(i);
            target.activate();
            if ( delay==null ) {
                activateTarget(i+1);
            } else {
                stopTimer();
                activationTimer = new Timer();
                activationTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        activateTarget(i+1);
                    }
                }, delay);
            }
        } else {
            if ( repeat ) {
                activateTarget(0);
            }
        }
    }

    private void stopTimer() {
        if ( activationTimer!=null ) {
            activationTimer.cancel();
            activationTimer = null;
        }
    }

    public ChainedTarget addTarget(Integer delay, Target... targets) {
        for ( Target target : targets ) {
            componentTargets.put(nextId, target);
            targetDelays.put(nextId, delay);
            nextId++;
        }
        return this;
    }

    private int nextId = 0;

    private Map<Integer, Target> componentTargets = new HashMap<>();
    private Map<Integer, Integer> targetDelays = new HashMap<>();

    @Override
    public Target oneShot(boolean oneShot) {
        super.oneShot(oneShot);
        for ( Target target : componentTargets.values() ) {
            target.oneShot(oneShot);
        }
        return this;
    }

    @Override
    public Target triggerState(boolean triggerState) {
        super.triggerState(triggerState);
        for ( Target target : componentTargets.values() ) {
            target.triggerState(triggerState);
        }
        return this;
    }

    @Override
    public Target followTrigger(boolean followTrigger) {
        super.followTrigger(followTrigger);
        for ( Target target : componentTargets.values() ) {
            target.followTrigger(followTrigger);
        }
        return this;
    }

    @Override
    public Target clearDelay(Long clearDelay) {
        super.clearDelay(clearDelay);
        for ( Target target : componentTargets.values() ) {
            target.clearDelay(clearDelay);
        }
        return this;
    }

    public Target repeat(boolean repeat) {
        return repeat(repeat, null);
    }

    public Target repeat(boolean repeat, Target offTarget) {
        this.repeat = repeat;
        this.offTarget = offTarget;
        if ( offTarget!=null ) {
            offTarget.oneShot(true);
        }
        return this;

    }

}