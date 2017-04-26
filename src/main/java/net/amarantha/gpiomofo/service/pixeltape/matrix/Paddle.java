package net.amarantha.gpiomofo.service.pixeltape.matrix;

import net.amarantha.utils.colour.RGB;

import java.util.List;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.round;

public class Paddle extends Animation {

    private int delta;
    private int[] position = {0,0};
    private int[] target = {0,0};
    private int axis = X;
    private int paddleSize = 5;
    private int fieldSize;

    @Override
    public void start() {
        fieldSize = axis ==X ? surface.width() : surface.height();
    }

    @Override
    public void stop() {

    }

    public void setPosition(double value) {
        target[axis] = round((1-value)*fieldSize);
        delta = (target[axis] - position[axis])/5;
    }

    @Override
    public void refresh() {
        updatePosition();
        surface.clear();
        for ( int i=0; i<paddleSize; i++ ) {
            surface.layer(0).draw(position[X]+(axis ==X?i:0), position[Y]+(axis ==Y?i:0), RGB.GREEN);
        }
    }

    private void updatePosition() {
        position[axis] = target[axis];
//        if ( ( delta > 0 && position[axis] >= target[axis] ) || ( delta < 0 && position[axis] <= target[axis] ) ) {
//            position[axis] = target[axis];
//            delta = 0;
//        } else {
//            position[axis] += delta;
//        }
    }

    @Override
    public void onFocusAdded(int focusId) {

    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {

    }
}
