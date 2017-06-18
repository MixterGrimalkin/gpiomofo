package net.amarantha.gpiomofo.display.zone.transition;

public class RainIn extends AbstractTransition {

    @Override
    public void reset() {
        currentRow = getPattern().getHeight() - 1;
        currentRandomLimit = 0.6;
        minRandomLimit = 0.0;
        fallStart = 0;
        fallStartDelta = (getRestY() / getPattern().getHeight()) * 0.6;
        randomLimitDelta = ((currentRandomLimit - minRandomLimit) / (getPattern().getHeight())) * 1.1 ;
    }

    @Override
    public int getNumberOfSteps() {
        return getPattern().getHeight();
    }

    @Override
    public void animate(double progress) {
        clear();
        for (int r = (int)fallStart; r < getRestY()+getPattern().getHeight(); r++ ) {
            if ( r < (getRestY()+currentRow) ) {
                for (int c = 0; c < getPattern().getWidth(); c++) {
                    if ( Math.random() <= currentRandomLimit) {
                        draw(c + getRestX(), r, getPattern().rgb(currentRow, c));
                    }
                }
            } else {
                for (int c = 0; c < getPattern().getWidth(); c++) {
                    draw(c+getRestX(), r, getPattern().rgb(r-getRestY(), c));
                }
            }
        }
        if ( currentRandomLimit >= minRandomLimit ) {
            currentRandomLimit -= randomLimitDelta;
        }
        currentRow--;
        fallStart += fallStartDelta;
    }

    private int currentRow ;

    private double fallStart;
    private double fallStartDelta;

    private double currentRandomLimit;
    private double minRandomLimit;
    private double randomLimitDelta;

}
