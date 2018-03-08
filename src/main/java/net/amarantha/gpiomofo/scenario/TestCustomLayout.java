package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.Trigger.TriggerCallback;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.TimeGuard;

import java.util.ArrayList;
import java.util.List;

import static net.amarantha.utils.colour.RGB.YELLOW;

public class TestCustomLayout extends Scenario {

    @Inject private NeoPixel neoPixel;

    @Service private TaskService tasks;

    @Named("Star1") private Trigger star1;
    @Named("Star2") private Trigger star2;
    @Named("Star3") private Trigger star3;
    @Named("Star4") private Trigger star4;
    @Named("Star5") private Trigger star5;

    private int[] rings = { 1, 7, 13, 19, 25 };
    private int[] stars = { 0, 6, 12, 18, 24 };
    private int[] connectors = { 2, 3, 4, 5, 8, 9, 10, 11, 14, 15, 16, 17, 20, 21, 22, 23 };

    @Override
    public void setup() {

        neoPixel.init(size);

        star1.onFire(starCallback(0));
        star2.onFire(starCallback(1));
        star3.onFire(starCallback(2));
        star4.onFire(starCallback(3));
        star5.onFire(starCallback(4));


    }

    private TriggerCallback starCallback(int number) {
        return null;
//        return (state) -> {
//            if ( state ) {
////                pulsingStars.add(number);
//            } else {
//                pulsingStars.remove((Object)number);
//                neoPixel.setPixel(stars[number], RGB.BLACK);
//            }
////            starPulseInterval = round(200.0 / pulsingStars.size());
//            starPulseDelta = ((double)pulsingStars.size()/(double)stars.length)*0.6;
//        };
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
                neoPixel.setPixel(rings[i], ringColour.withBrightness(ringBrightness));
            }

            // Connectors fade in/out
            for ( int i=0; i<connectors.length; i++ ) {
                neoPixel.setPixel(connectors[i], ringColour.withBrightness(ringBrightness));
            }

            // Pulse stars
//            guard.every(starPulseInterval, "StarPulse", () -> {
//                if ( pulsingStars.size()==stars.length ) {
//                    ringBrightnessDelta = 0.02;
//                    starPulseBrightness = 1.0;
//                    starPulseDelta = 0.0;
//                } else {
//                    ringBrightnessDelta = -0.05;
//                    starPulseBrightness += starPulseDelta;
//                    if (starPulseBrightness >= 1.0) {
//                        starPulseBrightness = 1.0;
//                        starPulseDelta = -starPulseDelta;
//                    }
//                    if (starPulseBrightness <= 0.0) {
//                        starPulseBrightness = 0.0;
//                        starPulseDelta = -starPulseDelta;
//                    }
//                }
//                pulsingStars.forEach((star)->{
//                    neoPixel.setPixel(stars[star], RGB.WHITE.withBrightness(starPulseBrightness));
//                });
//            });


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
    }
}
