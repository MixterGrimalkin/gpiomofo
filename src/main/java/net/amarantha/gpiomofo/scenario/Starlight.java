package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixelFactory;
import net.amarantha.gpiomofo.display.pixeltape.Pixel;
import net.amarantha.gpiomofo.display.pixeltape.PixelAnimation;
import net.amarantha.gpiomofo.service.dmx.DmxService;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.http.HttpService;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.Now;
import net.amarantha.utils.time.TimeGuard;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;
import static net.amarantha.utils.math.MathUtils.*;
import static net.amarantha.utils.shell.Utility.log;

public class Starlight extends Scenario {

    @Inject private Now now;
    @Inject private TimeGuard guard;

    @Service private TaskService tasks;
    @Service private DmxService dmx;
    @Service private HttpService http;
    @Service private GpioService gpio;
    @Inject private NeoPixelFactory pixels;
    @Inject private NeoPixel neoPixel;

    @Parameter("ConstellationId")   private String constellationId;
    @Parameter("MonitorHost")       private String monitorHost;
    @Parameter("MonitorPort")       private int monitorPort;
    @Parameter("PingInterval")      private int pingInterval;

    @Parameter("DmxStars")          private boolean dmxStars;
    @Parameter("DmxRings")          private boolean dmxRings;

    @Parameter("PinResistance")     private String resistanceStr;
    @Parameter("TriggerState")      private boolean triggerState;
    @Parameter("StarTriggers")      private String starTriggerStr;

    @Parameter("PixelCount")        private int pixelCount;
    @Parameter("RingPixels")        private String ringPixelStr;
    @Parameter("StarPixels")        private String starPixelStr;

    @Parameter("Clusters")          private String clusterString;

    @Parameter("RingFlashColour")   private RGB ringFlashColour;
    @Parameter("RingPulseColour")   private RGB ringPulseColour;
    @Parameter("RingOnFadeUp")      private int ringOnFadeUp;
    @Parameter("RingOnFadeDown")    private int ringOnFadeDown;
    @Parameter("RingOffFadeDown")   private int ringOffFadeDown;

    @Parameter("LeapFrogStarCount") private int leapFrogStarCount;
    @Parameter("LeapFrogTime")      private int leapFrogTime;
    @Parameter("StarChaseDelay")    private int starChaseDelay;
    @Parameter("StarChaseColour")   private RGB starChaseColour;
    @Parameter("ConnectorChaseDelay")  private int connectorChaseDelay;
    @Parameter("ConnectorChaseColour") private RGB connectorChaseColour;

    @Parameter("FullWinStarCount")  private int fullWinStarCount;
    @Parameter("RingWinColour")     private RGB ringWinColour;
    @Parameter("StarWinColour")     private RGB starWinColour;
    @Parameter("TwinkleColour")     private RGB twinkleColour;
    @Parameter("TwinklePulseMin")   private int minTwinkleTime;
    @Parameter("TwinklePulseMax")   private int maxTwinkleTime;
    @Parameter("TwinkleRange")      private double twinkleRange;
    @Parameter("StarFadeDown")      private int starFadeDown;

    @Parameter("StarFinalColour")   private RGB ringFinalColour;
    @Parameter("FinalColour1")   private RGB starFinalColour1;
    @Parameter("FinalColour2")   private RGB starFinalColour2;
    @Parameter("FinalColour3")   private RGB starFinalColour3;
    @Parameter("FinalColour4")   private RGB starFinalColour4;
    @Parameter("FinalAllColour")   private RGB finalAllColour;
    @Parameter("FinalRingFadeOut") private int wooshRingFadeOut;

    @Parameter("FinalMaxInterval") private long wooshMaxInterval;
    @Parameter("FinalMinInterval") private long wooshMinInterval;
    @Parameter("FinalIntervalDelta") private long wooshIntervalDelta;
    @Parameter("FinalAnimationDuration") private long finalWooshDuration;
    @Parameter("FinalPulseDuration") private long finalPulseDuration;
    @Parameter("FinalPulseMaxDelay") private int finalPulseMaxDelay;
    @Parameter("FinalPulseMinDelay") private int finalPulseMinDelay;
    @Parameter("FinalPulseDeltaFactor") double finalPulseDeltaFactor;

    @Parameter("LoneStarTime") private long loneStarTime;

    @Parameter("TriggerHoldTime") private int triggerHoldTime;

    private long finalStartedAt;
    private boolean finalWinActive = false;

    private long lastWoosh;
    private long wooshInterval;

    private boolean finalPulse = false;
    private int finalPulseDelay;

