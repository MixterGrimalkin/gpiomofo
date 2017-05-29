package net.amarantha.gpiomofo.display.zone.transition;

import net.amarantha.gpiomofo.display.entity.Pattern;

public abstract class Interlace extends AbstractTransition {

    protected int maxShift;

    protected int shift;
    protected double shiftDelta;

    private boolean goLeft;

    protected double acceleration = 0.8;

    @Override
    public int getNumberOfSteps() {
        return (int)(getDuration() / zone.getTick());
//        return (int)(getDuration() / maxShift);
    }

    @Override
    public void reset() {
        maxShift = zone.getWidth();
        goLeft = Math.random() > 0.5;
    }

    @Override
    public void animate(double progress) {
//        System.out.println("shift=" + shift + ", prog=" +progress + ", steps=" + getNumberOfSteps());
        zone.clear();
        Pattern pattern = new Pattern(zone.getPattern().getHeight(), zone.getPattern().getWidth() + (Math.abs(shift) * 2));
        for (int r = 0; r < zone.getPattern().getHeight(); r++) {
            for (int c = 0; c < zone.getPattern().getWidth(); c++) {
                pattern.draw(r, c + (goLeft ? 0 : Math.abs(shift) * 2), zone.getPattern().rgb(r, c));
            }
            goLeft = !goLeft;
        }
        zone.drawPattern(zone.getRestX() - Math.abs(shift), zone.getRestY(), pattern);
        shift += (int)shiftDelta;
        shiftDelta += acceleration;
    }

}
