package net.amarantha.gpiomofo.trigger;


import java.util.LinkedList;
import java.util.List;

public class Trigger {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.replaceAll(" ", "-");
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

    @Override
    public String toString() {
        return name;
    }

}
