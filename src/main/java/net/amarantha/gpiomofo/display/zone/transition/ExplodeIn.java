package net.amarantha.gpiomofo.display.zone.transition;

public class ExplodeIn extends Explode {

    @Override
    public void reset() {
        super.reset();
        spacing = maxSpacing;
    }

    @Override
    protected void updateSpacing() {
        spacing--;
    }

}
