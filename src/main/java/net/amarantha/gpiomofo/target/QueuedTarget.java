package net.amarantha.gpiomofo.target;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class QueuedTarget extends Target {

    @Override
    protected void onActivate() {
        if ( targetPointer >= componentTargets.size() ) {
            targetPointer = 0;
        }
        currentTarget = componentTargets.get(targetPointer++);
        if ( currentTarget!=null ) {
            currentTarget.activate();
        }
    }

    @Override
    protected void onDeactivate() {
        if ( currentTarget!=null ) {
            System.out.println("Deactivate " + currentTarget.getName());
            currentTarget.deactivate(true);
        }
    }

    private Target currentTarget;
    private List<Target> componentTargets = new LinkedList<>();
    private int targetPointer = 0;

    public QueuedTarget addTargets(Target... targets) {
        componentTargets.addAll(Arrays.asList(targets));
        return this;
    }

    public List<Target> getComponentTargets() {
        return componentTargets;
    }
}
