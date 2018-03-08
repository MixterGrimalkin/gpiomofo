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
        return (int)(getDuration() / 50);
//        return (int)(getDuration() / maxShift);
    }

    @Override
    public void reset() {
        maxShift = getWidth();
        goLeft = Math.random() > 0.5;
    }

    @Override
    public void animate(double progress) {
//        System.out.println("shift=" + shift + ", prog=" +progress + ", steps=" + getNumberOfSteps());
        clear();
        Pattern pattern = new Pattern(getPattern().getHeight(), getPattern().getWidth() + (Math.abs(shift) * 2));
        for (int r = 0; r < getPattern().getHeight(); r++) {
            for (int c = 0; c < getPattern().getWidth(); c++) {
                pattern.draw(r, c + (goLeft ? 0 : Math.abs(shift) * 2), getPattern().rgb(r, c));
            }
            goLeft = !goLeft;
        }
        draw(getRestX() - Math.abs(shift), getRestY(), pattern);
        shift += (int)shiftDelta;
        shiftDelta += acceleration;
    }

}
