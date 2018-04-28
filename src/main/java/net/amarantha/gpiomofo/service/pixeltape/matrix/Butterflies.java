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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.PI;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFlip;
import static net.amarantha.utils.math.MathUtils.randomFrom;

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

    private boolean useAudio = true;
    public void setUseAudio(boolean useAudio) {this.useAudio = useAudio;}
    private int winCount;
    public void setWinCount(int winCount) {this.winCount = winCount;}
    private int[] targetJitter;
    public void setTargetJitter(int x, int y) {this.targetJitter = new int[]{ x, y };}

    private Map<Integer, RGB> colours;
    private Map<Integer, List<Butterfly>> colourGroups = new HashMap<>();

    public void init(int spriteCount, Map<Integer, RGB> colours, int tailLength) {
        this.colours = colours;
        props.injectPropertiesOrExit(this);
        sprites.setTailLength(tailLength);
        for (int i = 0; i < colours.size(); i++) {
            for (int j = 0; j < spriteCount / colours.size(); j++) {
                Butterfly b = sprites.create(i, colours.get(i));
                b.init();
                List<Butterfly> group = colourGroups.get(i);
                if ( group == null ) {
                    colourGroups.put(i, group = new ArrayList<>());
                }
                group.add(b);
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
        if ( useAudio ) {
            osc.send(backgroundSoundStop);
        }
        sprites.forEach(Sprite::reset);
    }

    private int foreground = 2;
    private int background = 1;

    @Override
    public void refresh() {
        updateFoci();
        if ( !payoff ) {
            if (foci.isEmpty()) {
                guard.every(2000, "RandomizeUngroupedButterflies", () -> sprites.forEach((s) -> s.randomize(0.2)));
            } else {
                guard.every(5000, "RandomizeGroupedButterflies", () ->
                    sprites.forEach((s) -> {
                        if (randomBetween(0.0, 1.0) <= 0.03) {
                            s.randomize(1.0);
                        } else {
                            Integer[] target = foci.get(s.getGroup());
                            if (target != null) {
                                s.targetOn(
                                        target[X] + randomFlip(randomBetween(0, targetJitter[X])),
                                        target[Y] + randomFlip(randomBetween(0, targetJitter[Y]))
                                );
                            }
                        }
                    })
                );
            }
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
    private boolean payoff = false;

    private int[] fieldCentre = { 20, 20 };
    private int[] ringRadiusRange = { 9, 17 };
    private int[] ringWidthRange = { 0, 3 };

    @Override
    public void onFocusAdded(int focusId) {
        if ( foci.size() >= winCount ) {
            payoff = true;
            colourGroups.forEach((i, group) -> {
                int ringRadius = randomBetween(ringRadiusRange[0], ringRadiusRange[1]);
                final boolean clockwise = randomFlip(1) > 0;
                group.forEach(butterfly->{
                    butterfly.targetOn(fieldCentre[X], fieldCentre[Y]);
                    butterfly.targetRadiusOn(ringRadius + randomFlip(randomBetween(ringWidthRange[0], ringWidthRange[1])));
                    butterfly.setdTheta(clockwise ? -0.1 : 0.1);
                });
            });
        } else {
            payoff = false;
            Map<Integer, List<Butterfly>> targetGroups = getTargetGroups();
            targetGroups.get(null).forEach(butterfly -> butterfly.setGroup(focusId));
            double prob = 1.0 / (foci.size());
            sprites.forEach((butterfly -> {
                if (randomBetween(0.0, 1.0) <= prob) {
                    butterfly.setGroup(focusId);
                }
            }));
            targetSprites();
            if (useAudio && audioActive) {
                if (focusId == 2) {
                    osc.send(centreFlutter);
                } else {
                    osc.send(tentFlutter);
                }
            }
        }
    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {
        if ( payoff && foci.size() < winCount) {
            sprites.forEach(((butterfly) -> {
                butterfly.randomizeRadius();
                butterfly.randomizeAngularSpeed();
            }));
            payoff = false;
        }
        Map<Integer, List<Butterfly>> targetGroups = getTargetGroups();
        targetGroups.forEach((id, sprites)->{
            if ( foci.get(id)==null ) {
                sprites.forEach(butterfly->{
                    if ( foci.isEmpty() ) {
                        butterfly.ungroup();
                    } else {
                        butterfly.setGroup(randomFrom(foci.keySet()));
                    }
                });
            }
        });
        targetSprites();
    }

//    public void targetOn(int x, int y) {
//        sprites.forEach((s) -> {
//            s.targetOn(
//                    x + randomFlip(randomBetween(0, targetJitter[X])),
//                    y + randomFlip(randomBetween(0, targetJitter[Y]))
//            );
//        });
//    }

    private void targetSprites() {
        sprites.forEach((butterfly) -> {
            Integer[] target = foci.get(butterfly.getGroup());
            if (target != null) {
                butterfly.targetOn(
                        target[X] + randomFlip(randomBetween(0, targetJitter[X])),
                        target[Y] + randomFlip(randomBetween(0, targetJitter[Y]))
                );
            } else {
                butterfly.randomize(1.0);
            }
        });
    }

    private Map<Integer, List<Butterfly>> getTargetGroups() {
        Map<Integer, List<Butterfly>> result = new HashMap<>();
        result.put(null, new ArrayList<>());
        foci.forEach((id, coords)-> result.put(id, new ArrayList<>()));
        sprites.forEach((butterfly) -> {
            List<Butterfly> group = result.get(butterfly.getGroup());
            if (group==null ) {
                butterfly.ungroup();
                result.get(null).add(butterfly);
            } else {
                group.add(butterfly);
            }
        });
        return result;
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
        sprites.forEach((s) -> {
            s.ungroup();
            s.randomize(1.0);
        });
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
