package net.amarantha.gpiomofo.trigger;


import java.util.LinkedList;
import java.util.List;

public class Trigger {

    public void fire(boolean active) {
        System.out.println("["+getName()+"] " + (active?"==>>":"----"));
        for ( TriggerCallback callback : triggerCallbacks) {
            callback.onTrigger(active);
        }
    }

    ///////////////
    // Callbacks //
    ///////////////

    private List<TriggerCallback> triggerCallbacks = new LinkedList<>();

    public void onFire(TriggerCallback triggerCallback) {
        triggerCallbacks.add(triggerCallback);
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
    public String toString() {
        return name;
    }

}
