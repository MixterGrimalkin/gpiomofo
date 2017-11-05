package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.AwsService;
import net.amarantha.gpiomofo.service.dmx.DmxService;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.Trigger.TriggerCallback;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.*;

import static java.lang.Integer.parseInt;
import static net.amarantha.utils.math.MathUtils.*;
import static net.amarantha.utils.shell.Utility.log;

public class Starlight extends Scenario {

    @Service
    private TaskService tasks;
    @Service
    private DmxService dmx;
    @Service
    private AwsService aws;

    @Inject
    private NeoPixel neoPixel;

    @Parameter("PixelCount")
    private int pixelCount;
    @Parameter("StarTriggers")
    private String starTriggerStr;
    @Parameter("StarPixels")
    private String starPixelStr;
    @Parameter("RingPixels")
    private String ringPixelStr;
    @Parameter("RingColour")
    private RGB ringColour;
    @Parameter("StarColour")
    private RGB starColour;
    @Parameter("ConnectorColour")
    private RGB connectorColour;
    @Parameter("PinResistance")
    private String resistanceStr;
    @Parameter("TriggerState")
    private boolean triggerState;

    @Parameter("DmxStars")
    private boolean dmxStars;
    @Parameter("DmxRings")
    private boolean dmxRings;

    @Parameter("StarFadeUp")
    private int starFadeUp;
    @Parameter("StarFadeDown")
    private int starFadeDown;
    @Parameter("RingFadeDown")
    private int ringFadeDown;
    @Parameter("RingPulseMin")
    private int minPulseTime;
    @Parameter("RingPulseMax")
    private int maxPulseTime;
    @Parameter("TwinklePulseMin")
    private int minTwinkleTime;
    @Parameter("TwinklePulseMax")
    private int maxTwinkleTime;
    @Parameter("TwinkleRange")
    private double twinkleRange;

    @Parameter("MaxRingBrightness")
    private double maxRingBrightness;
    @Parameter("MinRingBrightness")
    private double minRingBrightness;

    private Map<Integer, Pixel> pixels = new HashMap<>();
    private Integer[] stars;
    private Integer[] rings;
    private Integer[] connectors;

    private List<Integer> pulsingRings = new ArrayList<>();

    private int updateInterval = 10;

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
            Pixel p = new Pixel(i);
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
            pixels.put(i, p);
        }

        neoPixel.init(pixelCount);

    }

    private String constellationName = "cassiopeia";

    private TriggerCallback starCallback(int number) {
        return (state) -> {
            if (state) {
                pulsingRings.add(number);
                aws.publish("starlight/" + constellationName, "Star " + number + " Activated");
            } else {
                pulsingRings.remove((Object) number);
                pixels.get(rings[number]).bounce(false).fadeDown(ringFadeDown);
                aws.publish("starlight/" + constellationName, "Star " + number + " Deactivated");
            }
            modifyEffect();
        };
    }

    private void modifyEffect() {
        if (pulsingRings.size() == rings.length) {
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
        } else {
            int pulseTime = 0;
            double ringBrightness = 0.0;
            if (pulsingRings.size() == 1) {
                // First star
                pulseTime = maxPulseTime;
                ringBrightness = minRingBrightness;
            } else if (pulsingRings.size() == rings.length - 1) {
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

    private Map<String, String> jsonToStringMap(String json) {
        Map<String, String> result = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(json);
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                result.put(key, obj.get(key).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void startup() {

        aws.subscribe("starlight/" + constellationName, (message) -> {
            Map<String, String> data = jsonToStringMap(message.getStringPayload());
            if ("Activate".equals(data.get("message"))) {
                pulsingRings.clear();
                for ( int i=0; i<rings.length; i++ ) {
                    pulsingRings.add(i);
                }
                modifyEffect();
            } else if ("Deactivate".equals(data.get("message"))) {
                pulsingRings.clear();
                for ( int i=0; i<rings.length; i++ ) {
                    pixels.get(stars[i]).bounce(false).fadeDown(starFadeDown);
                    pixels.get(rings[i]).bounce(false).fadeDown(ringFadeDown);
                }
                for (int i = 0; i < connectors.length; i++) {
                    pixels.get(connectors[i]).bounce(false).range(0, 1).fadeDown(maxTwinkleTime);
                }
                modifyEffect();
            }
        });

        neoPixel.allOff();
        tasks.addRepeatingTask("Update", updateInterval, () -> {
            pixels.forEach((i, pixel) -> pixel.update());
            neoPixel.render();
        });
    }

    @Override
    public void shutdown() {
        super.shutdown();
        neoPixel.allOff();
    }

    private class Pixel {
        int number;
        double current;
        double min;
        double max;
        double delta;
        boolean bounce;
        RGB rgb = RGB.WHITE;

        Pixel(int number) {
            this(number, 0.0, 0.0, 1.0, 0.0, false);
        }

        Pixel(int number, double current, double min, double max, double delta, boolean bounce) {
            this.number = number;
            this.current = current;
            this.min = min;
            this.max = max;
            this.delta = delta;
            this.bounce = bounce;
        }

        Pixel update() {
            return applyDelta().draw();
        }

        Pixel applyDelta() {
            current += delta;
            if (current >= max) {
                if (delta > 0) {
                    current = max;
                    if (bounce) {
                        delta *= -1;
                    } else {
                        delta = 0;
                    }
                }
            } else if (current <= min) {
                if (delta < 0) {
                    current = min;
                    if (bounce) {
                        delta *= -1;
                    } else {
                        delta = 0;
                    }
                }
            }
            return this;
        }

        Pixel rgb(RGB rgb) {
            this.rgb = rgb;
            return this;
        }

        Pixel delta(double delta) {
            this.delta = delta;
            return this;
        }

        Pixel bounce(boolean bounce) {
            this.bounce = bounce;
            return this;
        }

        Pixel range(double min, double max) {
            this.min = min;
            this.max = max;
            return this;
        }

        Pixel max(double max) {
            this.max = max;
            return this;
        }

        Pixel min(double min) {
            this.min = min;
            return this;
        }

        boolean goingUp() {
            return delta > 0;
        }

        void fadeUp(int duration) {
            fade(duration, true);
        }

        void fadeDown(int duration) {
            fade(duration, false);
        }

        void fade(int duration, boolean up) {
            if ((up && current <= max) || (!up && current >= min)) {
                double distance = up ? (max - min) : -(max - min);
                delta = distance / (duration / updateInterval);
            }
        }

        Pixel draw() {
            neoPixel.setPixel(number, rgb.withBrightness(current));
            return this;
        }

        Pixel jump(double jump) {
            current = jump;
            return this;
        }

        double current() {
            return current;
        }
    }


}
