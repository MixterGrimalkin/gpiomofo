package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.Trigger.TriggerCallback;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.TimeGuard;

import java.util.ArrayList;
import java.util.List;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static java.lang.Integer.parseInt;
import static net.amarantha.utils.colour.RGB.YELLOW;

public class Starlight extends Scenario {

    @Inject private NeoPixel neoPixel;

    @Service private TaskService tasks;

    @Parameter("PixelCount") private int pixelCount;
    @Parameter("StarTriggers") private String starTriggerStr;
    @Parameter("StarPixels") private String starPixelStr;
    @Parameter("RingPixels") private String ringPixelStr;

    private int[] stars;
    private int[] rings;

    private static class Pixel {
        int number;
        double current;
        double max;
        double min;
        double delta;
        boolean bounce;
        Pixel(int number, double current, double max, double min, double delta, boolean bounce) {
            this.number = number;
            this.current = current;
            this.max = max;
            this.min = min;
            this.delta = delta;
            this.bounce = bounce;
        }
        void applyDelta() {
            current += delta;
            if ( current >= max ) {
                current = max;
                if ( bounce ) {
                    delta *= -1;
                }
            } else if ( current <= min ) {
                current = min;
                if ( bounce ) {
                    delta *= -1;
                }
            }
        }
    }

    @Override
    public void setup() {

        String[] pinsStrs = starTriggerStr.split(",");
        String[] starStrs = starPixelStr.split(",");
        String[] ringStrs = ringPixelStr.split(",");
        if ( pinsStrs.length!=starStrs.length || starStrs.length!=ringStrs.length ) {
            System.out.println("StarTriggers, StarPixels and RingPixels must all be the same length!");
            System.exit(1);
        }

        stars = new int[pinsStrs.length];
        rings = new int[pinsStrs.length];

        for ( int i=0; i<pinsStrs.length; i++ ) {
            Trigger t = triggers.gpio(parseInt(pinsStrs[i].trim()), PULL_DOWN, true);
            t.onFire(starCallback(i));
            stars[i] = parseInt(starStrs[i].trim());
            rings[i] = parseInt(ringStrs[i].trim());
        }

        neoPixel.init(pixelCount);

    }

    private TriggerCallback starCallback(int number) {
        return (state) -> {
            if ( state ) {
                pulsingStars.add(number);
            } else {
                pulsingStars.remove((Object)number);
                neoPixel.setPixelColourRGB(stars[number], RGB.BLACK);
            }
            starPulseDelta = ((double)pulsingStars.size()/(double)stars.length)*0.6;
        };
    }



    private List<Integer> pulsingStars = new ArrayList<>();


    private double ringBrightnessDelta = 0.0;
    private RGB ringColour = YELLOW;
    private double ringBrightness = 0.0;

    private int starPulseInterval = 100;
    private double starPulseBrightness = 0.0;
    private double starPulseDelta = 0.2;


    private int p = 0;
    private int size = 26;

    @Inject private TimeGuard guard;

    @Override
    public void startup() {

        tasks.addRepeatingTask("Update", 50, ()->{

            // Rings fade in/out
            ringBrightness += ringBrightnessDelta;
            if ( ringBrightness >= 1.0 ) {
                ringBrightness = 1.0;
                ringBrightnessDelta = 0.0;
            }
            if ( ringBrightness <= 0.0 ) {
                ringBrightness = 0.0;
                ringBrightnessDelta = 0.0;
            }
            for ( int i=0; i<rings.length; i++ ) {
                neoPixel.setPixelColourRGB(rings[i], ringColour.withBrightness(ringBrightness));
            }

            // Connectors fade in/out
//            for ( int i=0; i<connectors.length; i++ ) {
//                neoPixel.setPixelColourRGB(connectors[i], ringColour.withBrightness(ringBrightness));
//            }

            // Pulse stars
            guard.every(starPulseInterval, "StarPulse", () -> {
                if ( pulsingStars.size()==stars.length ) {
                    ringBrightnessDelta = 0.02;
                    starPulseBrightness = 1.0;
                    starPulseDelta = 0.0;
                } else {
                    ringBrightnessDelta = -0.05;
                    starPulseBrightness += starPulseDelta;
                    if (starPulseBrightness >= 1.0) {
                        starPulseBrightness = 1.0;
                        starPulseDelta = -starPulseDelta;
                    }
                    if (starPulseBrightness <= 0.0) {
                        starPulseBrightness = 0.0;
                        starPulseDelta = -starPulseDelta;
                    }
                }
                pulsingStars.forEach((star)->{
                    neoPixel.setPixelColourRGB(stars[star], RGB.WHITE.withBrightness(starPulseBrightness));
                });
            });


            neoPixel.render();
        });

    }

    private void fill(int[] points, RGB colour) {
        for ( int i=0; i<points.length; i++ ) {
            neoPixel.setPixelColourRGB(points[i], colour);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
