package net.amarantha.gpiomofo.service.pixeltape.matrix.sprites;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.entity.Point;
import net.amarantha.gpiomofo.display.entity.Region;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.utils.colour.RGB;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.round;

public abstract class Sprite {

    private Point centre = new Point(0, 0);

    private Region bounds;
    private Region size;

    public Sprite() {
        id = nextId++;
    }

    private List<MotionMaker> motionMakers = new LinkedList<>();

    public void updatePosition() {
        if (!motionMakers.isEmpty() ) {
            Point newCentre = motionMakers.get(0).currentCentre();
            for (MotionMaker mm : motionMakers) {
                newCentre = mm.updateCentre(newCentre);
            }
            centre = newCentre;
        }
    }

    public void init() {

    }

    public void reset() {

    }

    public void start() {

    }

    public void stop() {

    }

    public void render() {

    }

    private int collisionRadius = 0;

    public int getCollisionRadius() {
        return collisionRadius;
    }

    public void setCollisionRadius(int collisionRadius) {
        this.collisionRadius = collisionRadius;
    }

    public Point getCentre() {
        return centre;
    }

    private static int nextId = 0;

    private final int id;

    public int getId() {
        return id;
    }

    private int layer = 0;

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }



    public double distanceTo(Sprite other) {
        Point thisCentre = getCentre();
        Point otherCentre = other.getCentre();
        return Math.sqrt(Math.pow(thisCentre.x()-otherCentre.x(), 2) + (Math.pow(thisCentre.y()-otherCentre.y(), 2)));
    }

}
