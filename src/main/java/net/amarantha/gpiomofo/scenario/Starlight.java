package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixelFactory;
import net.amarantha.gpiomofo.display.pixeltape.Pixel;
import net.amarantha.gpiomofo.service.AwsService;
import net.amarantha.gpiomofo.service.dmx.DmxService;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.Trigger.TriggerCallback;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.http.HttpService;
import net.amarantha.utils.http.entity.HttpCallback;
import net.amarantha.utils.http.entity.Param;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.string.StringMap;
import net.amarantha.utils.task.TaskService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.Response;
import java.util.*;

import static java.lang.Integer.parseInt;
import static net.amarantha.utils.math.MathUtils.*;
import static net.amarantha.utils.shell.Utility.log;

public class Starlight extends Scenario {

    @Service private TaskService tasks;
    @Service private DmxService dmx;
    @Service private HttpService http;
//    @Service private AwsService aws;


    @Inject private NeoPixelFactory pixels;
    @Inject private NeoPixel neoPixel;

    @Parameter("ConstellationId")   private String constellationId;
    @Parameter("MonitorHost")       private String monitorHost;
    @Parameter("MonitorPort")       private int monitorPort;

    @Parameter("FullWinThreshold")  private int fullWinThreshold;

    @Parameter("PixelCount")        private int pixelCount;
    @Parameter("StarTriggers")      private String starTriggerStr;
    @Parameter("StarPixels")        private String starPixelStr;
    @Parameter("RingPixels")        private String ringPixelStr;
    @Parameter("RingColour")        private RGB ringColour;
    @Parameter("StarColour")        private RGB starColour;
    @Parameter("ConnectorColour")   private RGB connectorColour;
    @Parameter("PinResistance")     private String resistanceStr;
    @Parameter("TriggerState")      private boolean triggerState;

    @Parameter("Clusters")          private String clusters;

    @Parameter("DmxStars")          private boolean dmxStars;
    @Parameter("DmxRings")          private boolean dmxRings;

    @Parameter("StarFadeUp")        private int starFadeUp;
    @Parameter("StarFadeDown")      private int starFadeDown;
    @Parameter("RingFadeDown")      private int ringFadeDown;
    @Parameter("RingPulseMin")      private int minPulseTime;
    @Parameter("RingPulseMax")      private int maxPulseTime;
    @Parameter("TwinklePulseMin")   private int minTwinkleTime;
    @Parameter("TwinklePulseMax")   private int maxTwinkleTime;
    @Parameter("TwinkleRange")      private double twinkleRange;

    @Parameter("MaxRingBrightness") private double maxRingBrightness;
    @Parameter("MinRingBrightness") private double minRingBrightness;

    private Integer[] stars;
    private Integer[] rings;
    private Integer[] connectors;

    private List<Integer> pulsingRings = new ArrayList<>();

    @Override
    public void setup() {

        String[] pinsStrs = starTriggerStr.split(",");
        String[] starStrs = starPixelStr.split(",");
        String[] ringStrs = ringPixelStr.split(",");
        if (pinsStrs.length != starStrs.length || starStrs.length != ringStrs.length) {
            System.out.println("StarTriggers, StarPixels and RingPixels must all be the same length!");
            System.exit(1);
        }

        stars = new Integer[pinsStrs.length];
        rings = new Integer[pinsStrs.length];
        connectors = new Integer[pixelCount - (stars.length * 2)];

        for (int i = 0; i < pinsStrs.length; i++) {
            stars[i] = parseInt(starStrs[i].trim());
            rings[i] = parseInt(ringStrs[i].trim());
            triggers.gpio(
                    "Star" + i,
                    parseInt(pinsStrs[i].trim()),
                    PinPullResistance.valueOf(resistanceStr),
                    triggerState
            ).onFire(starCallback(i));
            if (dmxRings) neoPixel.intercept(rings[i], dmx.rgbDevice(i * 4).getInterceptor());
            if (dmxStars) neoPixel.intercept(stars[i], dmx.device((i * 4) + 3).getInterceptor());
        }

        int j = 0;
        for (int i = 0; i < pixelCount; i++) {
            boolean isStar = arrayContains(stars, i);
            boolean isRing = arrayContains(rings, i);
            Pixel p = pixels.create(i);
            if (isStar) {
                p.rgb(starColour);
            }
            if (isRing) {
                p.rgb(ringColour);
            }
            if (!isStar && !isRing) {
                p.rgb(connectorColour);
                connectors[j++] = i;
            }
        }

        neoPixel.init(pixelCount);

    }

    private TriggerCallback starCallback(int number) {
        return (state) -> {
            if (state) {
                pulsingRings.add(number);
                http.postAsync(null, monitorHost, monitorPort, "events/"+constellationId+"/star"+number+"/on", "");
            } else {
                pulsingRings.remove((Object) number);
                pixels.get(rings[number]).bounce(false).fadeDown(ringFadeDown);
                http.postAsync(null, monitorHost, monitorPort, "events/"+constellationId+"/star"+number+"/off", "");
            }
            modifyEffect();
        };
    }

