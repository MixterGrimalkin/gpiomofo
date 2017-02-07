package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.service.gpio.MPR121;

import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;

/**
 * Adds listeners to a pair of MPR121 pins and provides these triggers:
 * - TapLeft
 * - TapRight
 * - HoldLeft
 * - HoldRight
 * - DblTapLeft
 * - DblTapRight
 * - SwipeLeft
 * - SwipeRight
 */
public class TouchTriggerSet {

    @Inject private MPR121 mpr121;

    @Inject private Trigger tapLeftTrigger;
    @Inject private Trigger tapRightTrigger;
    @Inject private Trigger holdLeftTrigger;
    @Inject private Trigger holdRightTrigger;
    @Inject private Trigger dblTapLeftTrigger;
    @Inject private Trigger dblTapRightTrigger;
    @Inject private Trigger swipeLeftTrigger;
    @Inject private Trigger swipeRightTrigger;

    private static final int LEFT = -1;
    private static final int RIGHT = 1;

    private Map<Integer, Boolean> currentStates = new HashMap<>();
    private Map<Integer, Long> lastChanged = new HashMap<>();
    private Map<Integer, Timer> timers = new HashMap<>();
    private Map<Integer, Boolean> cancelNextRelease = new HashMap<>();

    public TouchTriggerSet() {
        currentStates.put(LEFT, false);
        currentStates.put(RIGHT, false);
        lastChanged.put(LEFT, currentTimeMillis());
        lastChanged.put(RIGHT, currentTimeMillis());
        cancelNextRelease.put(LEFT, false);
        cancelNextRelease.put(RIGHT, false);
    }

    public TouchTriggerSet setPins(int left, int right) {
        mpr121.addListener(left, this::processLeft);
        mpr121.addListener(right, this::processRight);
        return this;
    }

    ////////////////
    // Main Logic //
    ////////////////

    private void processLeft(boolean state) {
        process(state, LEFT, tapLeftTrigger, dblTapLeftTrigger, holdLeftTrigger, swipeLeftTrigger);
    }

    private void processRight(boolean state) {
        process(state, RIGHT, tapRightTrigger, dblTapRightTrigger, holdRightTrigger, swipeRightTrigger);
    }

    private void process(boolean state, int side, Trigger tap, Trigger dblTap, Trigger hold, Trigger swipe) {

        if ( state ) {
            if ( currentStates.get(-side) || currentTimeMillis()-lastChanged.get(-side) <= swipeTime ) {
                swipe.fire(true);
                cancelNextRelease.put(-side, currentStates.get(-side));
                cancelNextRelease.put(side, true);
            } else {
                startTimer(side).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        hold.fire(true);
                    }
                }, holdTime);
            }
        } else {
            cancelTimer(side);
            if ( !cancelNextRelease.get(side) ) {
                if (currentTimeMillis() - lastChanged.get(side) < holdTime) {
                    tap.fire(true);
                }
            }
            cancelNextRelease.put(side, false);
        }

        currentStates.put(side, state);
        lastChanged.put(side, currentTimeMillis());
    }

    private Timer startTimer(int side) {
        Timer timer = new Timer();
        timers.put(side, timer);
        return timer;
    }

    private void cancelTimer(int side) {
        Timer timer = timers.get(side);
        if ( timer!=null ) {
            timer.cancel();
        }
    }

    ////////////
    // Config //
    ////////////


    public int getHoldTime() {
        return holdTime;
    }

    public void setHoldTime(int holdTime) {
        this.holdTime = holdTime;
    }

    public int getSwipeTime() {
        return swipeTime;
    }

    public void setSwipeTime(int swipeTime) {
        this.swipeTime = swipeTime;
    }

    private int holdTime = 700;
    private int swipeTime = 150;

    ///////////////////////
    // Trigger Accessors //
    ///////////////////////

    public Trigger getTapLeftTrigger() {
        return tapLeftTrigger;
    }

    public Trigger getTapRightTrigger() {
        return tapRightTrigger;
    }

    public Trigger getHoldLeftTrigger() {
        return holdLeftTrigger;
    }

    public Trigger getHoldRightTrigger() {
        return holdRightTrigger;
    }

    public Trigger getDblTapLeftTrigger() {
        return dblTapLeftTrigger;
    }

    public Trigger getDblTapRightTrigger() {
        return dblTapRightTrigger;
    }

    public Trigger getSwipeLeftTrigger() {
        return swipeLeftTrigger;
    }

    public Trigger getSwipeRightTrigger() {
        return swipeRightTrigger;
    }

}
