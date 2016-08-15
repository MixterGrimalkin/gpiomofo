package net.amarantha.gpiomofo.trigger;

import net.amarantha.gpiomofo.target.Target;

import java.util.LinkedList;
import java.util.List;

public class RangeTrigger extends Trigger {

    private List<Range> ranges = new LinkedList<>();

    private Target currentTarget;

    public void fireTriggers(double value) {
        for ( Range range : ranges ) {
            if ( range.isInRange(value) ) {
                activateTarget(range.target);
            }
        }
    }

    private void activateTarget(Target target) {
        if ( !target.equals(currentTarget) ) {
            if ( currentTarget!=null ) {
                currentTarget.deactivate();
            }
            target.activate();
            currentTarget = target;
        }
    }

    public RangeTrigger addRange(double min, double max, Target target) {
        ranges.add(new Range(min, max, target));
        return this;
    }

    private class Range {
        private final Target target;
        private final double min;
        private final double max;
        public Range(double min, double max, Target target) {
            this.min = min;
            this.max = max;
            this.target = target;
        }
        private boolean isInRange(double value) {
            return min<=value && value<max;
        }
    }




}
