package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;

import java.util.ArrayList;
import java.util.List;

import static net.amarantha.utils.math.MathUtils.randomBetween;

@PropertyGroup("RainAnimation")
public class Rain extends Animation {

    @Inject private PropertiesService props;

    @Property("DropletCount") private int dropletCount = 100;
    @Property("MinSize") private int dropletMinSize = 1;
    @Property("MaxSize") private int dropletMaxSize = 3;
    @Property("MinSpeed") private int minSpeed = 2;
    @Property("MaxSpeed") private int maxSpeed = 5;

    private List<Droplet> droplets;
    private int layer = 0;

    public void setLayer(int layer) {
        this.layer = layer;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        createDroplets(dropletCount);
    }

    private void createDroplets(int count) {
        droplets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            droplets.add(new Droplet().randomize());
        }
        dropletCount = count;
    }



    @Override
    public void stop() {
        surface.layer(layer).clear();
    }

    @Override
    public void refresh() {
        surface.layer(layer).clear();
        droplets.forEach((droplet) -> {
            droplet.y += droplet.speed;
            if (droplet.y >= surface.height()) {
                droplet.randomize();
            }
            for ( int i=0; i<droplet.size; i++) {
                surface.layer(layer).draw(droplet.x, droplet.y-i, RGB.GREEN);
            }
        });
    }

    @Override
    public void onFocusAdded(int focusId) {

    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {

    }

    class Droplet {
        int x;
        int y;
        int speed;
        int size = 2;
        public Droplet() {
            this(0, 0, 0);
        }
        public Droplet(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
        Droplet randomize() {
            x = randomBetween(0, surface.width());
            y = randomBetween(-10, 0);
            speed = randomBetween(minSpeed, maxSpeed);
            size = randomBetween(dropletMinSize, dropletMaxSize);
            return this;
        }
    }
}
