package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Sprite;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.time.TimeGuard;

import java.util.*;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFlip;

@PropertyGroup("Butterflies")
public class Butterflies extends Animation {

    @Inject private PropertiesService props;
    @Inject private OscService osc;
    @Inject private TimeGuard guard;

    @Property("AudioPlayerIP") private String playerIp;
    @Property("AudioPlayerPort") private int playerPort;
    @Property("FlutterInSound") private String flutterInSoundFilename;
    @Property("FlutterOutSound") private String flutterOutSoundFilename;
    @Property("BackgroundSound") private String backgroundSoundFilename;
    private OscCommand backgroundSoundStart;
    private OscCommand backgroundSoundStop;

    private Map<Integer, RGB> colours;

    private int[] targetJitter;

    public void setTargetJitter(int[] targetJitter) {
        this.targetJitter = targetJitter;
    }

    public void init(int spriteCount, Map<Integer, RGB> colours, int tailLength) {
        this.colours = colours;
        props.injectPropertiesOrExit(this);
        boolean wide = surface.width() >= surface.height();
        targetJitter = new int[]{surface.width() / (wide ? colours.size()*2 : 2), surface.height() / (wide ? 2 : colours.size()*2 )};
        sprites.setTailLength(tailLength);
        for (int i = 0; i < colours.size(); i++) {
            for (int j = 0; j < spriteCount / colours.size(); j++) {
                sprites.create(i, colours.get(i)).init();
            }
        }
        backgroundSoundStart = new OscCommand(playerIp, playerPort, backgroundSoundFilename+"/loop");
        backgroundSoundStop = new OscCommand(playerIp, playerPort, backgroundSoundFilename+"/stop");
        tentFlutter = new OscCommand(playerIp, playerPort, "windy-tent/play");
        centreFlutter = new OscCommand(playerIp, playerPort, "centre-sound/play");
        exitSound = new OscCommand(playerIp, playerPort, "exit-sound/play");
        reset();
        randomize();
    }

    public void reset() {
        sprites.forEach((s)->{
            s.reset();
        });
    }

    @Override
    public void start() {
        audioActive = true;
        osc.send(backgroundSoundStart);
    }

    @Override
    public void stop() {
        audioActive = false;
        osc.send(backgroundSoundStop);
        sprites.forEach(Sprite::reset);
    }

    private int foreground = 2;
    private int background = 1;

    @Override
    public void refresh() {
        updateFoci();
        if (foci.isEmpty()) {
            guard.every(2000, "RandomizeButterflies", () -> sprites.forEach((s) -> s.randomize(0.2)));
        }
        List<int[]> usedPositions = new ArrayList<>(sprites.get().size());
        sprites.forEach((s) -> usedPositions.add(s.updatePosition(usedPositions)));
        surface.layer(background).clear();
        surface.layer(foreground).clear();
        sprites.forEach(s -> {
            for (int i = 0; i < sprites.tailLength(); i++) {
                surface.layer(background).draw(s.tailPos[i][X], s.tailPos[i][Y], s.tailColours[i]);
            }
        });
        sprites.forEach(s -> {
            surface.layer(foreground).draw(s.real[X], s.real[Y], s.colour);
        });
    }

    private OscCommand tentFlutter;
    private OscCommand centreFlutter;
    private OscCommand exitSound;

    private boolean audioActive = false;

    @Override
    public void onFocusAdded(int focusId) {
        targetSprites();
        if ( audioActive ) {
            if (focusId == 2) {
                osc.send(centreFlutter);
            } else {
                osc.send(tentFlutter);
            }
        }
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
            if ( audioActive ) {
                osc.send(exitSound);
            }
        } else {
            sprites.forEach((oldSprite) -> {
                Integer[] target = randomFocus(oldSprite);
                oldSprite.targetOn(
                        target[X] + randomFlip(randomBetween(0, targetJitter[X])),
                        target[Y] + randomFlip(randomBetween(0, targetJitter[Y]))
                );
            });
        }
    }

    public Integer[] randomFocus(Butterfly oldSprite) {
        List<Integer[]> coords = new ArrayList<>(foci.values());
        if (foci.keySet().contains(oldSprite.preferredFocus)) {
            return foci.get(oldSprite.preferredFocus);
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
