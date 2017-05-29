package net.amarantha.gpiomofo.display.zone.transition;

public class InterlaceIn extends Interlace {

    @Override
    public void reset() {
        super.reset();
        shift = maxShift;
        shiftDelta = Math.floor(-(maxShift / getNumberOfSteps()));
        acceleration = -0.8;
    }



}
