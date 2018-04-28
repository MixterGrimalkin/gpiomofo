package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Sprite;
import net.amarantha.utils.colour.RGB;

import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.random;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.*;

public class Butterfly extends Sprite {

    private final int width;
    private final int height;
    private final int tailLength;



    int[][] tailPos;
    RGB[] tailColours;

    private int[] bounds = {0, 0};
    private double[] current = {0, 0};
    private double[] target = {0, 0};
    private double[] delta = {0, 0};
    private double[] offset = {0, 0};
    private double linearSpeed;
    private double theta;

    private double radius;
    private double dRadius;
    private double targetRadius;

    private double dTheta;
    int[] real = {0, 0};
    RGB colour;
    int preferredFocus = 0;
    private Integer group;

    public double getdTheta() {
        return dTheta;
    }

    public void setdTheta(double dTheta) {
        this.dTheta = dTheta;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Integer getGroup() {
        return group;
    }

    public void ungroup() {
        setGroup(null);
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    @Inject
    public Butterfly(LightSurface surface) {
        this(0, RGB.GREEN, surface.width(), surface.height(), 5);
    }

    Butterfly(int preferredFocus, RGB colour, int width, int height, int tailLength) {
        this.colour = colour;
        this.preferredFocus = preferredFocus;
        this.width = width;
        this.height = height;
        this.tailLength = tailLength;
        bounds = new int[]{ width, height };
        tailPos = new int[tailLength][2];
        tailColours = new RGB[tailLength];
        current[X] = width / 2;
        current[Y] = height / 2;
        theta = 0;
        randomize(1.0);
        updateDelta();
        for (int i = 0; i < tailLength; i++) {
            tailColours[i] = RGB.BLACK;
            tailPos[i] = new int[]{-1, -1};
        }
    }

    void storeTail() {
        if (tailLength > 0) {
            for (int i = tailLength - 1; i > 0; i--) {
                tailColours[i] = tailColours[i - 1].withBrightness(0.8 - (1.0 / tailLength));
                tailPos[i] = tailPos[i - 1];
            }
            tailColours[0] = colour;
            tailPos[0] = new int[]{real[X], real[Y]};
        }
    }

    void randomize(double probability) {
        if (random() < probability) {
            target[X] = randomBetween(0, width - 1);
            target[Y] = randomBetween(0, height - 1);
            linearSpeed = randomBetween(10.0, 25.0);
            randomizeRadius();
            randomizeAngularSpeed();
        }
    }

    void randomizeRadius() {
        targetRadiusOn(randomBetween(0, 5));
    }

    void randomizeAngularSpeed() {
        dTheta = randomFlip(randomBetween(0.1, PI / 8));
    }

    public void targetRadiusOn(double newRadius) {
        targetRadius = newRadius;
        dRadius = (targetRadius - radius) / linearSpeed;
    }

    void targetOn(int tx, int ty) {
        target[X] = bound(0, width - 1, tx);
        target[Y] = bound(0, height - 1, ty);
        updateDelta();
    }

    void updateDelta() {
        delta[X] = (target[X] - current[X]) / linearSpeed;
        delta[Y] = (target[Y] - current[Y]) / linearSpeed;
    }

    @Override
    public void updateAxis(int axis) {
        if (target[axis] != current[axis]) {
            current[axis] += delta[axis];
        }
        if (current[axis] < 0) {
            current[axis] = 0;
            delta[axis] = -delta[axis];
        } else if (current[axis] >= bounds[axis]) {
            current[axis] = bounds[axis] - 1;
            delta[axis] = -delta[axis];
        }
    }

    @Override
    public void doRender() {

    }

    int[] updatePosition(List<int[]> usedPositions) {
        updateAxis(X);
        updateAxis(Y);
        updateRadius();
        int newX = round(current[X] + (Math.sin(theta) * radius));
        int newY = round(current[Y] + (Math.cos(theta) * radius));
        if ( positionUsed(new int[]{newX, newY}, usedPositions) ) {
//            randomize(1.0);
        } else {
            real[X] = newX;
            real[Y] = newY;
        }
        angularBounce(X);
        angularBounce(Y);
        updateAngle();
        updateDelta();
        storeTail();
        return real;
    }


    private boolean blockCollisions = false;

    boolean positionUsed(int[] newPosition, List<int[]> usedPositions) {
        if ( blockCollisions ) {
            for (int[] position : usedPositions) {
                if (position[X] == newPosition[X] && position[Y] == newPosition[Y]) {
                    return true;
                }
            }
        }
        return false;
    }

    void angularBounce(int axis) {
        if ( real[axis] < 0 ) {
            real[axis] = 0;
            dTheta = -dTheta;
        } else  if ( real[axis] >= bounds[axis] ) {
            real[axis] = bounds[axis] - 1;
            dTheta = -dTheta;
        }

    }

    void updateAngle() {
        theta += dTheta;
        if (theta < 0.0) {
            theta = 2 * PI;
        } else if (theta > 2 * PI) {
            theta = 0.0;
        }
    }

    public void updateRadius() {
        radius += dRadius;
        if ( ( dRadius > 0.0 && radius >= targetRadius ) || ( dRadius < 0.0 && radius <= targetRadius ) ) {
            radius = targetRadius;
            dRadius = 0.0;
        }
    }


}
