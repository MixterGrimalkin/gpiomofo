package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
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

public class Starlight extends Scenario {

    @Inject private NeoPixel neoPixel;

    @Service private TaskService tasks;

    @Parameter("PixelCount") private int pixelCount;
    @Parameter("StarTriggers") private String starTriggerStr;
    @Parameter("StarPixels") private String starPixelStr;
    @Parameter("RingPixels") private String ringPixelStr;

    private List<Integer> pulsingRings = new ArrayList<>();


    private double starBrightnessDelta = 0.0;
    @Parameter("RingColour") private RGB ringColour;
    @Parameter("StarColour") private RGB starColour;
    @Parameter("ConnectorColour") private RGB connectorColour;
    private double starBrightness = 0.0;

    @Parameter("PinResistance") private String resistanceStr;
    private PinPullResistance resistance;
    @Parameter("TriggerState") private boolean triggerState;

    private int ringPulseInterval = 100;
    private double ringBrightness = 0.0;
    private double ringDelta = 0.2;

    private int p = 0;
    private int size = 26;

    @Inject private TimeGuard guard;

    private int[] stars;
    private int[] rings;
    private int[] connectors;

    @Override
    public void setup() {

        System.out.println(triggerState);
        System.out.println(resistanceStr);
        resistance = PinPullResistance.valueOf(resistanceStr);
        System.out.println(resistance);

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
            Trigger t = triggers.gpio(parseInt(pinsStrs[i].trim()), resistance, triggerState);
            t.onFire(starCallback(i));
            stars[i] = parseInt(starStrs[i].trim());
            rings[i] = parseInt(ringStrs[i].trim());
        }

        connectors = new int[pixelCount-(stars.length+rings.length)];
        int j = 0;
        for ( int i=0; i<pixelCount; i++ ) {
            if ( !arrayContains(stars, i) && !arrayContains(rings, i) ) {
                connectors[j++] = i;
            }
        }

        neoPixel.init(pixelCount);

    }

    private boolean arrayContains(int[] array, int value) {
        for ( int i=0; i<array.length; i++ ) {
            if ( array[i]==value ) {
                return true;
            }
        }
        return false;
    }

    private TriggerCallback starCallback(int number) {
        return (state) -> {
            if ( state ) {
                pulsingRings.add(number);
            } else {
                pulsingRings.remove((Object)number);
                neoPixel.setPixelColourRGB(rings[number], RGB.BLACK);
            }
            ringDelta = ((double) pulsingRings.size()/(double)rings.length)*0.6;
        };
    }



    @Override
    public void startup() {

        tasks.addRepeatingTask("Update", 50, ()->{

            // Stars fade in/out
            starBrightness += starBrightnessDelta;
            if ( starBrightness >= 1.0 ) {
                starBrightness = 1.0;
                starBrightnessDelta = 0.0;
            }
            if ( starBrightness <= 0.0 ) {
                starBrightness = 0.0;
                starBrightnessDelta = 0.0;
            }
            for ( int i=0; i<stars.length; i++ ) {
                neoPixel.setPixelColourRGB(stars[i], starColour.withBrightness(starBrightness));
            }

            // Connectors fade in/out
            for ( int i=0; i<connectors.length; i++ ) {
                neoPixel.setPixelColourRGB(connectors[i], connectorColour.withBrightness(starBrightness));
            }

            // Pulse rings
            guard.every(ringPulseInterval, "RingPulse", () -> {
                if ( pulsingRings.size()==rings.length ) {
                    starBrightnessDelta = 0.02;
                    ringBrightness = 1.0;
                    ringDelta = 0.0;
                } else {
                    starBrightnessDelta = -0.05;
                    ringBrightness += ringDelta;
                    if (ringBrightness >= 1.0) {
                        ringBrightness = 1.0;
                        ringDelta *= -1;
                    }
                    if (ringBrightness <= 0.0) {
                        ringBrightness = 0.0;
                        ringDelta *= -1;
                    }
                }
                pulsingRings.forEach((star)->{
                    neoPixel.setPixelColourRGB(rings[star], ringColour.withBrightness(ringBrightness));
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


}
