package net.amarantha.gpiomofo.pixeltape.matrix;

import net.amarantha.utils.colour.RGB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.amarantha.gpiomofo.utility.Constants.X;
import static net.amarantha.gpiomofo.utility.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFlip;

public class Butterflies extends Animation {

    private Map<Integer, RGB> colours;

    private int[] targetJitter;

    public void init(int spriteCount, Map<Integer, RGB> colours, int tailLength) {
        this.colours = colours;
        targetJitter = new int[]{matrix.width() / 2, 3};
        sprites.setTailLength(tailLength);
        for (int i = 0; i < colours.size(); i++) {
            for (int j = 0; j < spriteCount / colours.size(); j++) {
                sprites.create(i, colours.get(i));
            }
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void refresh() {
        sprites.forEach(Sprite::updatePosition);
        matrix.clear();
        if (sprites.tailLength() > 0) {
            sprites.forEach(s -> {
                for (int i = 0; i < sprites.tailLength(); i++) {
                    matrix.draw(s.tailPos[i][X], s.tailPos[i][Y], s.tailColours[i]);
                }
            });
        }
        sprites.forEach(s -> matrix.draw(s.real[X], s.real[Y], s.colour));
    }

    @Override
    public void onFocusAdded(int focusId) {
        targetSprites();
    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {
        targetSprites();

    }

    public void targetOn(int x, int y) {
        sprites.forEach((s) -> {
            s.targetOn(
                    x + randomFlip(randomBetween(0, targetJitter[X])),
                    y + randomFlip(randomBetween(0, targetJitter[Y]))
            );
        });
    }

    private void targetSprites() {
        if (matrix.foci().isEmpty()) {
            randomize();
        } else {
            sprites.forEach((sprite) -> {
                Integer[] target = randomFocus(sprite);
                sprite.targetOn(
                        target[X] + randomFlip(randomBetween(0, targetJitter[X])),
                        target[Y] + randomFlip(randomBetween(0, targetJitter[Y]))
                );
            });
        }
    }

    public Integer[] randomFocus(Sprite sprite) {
        List<Integer[]> coords = new ArrayList<>(matrix.foci().values());
        if (coords.size() < colours.size() && matrix.foci().keySet().contains(sprite.preferredFocus)) {
            return matrix.foci().get(sprite.preferredFocus);
        } else {
            return coords.get(randomBetween(0, coords.size() - 1));

        }
    }

    public void randomize() {
        sprites.forEach((s) -> s.randomize(1.0));
    }
}
