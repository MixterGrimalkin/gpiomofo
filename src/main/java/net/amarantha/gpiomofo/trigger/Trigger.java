package net.amarantha.gpiomofo.trigger;


import net.amarantha.gpiomofo.factory.HasName;

import java.util.LinkedList;
import java.util.List;

public class Trigger implements HasName {

    public void fire(boolean active) {
        System.out.println("["+getName()+"] " + (active?"==>>":" -- "));
        for ( TriggerCallback callback : triggerCallbacks) {
            callback.onTrigger(active);
        }
        for ( TriggerCallback callback : compositeCallbacks) {
            callback.onTrigger(active);
        }
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
