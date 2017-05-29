package net.amarantha.gpiomofo.display.zone.transition;

public class InterlaceOut extends Interlace {

    @Override
    public void reset() {
        super.reset();
        shift = 0;
        shiftDelta = Math.ceil(maxShift / getNumberOfSteps());
        acceleration = 0.8;
    }

}