    private boolean loneStarActive = false;
    private Long loneStarted;

    private Integer[] pins;
    private Integer[] stars;
    private Integer[] rings;
    private Integer[] connectors;

    private long[] lastStarTimes;
    private int[] lastStarNumbs;

    private Map<Integer, Boolean> currentStates = new HashMap<>();
    private Map<Integer, List<Integer>> clusters = new HashMap<>();

    private boolean leapFrogActive = false;

    private Map<Integer, PixelAnimation> animations = new HashMap<>();

    private Map<Integer, RGB> finalColours = new HashMap<>();

    @Override
    public void setup() {

        // Parse config strings
        String[] pinsStrs = starTriggerStr.split(",");
        String[] starStrs = starPixelStr.split(",");
        String[] ringStrs = ringPixelStr.split(",");
        if (pinsStrs.length != starStrs.length || starStrs.length != ringStrs.length) {
            System.out.println("StarTriggers, StarPixels and RingPixels must all be the same length!");
            System.exit(1);
        }
        pins = new Integer[pinsStrs.length];
        stars = new Integer[pinsStrs.length];
        rings = new Integer[pinsStrs.length];
        connectors = new Integer[pixelCount - (stars.length * 2)];

        lastStarTimes = new long[leapFrogStarCount];
        lastStarNumbs = new int[leapFrogStarCount];
        for ( int i=0; i<leapFrogStarCount; i++ ) {
            lastStarNumbs[i] = -1;
        }

        // Create stars and rings
        for (int i = 0; i < pinsStrs.length; i++) {
            pins[i] = parseInt(pinsStrs[i].trim());
            stars[i] = parseInt(starStrs[i].trim());
            rings[i] = parseInt(ringStrs[i].trim());
            final int starNo = i;
            triggers.gpio(
                    "Star" + i,
                    pins[i],
                    PinPullResistance.valueOf(resistanceStr),
                    triggerState
            ).setHoldTime(triggerHoldTime).onFire((state) -> updateState(state, starNo));
            if (dmxRings) neoPixel.intercept(rings[i], dmx.rgbDevice(i * 4).getInterceptor());
            if (dmxStars) neoPixel.intercept(stars[i], dmx.device((i * 4) + 3).getInterceptor());
            currentStates.put(i, false);
        }

        // Create pixels
        int j = 0;
        for (int i = 0; i < pixelCount; i++) {
            pixels.createPixel(i);
            if (!arrayContains(stars, i) && !arrayContains(rings, i)) {
                connectors[j++] = i;
            }
        }

        // Create star clusters
        if ( !clusterString.isEmpty() ) {
            for (String clusterSet : clusterString.split(":")) {
                List<Integer> clusterList = new ArrayList<>();
                for (String star : clusterSet.split(",")) {
                    clusterList.add(parseInt(star.trim()));
                }
                for (Integer star : clusterList) {
                    clusters.put(star, clusterList);
                }
            }
        }

        // Final Win Colours
        finalColours.put(0, starFinalColour1);
        finalColours.put(1, starFinalColour2);
        finalColours.put(2, starFinalColour3);
        finalColours.put(3, starFinalColour4);

        // Create Animations
        animations.put(0, pixels.createAnimation(40, new Woosh(starFinalColour1)));
        animations.put(1, pixels.createAnimation(40, new Woosh(starFinalColour2)));
        animations.put(2, pixels.createAnimation(40, new Woosh(starFinalColour3)));
        animations.put(3, pixels.createAnimation(40, new Woosh(starFinalColour4)));

        neoPixel.init(pixelCount);

    }

    private class Woosh implements Consumer<PixelAnimation> {
        List<Ray> rays = new LinkedList<>();
        private RGB rgb;
        public Woosh(RGB rgb) {
            this.rgb = rgb;
        }
        void addRay(int position, int delta) {
            rays.add(new Ray(position, delta, 3));
        }
        private int raySize = 4;
        @Override
        public void accept(PixelAnimation animation) {
            animation.clear();
            List<Ray> newRays = new LinkedList<>();
            rays.forEach((ray) -> {
                int p = ray.position - (raySize/2);
                animation.fill(p, p+raySize, rgb);
                ray.update();
                if ( ray.wraps > 0 ) {
                    newRays.add(ray);
                }
            });
            rays = newRays;
        }
        void clearRays() {
            rays.clear();
        }
    }

