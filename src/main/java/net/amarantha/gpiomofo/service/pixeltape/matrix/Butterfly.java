package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Sprite;
import net.amarantha.utils.colour.RGB;

import static java.lang.Math.PI;
import static java.lang.Math.random;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.*;

public class Butterfly extends Sprite {

    RGB colour;
    int[] real = {0, 0};

    private boolean enableEntropy = true;

    private int[] fieldSize = {0, 0};
    private double[] current = {0, 0};
    private double[] target = {0, 0};
    private double[] delta = {0, 0};
    private double linearSpeed;

    int[][] tailPos;
    RGB[] tailColours;
    private final int tailLength;

    private double radius;
    private double dRadius;
    private double targetRadius;

    private double theta;
    private double dTheta;

    private Integer group;

    @Inject
    public Butterfly(LightSurface surface) {
        this(RGB.GREEN, surface.width(), surface.height(), 5);
    }

    Butterfly(RGB colour, int width, int height, int tailLength) {
        this.colour = colour;
        fieldSize = new int[]{ width, height };
        tailPos = new int[tailLength][2];
        this.tailLength = tailLength;
        tailColours = new RGB[tailLength];
        current[X] = fieldSize[X] / 2;
        current[Y] = fieldSize[Y] / 2;
        theta = 0;
        randomize(1.0);
        calculateLinearDelta();
        for (int i = 0; i < tailLength; i++) {
            tailColours[i] = RGB.BLACK;
            tailPos[i] = new int[]{-1, -1};
        }
    }

    void randomize(double probability) {
        if (enableEntropy && random() < probability) {
            target[X] = randomBetween(0, fieldSize[X] - 1);
            target[Y] = randomBetween(0, fieldSize[Y] - 1);
            linearSpeed = randomBetween(10.0, 25.0);
            randomizeRadius();
            randomizeAngularSpeed();
        }
    }

    void randomizeRadius() {
        if ( enableEntropy ) targetRadiusOn(randomBetween(0, 5));
    }

    void targetRadiusOn(double newRadius) {
        targetRadius = newRadius;
        dRadius = (targetRadius - radius) / linearSpeed;
    }

    void randomizeAngularSpeed() {
        if ( enableEntropy ) setAngularSpeed(randomFlip(randomBetween(0.1, PI / 8)));
    }

    void setAngularSpeed(double dTheta) {
        this.dTheta = dTheta;
    }

    void targetOn(int tx, int ty) {
        target[X] = bound(0, fieldSize[X] - 1, tx);
        target[Y] = bound(0, fieldSize[Y] - 1, ty);
        calculateLinearDelta();
    }

    private void calculateLinearDelta() {
        delta[X] = (target[X] - current[X]) / linearSpeed;
        delta[Y] = (target[Y] - current[Y]) / linearSpeed;
    }

    private boolean decelerate = true;

    public void setDecelerate(boolean decelerate) {
        this.decelerate = decelerate;
    }

    @Override
    public void update() {
        updateLinearPosition(X);
        updateLinearPosition(Y);
        updateRadius();
        real[X] = round(current[X] + (Math.sin(theta) * radius));
        real[Y] = round(current[Y] + (Math.cos(theta) * radius));
        angularBounce(X);
        angularBounce(Y);
        updateTheta();
        if ( decelerate ) {
            calculateLinearDelta();
        }
        storeTail();
    }

    @Override
    public void updateLinearPosition(int axis) {
        if (!decelerate || target[axis] != current[axis]) {
            current[axis] += delta[axis];
        }
        if (current[axis] < 0) {
            current[axis] = 0;
            delta[axis] = -delta[axis];
        } else if (current[axis] >= fieldSize[axis]) {
            current[axis] = fieldSize[axis] - 1;
            delta[axis] = -delta[axis];
        }
    }

    private void updateRadius() {
        radius += dRadius;
        if ( ( dRadius > 0.0 && radius >= targetRadius ) || ( dRadius < 0.0 && radius <= targetRadius ) ) {
            radius = targetRadius;
            dRadius = 0.0;
        }
    }

    private void angularBounce(int axis) {
        if ( real[axis] < 0 ) {
            real[axis] = 0;
            dTheta = -dTheta;
        } else if ( real[axis] >= fieldSize[axis] ) {
            real[axis] = fieldSize[axis] - 1;
            dTheta = -dTheta;
        }
    }

    private void updateTheta() {
        theta += dTheta;
        if (theta < 0.0) {
            theta = 2 * PI;
        } else if (theta > 2 * PI) {
            theta = 0.0;
        }
    }

    private void storeTail() {
        if (tailLength > 0) {
            for (int i = tailLength - 1; i > 0; i--) {
                tailColours[i] = tailColours[i - 1].withBrightness(0.8 - (1.0 / tailLength));
                tailPos[i] = tailPos[i - 1];
            }
            tailColours[0] = colour;
            tailPos[0] = new int[]{real[X], real[Y]};
        }
    }

    @Override
    public void doRender() {}

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public void enableEntropy(boolean enableEntropy) {
        this.enableEntropy = enableEntropy;
    }

    public void setDelta(double x, double y) {
        delta = new double[]{ x, y };
    }
}
