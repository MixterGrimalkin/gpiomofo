package net.amarantha.gpiomofo.display.zone.transition;


import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.zone.AbstractZone;
import net.amarantha.gpiomofo.display.zone.AbstractZone.ZoneCallback;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractTransition {

    private long delay;
    private long lastDrawn;
    private int currentStep;

    /**
     * Called when transition begins
     */
    public abstract void reset();

    /**
     * @return Number of steps required to complete animation
     */
    public abstract int getNumberOfSteps();

    /**
     * Called <code>numberOfSteps</code> times, during transition is active
     */
    public abstract void animate(double progress);

    /**
     * @return True when the animation is complete
     */
    protected boolean isComplete() {
        return currentStep >= getNumberOfSteps();
    }

    public final void tick() {
        if ( System.currentTimeMillis() - lastDrawn >= delay ) {
            currentStep++;
            if ( zone.getPattern()==null || isComplete() ) {
                zone.clear();
                complete();
            } else {
                double progress = (double)currentStep / (double)getNumberOfSteps();
                if ( !onAtFired && onAt!=null && progress >= onAtProgress ) {
                    onAt.execute();
                    onAtFired = true;
                }
                animate(progress);
            }
            lastDrawn = System.currentTimeMillis();
        }
    }

    /**
     * Activate transition
     * @param zone Zone
     * @param onComplete To execute when transition is complete
     */
    public final void transition(AbstractZone zone, ZoneCallback onComplete, ZoneCallback onAt, double onAtProgress) {
        this.zone = zone;
        this.onComplete = onComplete;
        this.onAt = onAt;
        this.onAtProgress = onAtProgress;
        onAtFired = false;
        reset();
        int steps = ( getNumberOfSteps()==0 ? 1 : getNumberOfSteps() );
        delay = duration / steps;
        lastDrawn = System.currentTimeMillis();
        currentStep = 0;
    }

    protected AbstractZone zone;
    private ZoneCallback onComplete;
    private ZoneCallback onAt;
    private boolean onAtFired = false;
    private double onAtProgress;

    /**
     * Pass control back to the Zone
     */
    protected final void complete() {
        if ( onComplete !=null ) {
            onComplete.execute();
        }
    }

    /**
     * Set approximate duration of transition. Will not be clock accurate.
     * @param duration Value in milliseconds
     * @return For method chaining
     */
    public AbstractTransition setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    private long duration;


    /////////////
    // Utility //
    /////////////

    /**
     * Split the pattern horizontally into blocks (might be letters).
     * This will not work for multi-line text.
     * @return The pattern as a map of letters
     */
    protected Map<Integer, Letter> splitPattern() {

        Pattern pattern = zone.getPattern();

        Map<Integer, Letter> letters = new HashMap<>();

        int letterCount = 0;

        boolean inLetter = false;
        int lastLetterStartX = 0;

        if( pattern!=null ) {
            for (int x = 0; x < pattern.getWidth(); x++) {

                boolean isEmptyCol = true;

                for (int y = 0; y < pattern.getHeight(); y++) {
                    if (pattern.rgb(y, x)!=null) {
                        isEmptyCol = false;
                        if (!inLetter) {
                            inLetter = true;
                            lastLetterStartX = x;
                        }
                        break;
                    }
                }
                if (isEmptyCol) {
                    inLetter = false;
                    letters.put(letterCount, new Letter(pattern.slice(lastLetterStartX, 0, x - lastLetterStartX, pattern.getHeight()), lastLetterStartX, 0));
                    letterCount++;
                }
            }
        }

        if ( inLetter ) {
            letters.put(letterCount, new Letter(pattern.slice(lastLetterStartX, 0, pattern.getWidth()-lastLetterStartX, pattern.getHeight()), lastLetterStartX, 0));
            letterCount++;
        }

        return letters;
    }

    protected static class Letter {
        Pattern pattern;
        int x;
        int y;
        public Letter(Pattern pattern, int x, int y) {
            this.pattern = pattern;
            this.x = x;
            this.y = y;
        }
    }

}
