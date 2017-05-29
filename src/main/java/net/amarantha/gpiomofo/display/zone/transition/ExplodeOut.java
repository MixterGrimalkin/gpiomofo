package net.amarantha.gpiomofo.display.zone.transition;

public class ExplodeOut extends Explode {

    @Override
    public void reset() {
        super.reset();
        spacing = 0;
    }

    @Override
    protected void updateSpacing() {
        spacing++;
    }

}
