package net.amarantha.gpiomofo.core.factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.core.target.Target;
import net.amarantha.gpiomofo.core.trigger.Trigger;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class LinkFactory {

    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;
    @Inject private PrintStream out;

    public LinkFactory link(Trigger trigger, Target... targets) {
        for ( Target target : targets ) {
            trigger.onFire(target::processTrigger);
            out.println("[" + trigger.getName() + "]-->[" + target.getName() + "]");
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

}
