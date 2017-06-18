package net.amarantha.gpiomofo.display.zone.transition;

public class ScrollIn extends Scroll {

    @Override
    public void reset() {
        long steps = getNumberOfSteps();
        switch (edge) {
            case LEFT:
                x = -getPattern().getWidth();
                y = getRestY();
                deltaX = speed!=null ? speed : Math.ceil((getRestX() - x) / steps);
                deltaY = 0;
                break;
            case RIGHT:
                x = getWidth();
                y = getRestY();
                deltaX = speed!=null ? -speed : Math.floor((x - getRestX()) / -steps);
                deltaY = 0;
                break;
            case TOP:
                x = getRestX();
                y = -getPattern().getHeight();
                deltaX = 0;
                deltaY = speed!=null ? speed : Math.ceil((getRestY() - y) / steps);
                break;
            case BOTTOM:
                x = getRestX();
                y = getHeight();
                deltaX = 0;
                deltaY = speed!=null ? -speed : Math.floor((y - getRestY()) / -steps);
                break;
            case NONE:
                break;
        }
    }

    @Override
    protected boolean isComplete() {
        switch (edge){
            case LEFT:
                return x >= getRestX();
            case RIGHT:
                return x <= getRestX();
            case TOP:
                return y >= getRestY();
            case BOTTOM:
                return y <= getRestY();
            case NONE:
                break;
        }
        return true;
    }

}
