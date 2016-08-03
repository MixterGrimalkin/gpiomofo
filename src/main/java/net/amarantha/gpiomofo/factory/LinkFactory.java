package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import java.util.LinkedList;
import java.util.List;

@Singleton
public class LinkFactory {

    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;

    public LinkFactory link(Trigger trigger, Target... targets) {
        for ( Target target : targets ) {
            trigger.onFire(target::processTrigger);
            System.out.println("[" + trigger.getName() + "]-->[" + target.getName() + "]");
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

}
