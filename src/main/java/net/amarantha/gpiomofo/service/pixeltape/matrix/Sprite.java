package net.amarantha.gpiomofo.service.pixeltape.matrix;

import net.amarantha.utils.colour.RGB;

import static java.lang.Math.PI;
import static java.lang.Math.random;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.*;

class Sprite {

    private final int width;
    private final int height;
    private final int tailLength;

    int[][] tailPos;
    RGB[] tailColours;

    private int[] bounds = {0, 0};
    private double[] current = {0, 0};
    private double[] target = {0, 0};
    private double[] delta = {0, 0};
    private double linearSpeed;
    private double theta;
    private double radius;
    private double dTheta;
    int[] real = {0, 0};
    RGB colour;
    int preferredFocus = 0;


    Sprite(int preferredFocus, RGB colour, int width, int height, int tailLength) {
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
            linearSpeed = randomBetween(2.0, 25.0);
            radius = randomBetween(0.0, 5.0);
            dTheta = randomFlip(randomBetween(0.1, PI / 8));
        }
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

    void updateAxis(int axis) {
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

    void updatePosition() {
        updateAxis(X);
        updateAxis(Y);
        real[X] = round(current[X] + (Math.sin(theta) * radius));
        real[Y] = round(current[Y] + (Math.cos(theta) * radius));
        updateAngle();
        updateDelta();
        storeTail();
    }

    void updateAngle() {
        theta += dTheta;
        if (theta < 0.0) {
            theta = 2 * PI;
        } else if (theta > 2 * PI) {
            theta = 0.0;
        }
    }

}
