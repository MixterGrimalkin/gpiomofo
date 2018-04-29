package net.amarantha.gpiomofo.service.pixeltape.matrix.sprites;

import com.google.inject.Inject;
import net.amarantha.utils.colour.RGB;

import java.util.*;

import static java.lang.Math.PI;
import static java.lang.System.currentTimeMillis;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFrom;

public class Explosion extends Sprite {

    @Inject private SpriteField factory;

    private int progress = 0;
    private int sparkCount = 20;

    public Explosion setSparkCount(int sparkCount) {
        this.sparkCount = sparkCount;
        return this;
    }

    private Map<Integer, Sprite> sparks = new HashMap<>();
    private Map<Integer, int[]> centres = new HashMap<>();
    private Map<Integer, Double> angles = new HashMap<>();
    private Map<Integer, Double> deltas = new HashMap<>();
    private Map<Integer, Double> distances = new HashMap<>();
    private Map<Integer, Long> lifetimes = new HashMap<>();

    private List<RGB> colours = new LinkedList<>();

    @Override
    public void init() {
        super.init();
    }

    public Explosion addColours(RGB... colour) {
        colours.addAll(Arrays.asList(colour));
        return this;
    }

    @Override
    public Sprite setColour(RGB colour) {
        colours.clear();
        colours.add(colour);
        return super.setColour(colour);
    }

    private int nextId = 0;

    private double minSpeed = 0.2;
    private double maxSpeed = 2.0;

    public Explosion setSpeed(double min, double max) {
        minSpeed = min;
        maxSpeed = max;
        return this;
    }


    @Override
    public void reset() {
//        super.reset();
        for ( int i=0; i<sparkCount; i++ ) {
            Sprite spark = factory.make(Ball.class).setColour(randomFrom(colours)).setLayer(layer);
            int id = i + nextId++;
            sparks.put(id, spark);
            centres.put(id, position());
            angles.put(id, randomBetween(0.0, PI*2));
            deltas.put(id, randomBetween(minSpeed, maxSpeed));
            lifetimes.put(id, currentTimeMillis() + randomBetween(1000,3000));
            distances.put(id, 0.0);
        }
    }

    @Override
    public void update() {
        super.update();
        List<Integer> idsToRemove = new LinkedList<>();
        sparks.forEach((id, spark)-> {
            double dist = distances.get(id) + deltas.get(id);
            distances.put(id, dist);
            double x = centres.get(id)[X] + Math.sin(angles.get(id)) * dist;
            double y = centres.get(id)[Y] + Math.cos(angles.get(id)) * dist;
            spark.setPosition(x, y);
            if ( lifetimes.get(id) < currentTimeMillis() ) {
                idsToRemove.add(id);
            }
        });
        idsToRemove.forEach((id)->{
            sparks.remove(id);
            centres.remove(id);
            angles.remove(id);
            deltas.remove(id);
            distances.remove(id);
            lifetimes.remove(id);
        });
    }

    @Override
    public void show() {
        super.show();
        sparks.forEach((id,spark)->spark.show());
    }

    @Override
    public void doRender() {
        sparks.forEach((id,spark)->spark.render());
    }

}
