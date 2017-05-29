package net.amarantha.gpiomofo.service.pixeltape.matrix.sprites;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.utils.colour.RGB;

import java.util.function.BiConsumer;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.round;

public abstract class Sprite {

    private static int nextId = 0;

    private final int id;

    @Inject protected LightSurface surface;

    private int[] minBound = {0, 0};
    private int[] maxBound = {0, 0};

    private int[] position = {0, 0};

    private double[] exactPosition = {0.0, 0.0};
    private double[] linearDelta = {0.0, 0.0};

    protected int layer = 0;
    protected RGB colour = RGB.WHITE;

    public Sprite() {
        id = nextId++;
    }

    public int getId() {
        return id;
    }

    public int[] getCentre() {
        return new int[]{ position[X], position[Y] };
    }

    public int getCollisionRadius() {
        return 2;
    }

    public double distanceTo(Sprite other) {
        int[] thisCentre = getCentre();
        int[] otherCentre = other.getCentre();
        return Math.sqrt(Math.pow(thisCentre[X]-otherCentre[X], 2) + (Math.pow(thisCentre[Y]-otherCentre[Y], 2)));
    }

    public void init() {
        setBounds(0, 0, surface.width()-1, surface.height()-1);
    }

    public void reset() {}

    public void start() {}

    public void stop() {}

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

    public void setLinearDelta(double dX, double dY) {
        linearDelta = new double[]{ dX, dY };
    }

    public double[] getLinearDelta() {
        return new double[]{ linearDelta[X], linearDelta[Y] };
    }

    public void setLinearDeltaAxis(int axis, double delta) {
        linearDelta[axis] = delta;
    }

    public void setAngularDelta(double angle, double delta) {
        setLinearDeltaAxis(X, Math.sin(angle) * delta);
        setLinearDeltaAxis(Y, Math.cos(angle) * delta);
    }

    public void updatePosition() {
        updateAxis(X);
        updateAxis(Y);
    }

    public synchronized void updateAxis(int axis) {
        double newPos = exactPosition[axis] + linearDelta[axis];
        Boolean bounceType = null;
        if ( newPos <= minBound[axis] ) {
            newPos = minBound[axis];
            bounceType = false;
        } else if ( newPos >= maxBound[axis] ) {
            newPos = maxBound[axis];
            bounceType = true;
        }
        exactPosition[axis] = newPos;
        position[axis] = round(exactPosition[axis]);
        if ( bounceType!=null ) {
            bounce(axis, bounceType);
        }
    }

    public synchronized void setPosition(int[] newPosition) {
        position = new int[]{ newPosition[X], newPosition[Y] };
        exactPosition = new double[]{ newPosition[X], newPosition[Y] };
    }

    public synchronized void setPosition(double x, double y) {
        position = new int[]{ round(x), round(y) };
        exactPosition = new double[]{ x, y };
    }

    public synchronized void setPosition(int x, int y) {
        position = new int[]{ x, y };
        exactPosition = new double[]{ x, y };
    }

    public void setPositionAxis(int axis, int pos) {
        exactPosition[axis] = position[axis] = pos;
    }

    private BiConsumer<Integer, Boolean> bounceCallback;

    public void onBounce(BiConsumer<Integer, Boolean> bounceCallback) {
        this.bounceCallback = bounceCallback;
    }

    protected void bounce(int axis, boolean max) {
        if ( bounceCallback!=null ) {
            bounceCallback.accept(axis, max);
        }
        linearDelta[axis] = -linearDelta[axis];
    }

    private boolean visible = false;

    public final void render() {
        if ( visible ) {
            doRender();
        }
    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
    }

    public abstract void doRender();

    public int getLayer() {
        return layer;
    }

    public Sprite setLayer(int layer) {
        this.layer = layer;
        return this;
    }

    public Sprite setColour(RGB colour) {
        this.colour = colour;
        return this;
    }

    public RGB getColour() {
        return colour;
    }

    public int[] position() {
        return position;
    }

    public int position(int axis) {
        return position[axis];
    }
}
