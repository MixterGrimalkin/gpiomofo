package net.amarantha.gpiomofo.target;

import java.util.*;

public class ChainedTarget extends Target {

    @Override
    protected void onActivate() {
        activateTarget(0);
    }

    @Override
    protected void onDeactivate() {
        stopTimer();
        for ( int i=0; i<nextId; i++ ) {
            componentTargets.get(i).deactivate();
        }
    }

    private Timer activationTimer;

    private void activateTarget(final int i) {
//        System.out.println("Firing Next " + i);
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

}
