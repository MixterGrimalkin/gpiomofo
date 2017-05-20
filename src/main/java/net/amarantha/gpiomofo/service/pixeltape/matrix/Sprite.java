package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.max;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.round;

public abstract class Sprite {

    @Inject protected LightSurface surface;

    protected int[] minBound = {0, 0};
    protected int[] maxBound = {0, 0};

    protected int[] position = {0, 0};

    protected double[] exactPosition = {0.0, 0.0};
    protected double[] linearDelta = {0.0, 0.0};

    protected int layer = 0;
    protected RGB colour = RGB.WHITE;

    public void init() {
        setBounds(0, 0, surface.width()-1, surface.height()-1);
    }

    public void setBounds(int minX, int minY, int maxX, int maxY) {
        minBound[X] = minX;
        minBound[Y] = minY;
        maxBound[X] = maxX;
        maxBound[Y] = maxY;
    }

    protected boolean inBounds(int x, int y) {
        return inBoundsOnAxis(X, x) && inBoundsOnAxis(Y, y);
    }

    protected boolean inBoundsOnAxis(int axis, int position) {
        return position >= minBound[axis] && position <= maxBound[axis];
    }

    void updatePosition() {
        updateAxis(X);
        updateAxis(Y);
    }

    void setLinearDelta(double dX, double dY) {
        linearDelta = new double[]{ dX, dY };
    }

    void setLinearDeltaAxis(int axis, double delta) {
        linearDelta[axis] = delta;
    }

    void setAngularDelta(double angle, double delta) {
        setLinearDeltaAxis(X, Math.sin(angle) * delta);
        setLinearDeltaAxis(Y, Math.cos(angle) * delta);
    }

    synchronized void updateAxis(int axis) {
        double newPos = exactPosition[axis] + linearDelta[axis];
        if ( newPos <= minBound[axis] ) {
            newPos = minBound[axis];
            exactPosition[axis] = newPos;
            position[axis] = round(exactPosition[axis]);
            bounce(axis, false);
        } else if ( newPos >= maxBound[axis] ) {
            newPos = maxBound[axis];
            exactPosition[axis] = newPos;
            position[axis] = round(exactPosition[axis]);
            bounce(axis, true);
        } else {
            exactPosition[axis] = newPos;
            position[axis] = round(exactPosition[axis]);
        }
    }

    synchronized void setPosition(int x, int y) {
        position = new int[]{ x, y };
        exactPosition = new double[]{ x, y };
    }

    void setPositionAxis(int axis, int pos) {
        exactPosition[axis] = position[axis] = pos;

    }

    protected void bounce(int axis, boolean max) {
        linearDelta[axis] = -linearDelta[axis];
    }

    protected void render() {
        surface.layer(layer).draw(position[X], position[Y], colour);
    }


    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setColour(RGB colour) {
        this.colour = colour;
    }







}
