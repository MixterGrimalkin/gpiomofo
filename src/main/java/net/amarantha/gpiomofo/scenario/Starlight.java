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

import java.util.*;
import java.util.Map.Entry;

import static java.lang.Integer.parseInt;
import static net.amarantha.utils.math.MathUtils.arrayContains;
import static net.amarantha.utils.math.MathUtils.randomBetween;

public class Starlight extends Scenario {

    @Service private TaskService tasks;
    @Service private DmxService dmx;
    @Service private HttpService http;
    @Service private GpioService gpio;
//    @Service private AwsService aws;
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

    private Long[] lastStarEvents;
    private Integer[] lastStarNumbers;

    private Map<Integer, Boolean> currentStates = new HashMap<>();
    private Map<Integer, List<Integer>> clusters = new HashMap<>();

    private boolean leapFrogActive = false;
    private boolean completeActive = false;

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
        lastStarEvents = new Long[leapFrogStarCount];
        lastStarNumbers = new Integer[leapFrogStarCount];

        // Create stars and rings
        for (int i = 0; i < pinsStrs.length; i++) {
            pins[i] = parseInt(pinsStrs[i].trim());
            stars[i] = parseInt(starStrs[i].trim());
            rings[i] = parseInt(ringStrs[i].trim());
            triggers.gpio(
                    "Star" + i,
                    pins[i],
                    PinPullResistance.valueOf(resistanceStr),
                    triggerState
            ).onFire(this::updateState);
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
            result.put(i, gpio.read(pins[i]));
        }
        return result;
    }

    private void updateState(boolean state) {

        // Update base state
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

        if ( activeStarCount == 0 ) {
            resetAll();
        }

        // Detect First Star event
        if ( state && activeStarCount==1 ) {
            flashRings();
        }

        // Store event time for activations only
        if ( state ) {
            for (int i = 1; i < lastStarEvents.length; i++) {
                lastStarEvents[i - 1] = lastStarEvents[i];
            }
        } else {
            cancelLeapFrog();
        }

        // Detect Leap Frog event
        lastStarEvents[lastStarEvents.length - 1] = System.currentTimeMillis();
        if (    lastStarEvents[lastStarEvents.length - 1] != null &&
                lastStarEvents[0] != null &&
                lastStarEvents[lastStarEvents.length - 1] - lastStarEvents[0] <= leapFrogTime   )
        {
//            boolean validLeapFrog = false;
//            for (int i = 1; i < lastStarNumbers.length; i++) {
//                if (lastStarNumbers[i-1]!=null && lastStarNumbers[i]!=null && !lastStarNumbers[i-1].equals(lastStarNumbers[i])) {
//                    validLeapFrog = true;
//                }
//            }
//            if (validLeapFrog) {
                activateLeapFrog();
//            }
        } else {
            cancelLeapFrog();
        }

        // Update Star Activations
        for ( int i=0; i<pins.length; i++ ) {
            if ( newStates.get(i)!=currentStates.get(i) ) {
                currentStates.put(i, newStates.get(i));
                if (newStates.get(i)) {
                    activateStar(i);
                } else {
                    cancelStar(i);
                }
            }
        }

        // Detect Complete event
        if ( activeStarCount >= fullWinStarCount ) {
            activateComplete();
        } else {
            cancelComplete();
        }

    }

    private void resetAll() {
        twinkle(false);
        for ( int i=0; i<pins.length; i++ ) {
            pixels.get(stars[i]).min(0.0).fadeDown(ringOnFadeDown);
            pixels.get(rings[i]).min(0.0).fadeDown(ringOnFadeDown);
        }
    }

    private void flashRings() {
        for ( int i=0; i<pins.length; i++ ) {
            pixels.get(rings[i]).rgb(ringFlashColour).jump(1.0).fadeDown(500);
        }
    }

    private void activateStar(int number) {
        pixels.get(rings[number]).rgb(ringPulseColour).jump(1.0).bounceFadeDown(ringOnFadeUp, ringOnFadeDown);
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/star" + number + "/on", "");
    }

    private void cancelStar(int number) {
        pixels.get(rings[number]).fadeDown(ringOffFadeDown);
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/star" + number + "/off", "");
    }

    private void activateLeapFrog() {
        if (!leapFrogActive && !completeActive) {
            leapFrogActive = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    for ( int i=stars.length-1; i>=0; i-- ) {
                        pixels.get(stars[i])
                                .rgb(starChaseColour)
                                .jump(0.0)
                                .range(0.0, 0.4)
                                .bounceFadeUp(starChaseDelay);
                        sleep(starChaseDelay /2);
                    }
                }
            }, 0);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    int limit = 10;
                    for ( int i=0; i<connectors.length; i+=limit ) {
                        for ( int j=0; j<(limit-1); j++) {
                            if ( i+j < connectors.length ) {
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
            http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/leapfrog/on", "");
        }
    }

    private void cancelLeapFrog() {
        if (leapFrogActive) {
            leapFrogActive = false;
            for ( int i=0; i<pins.length; i++ ) {
                pixels.get(stars[i]).min(0.0).fadeDown(starFadeDown);
            }
            twinkle(false);
        }
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/leapfrog/off", "");
    }

    private void activateComplete() {
        if (!completeActive) {
            completeActive = true;
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
            http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/complete/on", "");
        }
    }

    private void cancelComplete() {
        if (completeActive) {
            completeActive = false;
            for ( int i=0; i<pins.length; i++ ) {
                pixels.get(stars[i]).min(0.0).fadeDown(starFadeDown);
                pixels.get(rings[i]).rgb(ringPulseColour);
                if ( !currentStates.get(i) ) {
                    pixels.get(rings[i]).min(0.0).fadeDown(ringOffFadeDown);
                }
            }
            twinkle(false);
        }
        http.postAsync(null, monitorHost, monitorPort, "events/" + constellationId + "/complete/off", "");
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
        http.postAsync(null, monitorHost, monitorPort, "events/"+constellationId+"/ping", "");
    }

    private void clearMonitor() {
        for ( int i=0; i<stars.length; i++ ) {
            http.post(monitorHost, monitorPort, "events/"+constellationId+"/star"+i+"/off", "");
        }
        http.post(monitorHost, monitorPort, "events/"+constellationId+"/leapfrog/off", "");
        http.post(monitorHost, monitorPort, "events/"+constellationId+"/complete/off", "");
    }

    @Override
    public void shutdown() {
        super.shutdown();
        clearMonitor();
        pixels.stop();
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
