package net.amarantha.gpiomofo.display.zone.transition;

import java.util.Map;

public class TypeIn extends AbstractTransition {

    private Map<Integer, Letter> letters;
    private int currentLetter;

    @Override
    public void reset() {
        letters = splitPattern();
        currentLetter = 0;
    }

    @Override
    public int getNumberOfSteps() {
        return letters.size();
    }

    @Override
    public void animate(double progress) {
        clear();
        for ( int i = 0; i <= currentLetter; i++ ) {
            Letter l = letters.get(i);
            draw(getRestX()+l.x, getRestY()+l.y, l.pattern);
        }
        currentLetter++;
    }

}
