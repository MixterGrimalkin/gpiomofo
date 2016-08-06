package net.amarantha.gpiomofo.target;

public class QueueResetTarget extends Target {

    @Override
    protected void onActivate() {
        if ( queuedTarget!=null ) {
            queuedTarget.resetQueue();
        }

    }

    @Override
    protected void onDeactivate() {

    }

    private QueuedTarget queuedTarget;

    public QueueResetTarget queuedTarget(QueuedTarget t) {
        this.queuedTarget = t;
        return this;
    }
}
