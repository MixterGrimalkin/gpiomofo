package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import java.util.*;

import static net.amarantha.utils.shell.Utility.log;

@Singleton
public class LinkFactory {

    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;

    private Map<Trigger, List<Target>> links = new HashMap<>();

    public LinkFactory link(Trigger trigger, Target... targets) {
        for ( Target target : targets ) {
            trigger.onFire(target::processTrigger);
            List<Target> currentTargets = links.get(trigger);
            if ( currentTargets==null ) {
                links.put(trigger, currentTargets = new LinkedList<>());
            }
            currentTargets.add(target);
        }
        return this;
    }

    public LinkFactory link(String triggerName, String... targetNames) {
        Trigger trigger = triggers.get(triggerName.replaceAll(" ", "-"));
        List<Target> allTargets = new LinkedList<>();
        for ( String s : targetNames ) {
            allTargets.add(targets.get(s.replaceAll(" ", "-")));
        }
        return link(trigger, allTargets.toArray(new Target[allTargets.size()]));
    }

    public LinkFactory lock(long lockTime, Target... targets) {
        for ( Target t : targets ) {
            t.lock(lockTime, targets);
        }
        return this;
    }

    public LinkFactory lock(long lockTime, String... targetNames) {
        List<Target> ts = new LinkedList<>();
        for ( String name : targetNames ) {
            ts.add(targets.get(name));
        }
        return lock(lockTime, ts.toArray(new Target[ts.size()]));
    }

    public Map<Trigger, List<Target>> getLinks() {
        return links;
    }
}
