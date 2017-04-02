package net.amarantha.gpiomofo.core.trigger;


import com.google.inject.Inject;
import net.amarantha.gpiomofo.core.factory.HasName;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.utils.time.Now;

import java.util.LinkedList;
import java.util.List;

import static net.amarantha.gpiomofo.service.shell.Utility.log;

public class Trigger implements HasName {

    private boolean lastState;

    @Inject private TaskService tasks;
    @Inject private Now now;

    public void fire(boolean active) {
        lastState = active;
        if ( holdTime==null ) {
            doFire(active);
        } else {
            if ( active ) {
                tasks.addTask("hold-"+getName(), holdTime, () -> doFire(true));
            } else {
                tasks.removeTask("hold-"+getName());
            }
        }
    }

    private void doFire(boolean active) {
        log(now.time().toString() + ": [" + getName() + "] " + (active ? "==>>" : " -- "));
        for (TriggerCallback callback : triggerCallbacks) {
            callback.onTrigger(active);
        }
        for (TriggerCallback callback : compositeCallbacks) {
            callback.onTrigger(active);
        }
    }

    private Integer holdTime;

    public int getHoldTime() {
        return holdTime==null ? 0 : holdTime;
    }

    public Trigger setHoldTime(int holdTime) {
        this.holdTime = holdTime;
        return this;
    }

    public boolean isActive() {
        return lastState;
    }

    ///////////////
    // Callbacks //
    ///////////////

    private List<TriggerCallback> triggerCallbacks = new LinkedList<>();
    private List<TriggerCallback> compositeCallbacks = new LinkedList<>();

    public void onFire(TriggerCallback triggerCallback) {
        triggerCallbacks.add(triggerCallback);
    }

    public void onFireComposite(TriggerCallback triggerCallback) {
        compositeCallbacks.add(triggerCallback);
    }

    public interface TriggerCallback {
        void onTrigger(boolean active);
    }

    //////////
    // Name //
    //////////

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name.replaceAll(" ", "-");
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trigger trigger = (Trigger) o;
        return name != null ? name.equals(trigger.name) : trigger.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
