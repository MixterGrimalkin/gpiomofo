package net.amarantha.gpiomofo.display.zone.transition;

public class RainIn extends AbstractTransition {

    @Override
    public void reset() {
        currentRow = zone.getPattern().getHeight() - 1;
        currentRandomLimit = 0.6;
        minRandomLimit = 0.0;
        fallStart = 0;
        fallStartDelta = (zone.getRestY() / zone.getPattern().getHeight()) * 0.6;
        randomLimitDelta = ((currentRandomLimit - minRandomLimit) / (zone.getPattern().getHeight())) * 1.1 ;
    }

    @Override
    public int getNumberOfSteps() {
        return zone.getPattern().getHeight();
    }

    @Override
    public void animate(double progress) {
        zone.clear();
        for (int r = (int)fallStart; r < zone.getRestY()+zone.getPattern().getHeight(); r++ ) {
            if ( r < (zone.getRestY()+currentRow) ) {
                for (int c = 0; c < zone.getPattern().getWidth(); c++) {
                    if ( Math.random() <= currentRandomLimit) {
                        zone.drawPoint(c + zone.getRestX(), r, zone.getPattern().rgb(currentRow, c));
                    }
                }
            } else {
                for (int c = 0; c < zone.getPattern().getWidth(); c++) {
                    zone.drawPoint(c+zone.getRestX(), r, zone.getPattern().rgb(r-zone.getRestY(), c));
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
