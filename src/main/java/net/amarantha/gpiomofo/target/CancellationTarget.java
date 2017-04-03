package net.amarantha.gpiomofo.target;

public class CancellationTarget extends Target {

    @Override
    protected void onActivate() {
        if ( targetToCancel!=null ) {
            targetToCancel.deactivate(force);
        }
    }

    @Override
    protected void onDeactivate() {

    }

    private Target targetToCancel;

    public CancellationTarget cancel(Target t) {
        this.targetToCancel = t;
        return this;
    }

    private boolean force;

    public CancellationTarget setForce(boolean force) {
        this.force = force;
        return this;
    }
}
