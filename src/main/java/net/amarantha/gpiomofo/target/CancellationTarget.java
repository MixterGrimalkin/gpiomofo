package net.amarantha.gpiomofo.target;

public class CancellationTarget extends Target {

    @Override
    protected void onActivate() {
        if ( targetToCancel!=null ) {
            targetToCancel.deactivate();
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
}
