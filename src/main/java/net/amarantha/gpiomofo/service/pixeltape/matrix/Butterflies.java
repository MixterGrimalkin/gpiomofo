package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.time.TimeGuard;

import java.util.*;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFlip;

public class Butterflies extends Animation {

    @Inject private TimeGuard guard;

    private Map<Integer, RGB> colours;

    private int[] targetJitter;

    public void init(int spriteCount, Map<Integer, RGB> colours, int tailLength) {
        this.colours = colours;
        boolean wide = surface.width() >= surface.height();
        targetJitter = new int[]{surface.width() / (wide ? colours.size()*2 : 2), surface.height() / (wide ? 2 : colours.size()*2 )};
        sprites.setTailLength(tailLength);
        for (int i = 0; i < colours.size(); i++) {
            for (int j = 0; j < spriteCount / colours.size(); j++) {
                sprites.create(i, colours.get(i));
            }
        }
        randomize();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private int foreground = 1;
    private int background = 0;

    @Override
    public void refresh() {
        updateFoci();
        if (foci.isEmpty()) {
            guard.every(2000, "RandomizeButterflies", () -> sprites.forEach((s) -> s.randomize(0.2)));
        }
        List<int[]> usedPositions = new ArrayList<>(sprites.get().size());
        sprites.forEach((s) -> usedPositions.add(s.updatePosition(usedPositions)));
        surface.clear();
        sprites.forEach(s -> {
            for (int i = 0; i < sprites.tailLength(); i++) {
                surface.layer(background).draw(s.tailPos[i][X], s.tailPos[i][Y], s.tailColours[i]);
            }
        });
        sprites.forEach(s -> {
            surface.layer(foreground).draw(s.real[X], s.real[Y], s.colour);
        });
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
        if (foci.isEmpty()) {
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
        List<Integer[]> coords = new ArrayList<>(foci.values());
        if (foci.keySet().contains(sprite.preferredFocus)) {
            return foci.get(sprite.preferredFocus);
        } else {
            return coords.get(randomBetween(0, coords.size() - 1));

        }
    }

    public void randomize() {
        sprites.forEach((s) -> s.randomize(1.0));
    }

    //////////
    // Foci //
    //////////

    private Map<Integer, Integer[]> foci = new HashMap<>();
    private Map<Integer, Long> cancelledFoci = new HashMap<>();
    private long lingerTime = 1000;

    public void setLingerTime(long lingerTime) {
        this.lingerTime = lingerTime;
    }

    public void addFocus(int id, int x, int y) {
        cancelledFoci.remove(id);
        foci.put(id, new Integer[]{x, y});
        onFocusAdded(id);
    }

    public void removeFocus(int id) {
        cancelledFoci.put(id, System.currentTimeMillis());
    }

    Map<Integer, Integer[]> foci() {
        return foci;
    }

    private void updateFoci() {
        List<Integer> fociToRemove = new ArrayList<>();
        cancelledFoci.forEach((id, time) -> {
            if (System.currentTimeMillis() - time >= lingerTime) {
                fociToRemove.add(id);
            }
        });
        if (!fociToRemove.isEmpty()) {
            fociToRemove.forEach((id) -> {
                cancelledFoci.remove(id);
                foci.remove(id);
            });
            onFocusRemoved(fociToRemove);
        }
    }


}