    private class Ray {
        int position;
        int delta;
        int wraps;
        public Ray(int position, int delta, int wraps) {
            this.position = position;
            this.delta = delta;
            this.wraps = wraps;
        }
        void update() {
            position += delta;
            if ( position < 0 ) {
                position = pixelCount-1;
                wraps--;
            }
            if ( position >= pixelCount ) {
                position = 0;
                wraps--;
            }
        }
    }

    private Map<Integer, Boolean> getPinStates() {
        Map<Integer, Boolean> result = new HashMap<>();
        for ( int i=0; i<pins.length; i++ ) {
            result.put(i, gpio.read(pins[i])==triggerState);
        }
        return result;
    }

    private void updateState(boolean state) {
        updateState(state, null);
    }

    private void updateState(boolean state, Integer latestStar) {

        // Read base state
        Map<Integer, Boolean> pinStates = getPinStates();
        Map<Integer, Boolean> newStates = new HashMap<>();
        for ( int i=0; i<pins.length; i++ ) {
            newStates.put(i, pinStates.get(i));
        }

        // Apply clustering
        for ( int i=0; i<pins.length; i++ ) {
            if ( newStates.get(i) ) {
                List<Integer> cluster = clusters.get(i);
                if ( cluster!=null ) {
                    for ( Integer j : cluster ) {
                        newStates.put(j, true);
                    }
                }
            }
        }

        // Count active stars
        int activeStarCount = 0;
        for ( int i=0; i<pins.length; i++ ) {
            activeStarCount += newStates.get(i) ? 1 : 0;
        }

        // Store event time
        if ( state ) {
            if ( latestStar!=null ) {
                boolean alreadyTracked = false;
                for (int i = 1; i < leapFrogStarCount; i++) {
                    if ( lastStarNumbs[i]==latestStar ) {
                        alreadyTracked = true;
                    }
                }
                if ( !alreadyTracked ) {
                    for ( int i=1; i<leapFrogStarCount; i++ ) {
                        lastStarNumbs[i-1] = lastStarNumbs[i];
                        lastStarTimes[i-1] = lastStarTimes[i];
                    }
                    lastStarNumbs[leapFrogStarCount-1] = latestStar;
                    lastStarTimes[leapFrogStarCount-1] = currentTimeMillis();
                }
            }
        }

        // Check states
        boolean noStars = false;
        boolean firstStar = false;
        boolean leapFrog = false;
        boolean complete = false;

        if ( activeStarCount == 0 ) {
            noStars = true;
        }

        if ( state && activeStarCount==1 ) {
            firstStar = true;
            loneStarted = currentTimeMillis();
        } else {
            loneStarted = null;
            loneStarActive = false;
            cancelLoneStar();
        }

        if ( activeStarCount >= fullWinStarCount ) {
            complete = true;
        }

        if ( currentTimeMillis()-lastStarTimes[leapFrogStarCount-1] <= leapFrogTime
                && lastStarTimes[leapFrogStarCount-1] - lastStarTimes[0] <= leapFrogTime ) {
            leapFrog = true;
        }

        // Update
        applyStarStates(newStates);
        if ( noStars && !leapFrog) {
            resetAll();
        } else {
            if (firstStar ) {

            } else if ( complete ) {
                cancelLeapFrog();
                activateFinal();
            } else {
                cancelFinal();
                if (leapFrog) {
                    activateLeapFrog();
                } else {
                    cancelLeapFrog();
                }
            }
        }

    }

    private void applyStarStates(Map<Integer, Boolean> newStates) {
        for (int i = 0; i < pins.length; i++) {
            if (newStates.get(i) != currentStates.get(i)) {
                currentStates.put(i, newStates.get(i));
                if (newStates.get(i)) {
                    if ( !finalWinActive ) pixels.get(rings[i]).rgb(ringPulseColour).jump(1.0).bounceFadeDown(ringOnFadeUp, ringOnFadeDown);
                    httpStarOn(i);
                } else {
                    if ( !finalWinActive ) pixels.get(rings[i]).fadeDown(ringOffFadeDown);
                    httpStarOff(i);
                }
            }
        }
    }

    private void resetAll() {
        cancelFinal();
        cancelLeapFrog();
        twinkle(false);
        for ( int i=0; i<pins.length; i++ ) {
            pixels.get(stars[i]).min(0.0).fadeDown(ringOnFadeDown);
            pixels.get(rings[i]).min(0.0).fadeDown(ringOnFadeDown);
            httpStarOff(i);
        }
    }

    private void flashRings(int except) {
        for ( int i=0; i<pins.length; i++ ) {
            if ( i!=except ) {
                pixels.get(rings[i]).rgb(ringFlashColour).jump(1.0).fadeDown(500);
            }
        }
    }

