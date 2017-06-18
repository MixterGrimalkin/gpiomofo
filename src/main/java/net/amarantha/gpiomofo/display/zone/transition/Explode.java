package net.amarantha.gpiomofo.display.zone.transition;


import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.utils.colour.RGB;

public abstract class Explode extends AbstractTransition {

    protected int maxSpacing = 30;

    protected int spacing = 0;

    @Override
    public void reset() { }

    @Override
    public int getNumberOfSteps() {
        return maxSpacing;
    }

    @Override
    public void animate(double progress) {
        int xSpacing = spacing;
        int ySpacing = spacing;
        if ( getPattern()!=null ) {
            Pattern exploded = new Pattern(getPattern().getHeight() * (ySpacing + 1), getPattern().getWidth() * (xSpacing + 1));
            for (int r = 0; r < getPattern().getHeight(); r++) {
                for (int c = 0; c < getPattern().getWidth(); c++) {
                    int xJitter = 0;//(int)Math.round((Math.random()-0.5) * (spacing/3));
                    int yJitter = 0;//(int)Math.round((Math.random()-0.5) * (spacing/3));
                    int x = r * (ySpacing + 1) + xJitter;
                    int y = c * (xSpacing + 1) + yJitter;
                    RGB colour = getPattern().rgb(r, c);
                    exploded.draw(x, y, colour);
                }
            }
            int explodedX = getRestX() + (getPattern().getWidth() / 2) - (exploded.getWidth() / 2);
            int explodedY = getRestY() + (getPattern().getHeight() / 2) - (exploded.getHeight() / 2);
            clear();
            draw(explodedX, explodedY, exploded);
        }
        updateSpacing();
    }

    protected abstract void updateSpacing();

}
