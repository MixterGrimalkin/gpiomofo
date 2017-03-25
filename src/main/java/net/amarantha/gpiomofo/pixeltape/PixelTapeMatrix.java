package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.utility.TimeGuard;
import net.amarantha.gpiomofo.utility.Utility;
import net.amarantha.utils.colour.RGB;

import java.util.*;

import static java.lang.Math.PI;
import static java.lang.Math.random;
import static net.amarantha.utils.math.MathUtils.*;

@Singleton
public class PixelTapeMatrix {

    @Inject
    private NeoPixel neoPixel;
    @Inject
    private TaskService tasks;

    private int width;
    private int height;

    public void init(int width, int height, boolean alternateRows) {

        this.width = width;
        this.height = height;

        Utility.log("Starting Pixel Tape Matrix " + width + " x " + height);

        pixels = new RGB[width][height];

        neoPixel.init(width * height);

        tasks.addRepeatingTask("Matrix", 100, this::refresh);


    }

    public PixelTapeMatrix addSprite(RGB colour) {
        sprites.add(new Sprite(colour));
        return this;
    }

    private void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = RGB.BLACK;
            }
        }
    }

    @Inject private TimeGuard guard;

    private long decayTime = 1000;

    private void refresh() {
        List<Integer> fociToRemove = new ArrayList<>();
        cancelledFoci.forEach((id, time)->{
            if ( System.currentTimeMillis() - time >= decayTime ) {
                fociToRemove.add(id);
            }
        });
        if ( !fociToRemove.isEmpty() ) {
            fociToRemove.forEach((id) -> {
                cancelledFoci.remove(id);
                foci.remove(id);
            });
            targetSprites();
        }

        sprites.forEach(Sprite::updatePosition);
        clear();
        if ( tailLength > 0 ) {
            sprites.forEach(s -> {
                for (int i = 0; i < tailLength; i++) {
                    if (s.tailPos[i][X] >= 0 && s.tailPos[i][X] < width && s.tailPos[i][Y] >= 0 && s.tailPos[i][Y] < height) {
                        pixels[s.tailPos[i][X]][s.tailPos[i][Y]] = s.tail[i];
                    }
                }
            });
        }
        sprites.forEach(s -> {
            if ( s.real[X] >=0 && s.real[X] < width && s.real[Y] >= 0 && s.real[Y] < height ) {
                pixels[s.real[X]][s.real[Y]] = s.colour;
            }
        });
//        foci.forEach((id,coord)-> pixels[coord[X]][coord[Y]] = RGB.WHITE);
        int pixel = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                neoPixel.setPixelColourRGB(pixel++, pixels[x][y]);
            }
        }
        neoPixel.render();
    }

    private int[] targetJitter = { width/2, 3 };

    public void setTailLength(int tailLength) {
        this.tailLength = tailLength;
    }

    public void targetOn(int x, int y) {
        sprites.forEach((s) -> {
            s.targetOn(
                    x + randomFlip(randomBetween(0, targetJitter[X])),
                    y + randomFlip(randomBetween(0, targetJitter[Y]))
            );
        });
    }

    private Map<Integer, Integer[]> foci = new HashMap<>();
    private Map<Integer, Long> cancelledFoci = new HashMap<>();

    public void addFocus(int id, int x, int y) {
        foci.put(id, new Integer[] { x, y });
        targetSprites();
    }

    public void removeFocus(int id) {
        cancelledFoci.put(id, System.currentTimeMillis());
    }

    private void targetSprites() {
        if ( foci.isEmpty() ) {
            randomize();
        } else {
            sprites.forEach((sprite) -> {
                Integer[] target = randomFocus();
                sprite.targetOn(
                        target[X] + randomFlip(randomBetween(0, targetJitter[X])),
                        target[Y] + randomFlip(randomBetween(0, targetJitter[Y]))
                );
            });
        }
    }

    public Integer[] randomFocus() {
        List<Integer[]> coords = new ArrayList<>(foci.values());
        return coords.get(randomBetween(0, coords.size()-1));
    }

    public void randomize() {
        sprites.forEach((s)->s.randomize(1.0));
    }

    private RGB[][] pixels;

    private List<Sprite> sprites = new LinkedList<>();

    private int tailLength = 3;

    private static final int X = 0;
    private static final int Y = 1;

    class Sprite {
        double[] bounds = { width, height };
        int[][] tailPos = new int[tailLength][2];
        RGB[] tail = new RGB[tailLength];
        double[] current = { 0, 0 };
        double[] target  = { 0, 0 };
        double[] delta = { 0, 0 };
        double linearSpeed;
        double theta;
        double radius;
        double dTheta;
        int[] real = { 0, 0 };
        RGB colour;

        Sprite(RGB colour) {
            this.colour = colour;
            current[X] = width / 2;
            current[Y] = height / 2;
            theta = 0;
            randomize(1.0);
            updateDelta();
            for ( int i=0; i<tailLength; i++ ) {
                tail[i] = RGB.BLACK;
                tailPos[i] = new int[]{ -1, -1 };
            }
        }

        void storeTail() {
            if ( tailLength > 0 ) {
                for (int i = tailLength - 1; i > 0; i--) {
                    tail[i] = tail[i - 1].withBrightness(0.8 - (1.0 / tailLength));
                    tailPos[i] = tailPos[i - 1];
                }
                tail[0] = colour;
                tailPos[0] = new int[]{real[X], real[Y]};
            }
        }

        void randomize(double probability) {
            if ( random() < probability ) {
                target[X] = randomBetween(0, width-1);
                target[Y] = randomBetween(0, height-1);
                linearSpeed = randomBetween(2, 25);
                radius = randomBetween(0.0, 5.0);
                dTheta = randomFlip(randomBetween(0.1, PI/8));
            }
        }

        void targetOn(int tx, int ty) {
            target[X] = bound(0, width-1, tx);
            target[Y] = bound(0, height-1, ty);
            updateDelta();
        }

        void updateDelta() {
            delta[X] = (target[X] - current[X]) / linearSpeed;
            delta[Y] = (target[Y] - current[Y]) / linearSpeed;
        }

        void updateAxis(int axis) {
            if ( target[axis] != current[axis] ) {
                current[axis] += delta[axis];
            }
            if ( current[axis] < 0 ) {
                current[axis] = 0;
                delta[axis] = -delta[axis];
            } else if ( current[axis] >= bounds[axis] ) {
                current[axis] = bounds[axis]-1;
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
            if ( theta < 0.0 ) {
                theta = 2*PI;
            } else if ( theta > 2*PI ) {
                theta = 0.0;
            }
        }

    }


}
