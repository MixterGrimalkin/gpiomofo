package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Sprite;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.time.TimeGuard;

import java.util.*;
import java.util.Map.Entry;

import static java.lang.Math.pow;
import static java.lang.Math.random;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.*;

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
                Butterfly b = sprites.create(colours.get(i));
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
        sprites.forEach(Sprite::reset);
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
        if ( !payoff && !linearMode ) {
            if (foci.isEmpty()) {
                guard.every(2000, "RandomizeUngroupedButterflies", () -> sprites.forEach((s) -> s.randomize(0.2)));
            } else {
                guard.every(10000, "RandomizeGroupedButterflies", () ->
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
        if ( linearMode ) {
            guard.every(3000, "Linearize", ()-> linearize(0.2));
        }
        surface.layer(background).clear();
        surface.layer(foreground).clear();
        sprites.forEach(s -> {
            s.update();
            for (int i = 0; i < sprites.tailLength(); i++) {
                surface.layer(background).draw(s.tailPos[i][X], s.tailPos[i][Y], s.tailColours[i]);
            }
            surface.layer(foreground).draw(s.real[X], s.real[Y], s.colour);
        });
        if ( winStarted!=null ) {
            if (payoff) {
                if (System.currentTimeMillis() - winStarted >= 30_000) {
                    linearMode(true);
                    winStarted = System.currentTimeMillis();
                }
            } else {
                if (System.currentTimeMillis() - winStarted >= 60_000) {
                    linearMode(false);
                    winStarted = null;
                }
            }
        }
    }

    private void linearize(double prob) {
        sprites.forEach(butterfly -> {
            if (random() < prob) {
                butterfly.setDecelerate(false);
                if (random() < 0.5) {
                    butterfly.setDelta(1.0, 0.0);
                } else {
                    butterfly.setDelta(0.0, 1.0);
                }
            }
        });
    }

    private boolean linearMode = false;

    private OscCommand tentFlutter;
    private OscCommand centreFlutter;
    private OscCommand exitSound;

    private boolean audioActive = false;
    private boolean payoff = false;

    private int[] fieldCentre = { 20, 20 };
    private int[] ringRadiusRange = { 9, 17 };
    private int[] ringWidthRange = { 0, 3 };

    public void linearMode(boolean mode) {
        linearMode = mode;
        if ( mode ) {
            sprites.forEach((butterfly -> {
                butterfly.enableEntropy(false);
                butterfly.targetRadiusOn(0);
                butterfly.setGroup(null);
            }));

        } else {
            sprites.forEach((butterfly -> {
                butterfly.enableEntropy(true);
                butterfly.setDecelerate(true);
                butterfly.randomizeRadius();
            }));
        }
    }

    private Long winStarted;

    @Override
    public void onFocusAdded(int focusId) {
        if ( !linearMode ) {
            if (foci.size() >= winCount) {
                if ( !payoff ) {
                    winStarted = System.currentTimeMillis();
                }
                payoff = true;
                colourGroups.forEach((i, group) -> {
                    int ringRadius = randomBetween(ringRadiusRange[0], ringRadiusRange[1]);
                    final boolean clockwise = randomFlip(1) > 0;
                    group.forEach(butterfly -> {
                        butterfly.targetOn(fieldCentre[X], fieldCentre[Y]);
                        butterfly.targetRadiusOn(ringRadius + randomFlip(randomBetween(ringWidthRange[0], ringWidthRange[1])));
                        butterfly.setAngularSpeed(clockwise ? -0.1 : 0.1);
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
    }

    private double calculateDistance(int[] c1, Integer[] c2) {
        return Math.sqrt(pow(c1[X] - c2[X], 2) + pow(c1[Y] - c2[Y], 2));
    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {
        if ( payoff && foci.size() < winCount) {
            sprites.forEach(((butterfly) -> {
                butterfly.randomizeRadius();
                Double lastDistance = null;
                for (Entry<Integer, Integer[]> entry : foci.entrySet() ) {
                    double distance = calculateDistance(butterfly.real, entry.getValue());
                    if ( lastDistance==null || lastDistance > distance ) {
                        lastDistance = distance;
                        butterfly.setGroup(entry.getKey());
                    }
                }
            }));
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!payoff) {
                        sprites.forEach(Butterfly::randomizeAngularSpeed);
                    }
                }
            }, 2000);
            payoff = false;
        }
        if ( !linearMode ) {
            Map<Integer, List<Butterfly>> targetGroups = getTargetGroups();
            targetGroups.forEach((id, sprites) -> {
                if (foci.get(id) == null) {
                    sprites.forEach(butterfly -> {
                        if (foci.isEmpty()) {
                            butterfly.setGroup(null);
                        } else {
                            butterfly.setGroup(randomFrom(foci.keySet()));
                        }
                    });
                }
            });
            targetSprites();
        }
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
                butterfly.setGroup(null);
                result.get(null).add(butterfly);
            } else {
                group.add(butterfly);
            }
        });
        return result;
    }

    public void randomize() {
        sprites.forEach((s) -> {
            s.setGroup(null);
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