    private void activateLeapFrog() {
        leapFrogActive = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for ( int i=stars.length-1; i>=0; i-- ) {
                    if ( leapFrogActive ) {
                        pixels.get(stars[i])
                                .rgb(starChaseColour)
                                .jump(0.0)
                                .range(0.0, 0.4)
                                .bounceFadeUp(starChaseDelay);
                        sleep(starChaseDelay / 2);
                    }
                }
            }
        }, 0);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int limit = 10;
                for ( int i=0; i<connectors.length; i+=limit ) {
                    for ( int j=0; j<(limit-1); j++) {
                        if ( leapFrogActive && i+j < connectors.length ) {
                            pixels.get(connectors[i + j])
                                    .rgb(connectorChaseColour)
                                    .range(0.0, 0.7)
                                    .bounceFadeUp(connectorChaseDelay);
                            sleep(connectorChaseDelay /2);
                        }
                    }
                }
            }
        }, 0);
        httpLeapfrogOn();
    }

    private void cancelLeapFrog() {
        leapFrogActive = false;
        for ( int i=0; i<pins.length; i++ ) {
            pixels.get(stars[i]).min(0.0).fadeDown(starFadeDown);
        }
        twinkle(false);
        httpLeapFrogOff();
    }

    private void activateLoneStar() {
        cancelLeapFrog();
        for ( int i=0; i<pins.length; i++ ) {
            pixels.get(stars[i])
                    .rgb(starWinColour)
                    .jump(0.0)
                    .range(0.8, 1.0)
                    .bounceFadeUp(ringOnFadeDown, ringOnFadeUp);
            pixels.get(rings[i])
                    .rgb(ringWinColour)
                    .jump(1.0)
                    .bounceFadeDown(ringOnFadeUp, ringOnFadeDown);
        }
        twinkle(true);
        httpLoneStarOn();
    }

    private void cancelLoneStar() {
        if ( !finalWinActive ) {
            for (int i = 0; i < pins.length; i++) {
                if (!currentStates.get(i)) {
                    pixels.get(rings[i]).fadeDown(ringOffFadeDown);
                }
            }
        }
        httpLoneStarOff();
    }

    private void finalWinLoop() {
        if ( finalWinActive ) {
            if ( currentTimeMillis() - finalStartedAt > finalWooshDuration) {
                if ( finalPulse ) {
                    if ( currentTimeMillis() - finalStartedAt > (finalWooshDuration+finalPulseDuration)) {
                        for ( int i=0; i<pixelCount; i++ ) {
                            int delay = round(randomBetween(0.8,1.2)*finalPulseMaxDelay);
                            pixels.get(i).pattern().rgb(finalAllColour).max(1.0).fadeUp(delay);
                        }
                    } else {
                        guard.every(finalPulseMaxDelay, "FinalPulse", ()->{
                            for ( int i=0; i<pixelCount; i++ ) {
                                Pixel p = pixels.get(i);
                                double factor = randomBetween(0.5,1.05);
                                p.pattern().rgb(finalAllColour).bounceFade(round(factor*finalPulseDelay), finalPulseDelay, p.goingUp());
                            }
                            if ( finalPulseDelay*finalPulseDeltaFactor >= finalPulseMinDelay ) {
                                finalPulseDelay *= finalPulseDeltaFactor;
                            }
                        });
                    }
                } else {
                    finalPulse = true;
                    for ( int i=0; i<pixelCount; i++ ) {
                        pixels.get(i).pattern().rgb(finalAllColour).min(0.3).max(0.8).bounceFadeUp(finalPulseDelay=finalPulseMaxDelay);
                    }
                }
            } else if ( currentTimeMillis() - lastWoosh > wooshInterval ) {
                finalPulse = false;
                lastWoosh = currentTimeMillis();
                if (wooshInterval-wooshIntervalDelta >= wooshMinInterval) {
                    wooshInterval -= wooshIntervalDelta;
                }
                int star = randomBetween(0, pins.length-1);
                int anim = randomBetween(0, 3);
                RGB colour = finalColours.get(anim);
                int factor = randomBetween(0,10) > 5 ? 1 : -1;
                ((Woosh)animations.get(anim).getRenderer()).addRay(stars[star], factor*randomBetween(1,2));
                pixels.get(rings[star]).rgb(colour).jump(1.0).fadeDown(wooshRingFadeOut);
            }
        } else if ( !loneStarActive && loneStarted!=null && currentTimeMillis()-loneStarted >= loneStarTime ) {
            loneStarActive = true;
            activateLoneStar();
        }
    }

    private void activateFinal() {
        if ( !finalWinActive ) {
            finalStartedAt = currentTimeMillis();
            wooshInterval = wooshMaxInterval;
            lastWoosh = currentTimeMillis()-wooshMaxInterval/3;
            finalWinActive = true;
            for (int i = 0; i < pins.length; i++) {
                pixels.get(stars[i]).rgb(ringFinalColour).bounce(false).delta(0).fadeUp(finalPulseMaxDelay);
                pixels.get(rings[i]).min(0.0).fadeDown(finalPulseMaxDelay);
            }
            for (int i = 0; i < connectors.length; i++) {
                pixels.get(connectors[i]).animation();
            }
            animations.forEach((k,v)->v.start());
        }
        httpFinalOn();
    }

    private void cancelFinal() {
        if ( finalWinActive ) {
            finalWinActive = false;
            for (int i = 0; i < pins.length; i++) {
                pixels.get(stars[i]).min(0.0).fadeDown(round(starFadeDown*1.1));
                pixels.get(rings[i]).min(0.0).fadeDown(round(ringOffFadeDown*0.9));
            }
            for (int i = 0; i < connectors.length; i++) {
                pixels.get(connectors[i]).pattern().min(0.0).fadeDown(starFadeDown);
            }
            animations.forEach((k,a)->{
                ((Woosh)a.getRenderer()).clearRays();
                a.stop();
            });
            httpFinalOff();
        }
    }

    private void twinkle(boolean on) {
        if ( on ) {
            for (int i = 0; i < connectors.length; i++) {
                Pixel p = pixels.get(connectors[i]);
                if (twinkleRange > 0) {
                    p.range(randomBetween(1 - twinkleRange, 0.9), 1)
                            .rgb(twinkleColour)
                            .bounceFadeUp(randomBetween(minTwinkleTime, maxTwinkleTime));
                } else {
                    p.range(0, 1)
                            .rgb(twinkleColour)
                            .bounceFadeUp(randomBetween(minTwinkleTime, maxTwinkleTime));
                }
            }
        } else {
            for (int i = 0; i < connectors.length; i++) {
                pixels.get(connectors[i])
                        .rgb(twinkleColour)
                        .range(0, 1)
                        .fadeDown(maxTwinkleTime);
            }
        }
    }



    @Override
    public void startup() {
        pixels.start();
        tasks.addRepeatingTask("UpdateMonitor", pingInterval, this::pingMonitor);
        tasks.addRepeatingTask("AnimationLoop", 10, this::finalWinLoop);
        clearMonitor();
        updateState(false);
    }

    private void pingMonitor() {
        boolean allOff = true;
        for ( Entry<Integer, Boolean> state : getPinStates().entrySet() ) {
            if ( state.getValue() ) {
                allOff = false;
                break;
            }
        }
        if ( allOff ) {
            updateState(false);
        }
        httpPing();
    }

    private void clearMonitor() {
        for ( int i=0; i<stars.length; i++ ) {
            httpStarOff(i);
        }
        httpLeapFrogOff();
        httpFinalOff();
        httpLoneStarOff();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        clearMonitor();
        pixels.stop();
    }

    private void httpPing() {
        log(now.time().toString()+": HTTP: !PING!");
        http.postAsync(null, monitorHost, monitorPort, "events/"+constellationId+"/ping", "");
    }

    private void httpStarOn(int number) {
        log(now.time().toString()+": HTTP: Star "+number+" ON");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/star" + number + "/on", "");
    }

    private void httpStarOff(int number) {
        log(now.time().toString()+": HTTP: Star "+number+" OFF");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/star" + number + "/off", "");
    }

    private void httpLeapfrogOn() {
        log(now.time().toString()+": HTTP: Leapfrog ON");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/leapfrog/on", "");
    }

    private void httpLeapFrogOff() {
        log(now.time().toString()+": HTTP: Leapfrog OFF");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/leapfrog/off", "");
    }

    private void httpFinalOn() {
        log(now.time().toString()+": HTTP: Complete ON");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/complete/on", "");
    }

    private void httpFinalOff() {
        log(now.time().toString()+": HTTP: Complete OFF");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/complete/off", "");
    }

    private void httpLoneStarOn() {
        log(now.time().toString()+": HTTP: Lone Star ON");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/lonestar/on", "");
    }

    private void httpLoneStarOff() {
        log(now.time().toString()+": HTTP: Lone Star OFF");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/lonestar/off", "");
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