    private List<Integer> ringsToAdd = new LinkedList<>();
    private List<Integer> ringsToRemove = new LinkedList<>();

    private void modifyEffect() {
        if (pulsingRings.size() >= min(rings.length, fullWinThreshold) ) {
            // Payoff
            for (int i = 0; i < rings.length; i++) {
                pixels.get(rings[i]).bounce(false).max(maxRingBrightness).fadeUp(maxPulseTime);
                pixels.get(stars[i]).bounce(false).fadeUp(starFadeUp);
            }
            for (int i = 0; i < connectors.length; i++) {
                Pixel p = pixels.get(connectors[i]);
                if (twinkleRange > 0) {
                    p.range(randomBetween(1 - twinkleRange, 0.9), 1)
                            .bounce(true)
                            .fadeUp(randomBetween(minTwinkleTime, maxTwinkleTime));
                } else {
                    p.range(0, 1)
                            .bounce(false)
                            .fadeUp(randomBetween(minTwinkleTime, maxTwinkleTime));
                }
            }
            http.postAsync(null, monitorHost, monitorPort, "events/"+constellationId+"/complete/on", "");
        } else {
            http.postAsync(null, monitorHost, monitorPort, "events/"+constellationId+"/complete/off", "");
            int pulseTime = 0;
            double ringBrightness = 0.0;
            if (pulsingRings.size() == 1) {
                // First star
                pulseTime = maxPulseTime;
                ringBrightness = minRingBrightness;
            } else if (pulsingRings.size() == min(rings.length, fullWinThreshold) - 1) {
                // Penultimate state
                pulseTime = minPulseTime;
                ringBrightness = maxRingBrightness;
                for (int i = 0; i < stars.length; i++) {
                    pixels.get(stars[i]).bounce(false).fadeDown(starFadeDown);
                }
                for (int i = 0; i < connectors.length; i++) {
                    pixels.get(connectors[i]).bounce(false).range(0, 1).fadeDown(maxTwinkleTime);
                }
            } else {
                // Intermediate state
                pulseTime = round(maxPulseTime - (maxPulseTime - minPulseTime) * (((double) pulsingRings.size()) / ((double) rings.length - 1)));
                ringBrightness = minRingBrightness + (maxRingBrightness - minRingBrightness) * (((double) pulsingRings.size()) / ((double) rings.length - 1));
            }
            Double jump = null;
            boolean up = false;
            for (int i : pulsingRings) {
                Pixel p = pixels.get(rings[i]);
                if (jump == null) {
                    jump = p.current();
                    up = p.goingUp();
                } else {
                    p.jump(jump);
                }
                p.bounce(true).max(ringBrightness).fade(pulseTime, up);
            }
        }
    }

    @Override
    public void startup() {
        pixels.start();
        tasks.addRepeatingTask("UpdateMonitor", 50000, this::pingMonitor);
        clearMonitor();
    }

    private void pingMonitor() {
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

//    private void subscribeToAws() {
//        aws.subscribe("starlight/" + constellationName, (message) -> {
//            Map<String, String> data = jsonToStringMap(message.getStringPayload());
//            if ("Activate".equals(data.get("message"))) {
//                pulsingRings.clear();
//                for ( int i=0; i<rings.length; i++ ) {
//                    pulsingRings.add(i);
//                }
//                modifyEffect();
//            } else if ("Deactivate".equals(data.get("message"))) {
//                pulsingRings.clear();
//                for ( int i=0; i<rings.length; i++ ) {
//                    pixels.get(stars[i]).bounce(false).fadeDown(starFadeDown);
//                    pixels.get(rings[i]).bounce(false).fadeDown(ringFadeDown);
//                }
//                for (int i = 0; i < connectors.length; i++) {
//                    pixels.get(connectors[i]).bounce(false).range(0, 1).fadeDown(maxTwinkleTime);
//                }
//                modifyEffect();
//            }
//        });
//    }

//    private Map<String, String> buildMessage(int number, boolean activate) {
//        return
//                new StringMap()
//                        .add("constellation", constellationName)
//                        .add("star", number + "")
//                        .add("state", activate ? "ON" : "OFF")
//                        .get();
//    }

//    private Map<String, String> activateConstellationMessage =
//            new StringMap()
//                    .add("constellation", constellationName)
//                    .add("activated", "true")
//                    .get();
//
//
//    private Map<String, String> jsonToStringMap(String json) {
//        Map<String, String> result = new HashMap<>();
//        try {
//            JSONObject obj = new JSONObject(json);
//            Iterator it = obj.keys();
//            while (it.hasNext()) {
//                String key = it.next().toString();
//                result.put(key, obj.get(key).toString());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

}
