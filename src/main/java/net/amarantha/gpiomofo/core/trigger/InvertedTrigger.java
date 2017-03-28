package net.amarantha.gpiomofo.core.trigger;

public class InvertedTrigger extends Trigger {

    private Trigger innerTrigger;

    @Override
    public void fire(boolean active) {
        super.fire(active);
        if ( !active && innerTrigger!=null ) {
            innerTrigger.fire(true);
        }
    }

    public InvertedTrigger trigger(Trigger innerTrigger) {
        this.innerTrigger = innerTrigger;
        innerTrigger.onFire(this::fire);
        return this;
    }
}
