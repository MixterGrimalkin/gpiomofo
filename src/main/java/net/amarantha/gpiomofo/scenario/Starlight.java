package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.dmx.DmxService;
import net.amarantha.gpiomofo.trigger.Trigger.TriggerCallback;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.TimeGuard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Starlight extends Scenario {

    @Service private TaskService tasks;
    @Service private DmxService dmx;

    @Inject private NeoPixel neoPixel;
    @Inject private TimeGuard guard;

    @Parameter("PixelCount") private int pixelCount;
    @Parameter("StarTriggers") private String starTriggerStr;
    @Parameter("StarPixels") private String starPixelStr;
    @Parameter("RingPixels") private String ringPixelStr;
    @Parameter("RingColour") private RGB ringColour;
    @Parameter("StarColour") private RGB starColour;
    @Parameter("ConnectorColour") private RGB connectorColour;
    @Parameter("PinResistance") private String resistanceStr;
    @Parameter("TriggerState") private boolean triggerState;

    private int[] stars;
    private int[] rings;
    private int[] connectors;

    private List<Integer> pulsingRings = new ArrayList<>();

    private int ringPulseInterval = 100;
    private double ringBrightness = 0.0;
    private double ringDelta = 0.2;

    private double starBrightnessDelta = 0.0;
    private double starBrightness = 0.0;

    @Override
    public void setup() {

        PinPullResistance resistance = PinPullResistance.valueOf(resistanceStr);

        String[] pinsStrs = starTriggerStr.split(",");
        String[] starStrs = starPixelStr.split(",");
        String[] ringStrs = ringPixelStr.split(",");
        if ( pinsStrs.length!=starStrs.length || starStrs.length!=ringStrs.length ) {
            System.out.println("StarTriggers, StarPixels and RingPixels must all be the same length!");
            System.exit(1);
        }

        stars = new int[pinsStrs.length];
        rings = new int[pinsStrs.length];
        connectors = new int[pixelCount-(stars.length+rings.length)];

        for ( int i=0; i<pinsStrs.length; i++ ) {
            stars[i] = parseInt(starStrs[i].trim());
            rings[i] = parseInt(ringStrs[i].trim());
            triggers.gpio(
                    parseInt(pinsStrs[i].trim()),
                    resistance,
                    triggerState
            ).onFire(starCallback(i));
            neoPixel.intercept(rings[i], dmx.rgbDevice(i*6).getInterceptor());
            neoPixel.intercept(stars[i], dmx.rgbDevice((i*6)+3).getInterceptor());
        }

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
                neoPixel.setPixel(rings[number], RGB.BLACK);
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
                neoPixel.setPixel(stars[i], starColour.withBrightness(starBrightness));
            }

            // Connectors fade in/out
            for ( int i=0; i<connectors.length; i++ ) {
                neoPixel.setPixel(connectors[i], connectorColour.withBrightness(starBrightness));
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
                    neoPixel.setPixel(rings[star], ringColour.withBrightness(ringBrightness));
                });
            });
            neoPixel.render();
        });

    }

    private void fill(int[] points, RGB colour) {
        for ( int i=0; i<points.length; i++ ) {
            neoPixel.setPixel(points[i], colour);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        neoPixel.allOff();
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
