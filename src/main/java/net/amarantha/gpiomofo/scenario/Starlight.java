package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixelFactory;
import net.amarantha.gpiomofo.display.pixeltape.Pixel;
import net.amarantha.gpiomofo.service.dmx.DmxService;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.http.HttpService;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.Now;

import java.util.*;
import java.util.Map.Entry;

import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;
import static net.amarantha.utils.math.MathUtils.arrayContains;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.shell.Utility.log;

public class Starlight extends Scenario {

    @Inject private Now now;

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

    private Integer[] pins;
    private Integer[] stars;
    private Integer[] rings;
    private Integer[] connectors;

    private long[] lastStarTimes;
    private int[] lastStarNumbs;

    private Map<Integer, Boolean> currentStates = new HashMap<>();
    private Map<Integer, List<Integer>> clusters = new HashMap<>();

    private boolean leapFrogActive = false;

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
            ).onFire((state) -> updateState(state, starNo));
            if (dmxRings) neoPixel.intercept(rings[i], dmx.rgbDevice(i * 4).getInterceptor());
            if (dmxStars) neoPixel.intercept(stars[i], dmx.device((i * 4) + 3).getInterceptor());
            currentStates.put(i, false);
        }

        // Create pixels
        int j = 0;
        for (int i = 0; i < pixelCount; i++) {
            pixels.create(i);
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

        neoPixel.init(pixelCount);

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
        } else {
            for ( int i=0; i<leapFrogStarCount; i++ ) {
                lastStarNumbs[i] = -1;
                lastStarTimes[i] = -1;
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
        }

        if ( activeStarCount >= fullWinStarCount ) {
            complete = true;
        }

        if ( lastStarTimes[0]!=-1 && lastStarTimes[leapFrogStarCount-1] - lastStarTimes[0] <= leapFrogTime ) {
            leapFrog = true;
        }

        // Update
        applyStarStates(newStates);
        if ( noStars ) {
            resetAll();
        } else {
            if ( firstStar ) {
                flashRings(latestStar==null ? -1 : latestStar);
            } else if (complete) {
                cancelLeapFrog();
                activateComplete();
            } else {
                cancelComplete();
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
                    pixels.get(rings[i]).rgb(ringPulseColour).jump(1.0).bounceFadeDown(ringOnFadeUp, ringOnFadeDown);
                    httpStarOn(i);
                } else {
                    pixels.get(rings[i]).fadeDown(ringOffFadeDown);
                    httpStarOff(i);
                }
            }
        }
    }

    private void resetAll() {
        cancelComplete();
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

    private void activateComplete() {
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
        httpCompleteOn();
    }

    private void cancelComplete() {
        for ( int i=0; i<pins.length; i++ ) {
            pixels.get(stars[i]).min(0.0).fadeDown(starFadeDown);
            pixels.get(rings[i]).rgb(ringPulseColour);
            if ( !currentStates.get(i) ) {
                pixels.get(rings[i]).min(0.0).fadeDown(ringOffFadeDown);
            }
        }
        twinkle(false);
        httpCompleteOff();
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
        httpCompleteOff();
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

    private void httpCompleteOn() {
        log(now.time().toString()+": HTTP: Complete ON");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/complete/on", "");
    }

    private void httpCompleteOff() {
        log(now.time().toString()+": HTTP: Complete OFF");
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/complete/off", "");
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
