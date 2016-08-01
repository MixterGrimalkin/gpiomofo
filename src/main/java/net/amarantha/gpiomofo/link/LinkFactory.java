package net.amarantha.gpiomofo.link;

import com.google.inject.Singleton;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

@Singleton
public class LinkFactory {

    public LinkFactory link(Trigger trigger, Target... targets) {
        for ( Target target : targets ) {
            trigger.onFire(target::processTrigger);
            System.out.println("[" + trigger.getName() + "]-->[" + target.getName() + "]");
        }
        return this;
    }

}
