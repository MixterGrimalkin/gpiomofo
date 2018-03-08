package net.amarantha.gpiomofo.display.zone.transition;

public class ScrollOut extends Scroll {

    @Override
    public void reset() {
        long steps = getNumberOfSteps();
        x = getRestX();
        y = getRestY();
        switch (edge) {
            case LEFT:
                deltaX = speed!=null ? -speed : Math.floor((x + getPattern().getWidth()) / -steps);
                deltaY = 0;
                break;
            case RIGHT:
                deltaX = speed!=null ? speed : Math.ceil((getWidth() - x) / steps);
                deltaY = 0;
                break;
            case TOP:
                deltaX = 0;
                deltaY = speed!=null ? -speed : Math.floor((y + getPattern().getHeight()) / -steps);
                break;
            case BOTTOM:
                deltaX = 0;
                deltaY = speed!=null ? speed : Math.ceil((y + getPattern().getHeight()) / steps);
                break;
            case NONE:
                break;
        }
    }

    @Override
    protected boolean isComplete() {
        switch (edge){
            case LEFT:
                return x <= -1 * getPattern().getWidth();
            case RIGHT:
                return x >= getWidth();
            case TOP:
                return y <= -1 * getPattern().getHeight();
            case BOTTOM:
                return y >= getHeight();
            case NONE:
                break;
        }
        return true;
    }

}
