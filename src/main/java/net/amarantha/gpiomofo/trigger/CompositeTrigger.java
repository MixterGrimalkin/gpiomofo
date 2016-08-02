package net.amarantha.gpiomofo.trigger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompositeTrigger extends Trigger {

    private List<Trigger> componentTriggers = new LinkedList<>();

    private Map<String, Boolean> triggerStates = new HashMap<>();

    private boolean lastState = false;

    public CompositeTrigger addTriggers(Trigger... triggers) {
        for ( Trigger trigger : triggers ) {
            componentTriggers.add(trigger);
            triggerStates.put(trigger.getName(), false);
            trigger.onFireComposite((state)->triggerFired(trigger, state));
        }
        return this;
    }

    private void triggerFired(Trigger trigger, boolean state) {
        triggerStates.put(trigger.getName(), state);
        if ( allActive() ) {
            fire(lastState = true);
        } else {
            if ( lastState ) {
                fire(lastState = false);
            }
        }
    }

    private boolean allActive() {
        for ( Trigger t : componentTriggers ) {
            if ( !triggerStates.get(t.getName()) ) {
                return false;
            }
        }
        return true;
    }

}
