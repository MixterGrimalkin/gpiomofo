package net.amarantha.gpiomofo.display.zone.transition;

public class ScrollOut extends Scroll {

    @Override
    public void reset() {
        long steps = getNumberOfSteps();
        x = zone.getRestX();
        y = zone.getRestY();
        switch (edge) {
            case LEFT:
                deltaX = speed!=null ? -speed : Math.floor((x + zone.getPattern().getWidth()) / -steps);
                deltaY = 0;
                break;
            case RIGHT:
                deltaX = speed!=null ? speed : Math.ceil((zone.getWidth() - x) / steps);
                deltaY = 0;
                break;
            case TOP:
                deltaX = 0;
                deltaY = speed!=null ? -speed : Math.floor((y + zone.getPattern().getHeight()) / -steps);
                break;
            case BOTTOM:
                deltaX = 0;
                deltaY = speed!=null ? speed : Math.ceil((y + zone.getPattern().getHeight()) / steps);
                break;
            case NONE:
                break;
        }
    }

    @Override
    protected boolean isComplete() {
        switch (edge){
            case LEFT:
                return x <= -1 * zone.getPattern().getWidth();
            case RIGHT:
                return x >= zone.getRight();
            case TOP:
                return y <= -1 * zone.getPattern().getHeight();
            case BOTTOM:
                return y >= zone.getBottom();
            case NONE:
                break;
        }
        return true;
    }

}
