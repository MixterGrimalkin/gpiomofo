package net.amarantha.gpiomofo.service.pixeltape.matrix.sprites;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.amarantha.gpiomofo.core.Constants;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.service.AbstractService;
import net.amarantha.utils.task.Task;
import net.amarantha.utils.task.TaskService;

import java.util.*;
import java.util.function.BiConsumer;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;

public class SpriteField {

    @Inject private LightSurface surface;
    @Inject private Injector injector;
    @Inject private PropertiesService props;
    @Inject private TaskService tasks;

    private List<Sprite> sprites = new LinkedList<>();
    private Map<Integer, List<Sprite>> collisionGroups = new HashMap<>();

    public <T extends Sprite> T make(Class<T> clazz) {
        return make(clazz, null);
    }

    public <T extends Sprite> T make(Class<T> clazz, Integer collisionGroup) {
        T sprite = injector.getInstance(clazz);
        props.injectPropertiesOrExit(sprite);
        sprites.add(sprite);
        if ( collisionGroup!=null ) {
            List<Sprite> group = collisionGroups.get(collisionGroup);
            if ( group==null ) {
                group = new LinkedList<>();
                collisionGroups.put(collisionGroup, group);
            }
            group.add(sprite);
        }
        sprite.init();
        return sprite;
    }

    private Timer animationTimer = new Timer();

    public void reset() {
        sprites.forEach(Sprite::reset);
    }

    private boolean running = false;

    public void start() {
        sprites.forEach(Sprite::start);
        tasks.addRepeatingTask(this, refreshInterval, () -> {
            if ( running ) {
                Set<Integer> layersToClear = new HashSet<Integer>();
                sprites.forEach((sprite1) -> {
                    sprite1.updatePosition();
                    layersToClear.add(sprite1.getLayer());
                });
                collisionGroups.forEach((key,list)->{
                    list.forEach((sprite)->{
                        list.forEach((innerSprite)->{
                            if ( sprite.getId()!=innerSprite.getId() ) {
                                if ( sprite.distanceTo(innerSprite) <= (sprite.getCollisionRadius()+innerSprite.getCollisionRadius())) {
                                    if ( collisionHandler!=null ) {
                                        collisionHandler.accept(sprite, innerSprite);
                                    }
                                }
                            }
                        });

                    });
                });
                layersToClear.forEach((layerNumber) -> surface.layer(layerNumber).clear());
                sprites.forEach(Sprite::render);
            }
        });
        running = true;
    }

    public int[] commonCentre(Sprite s1, Sprite s2) {
        int x = Math.abs(s1.position(X) - s2.position(X));
        int y = Math.abs(s1.position(Y) - s2.position(Y));
        return new int[]{ x, y };
    }

    public void stop() {
        sprites.forEach(Sprite::stop);
        running = false;
    }

    public void pause() {
        running = false;
    }

    public void resume() {
        running = true;
    }

    public void setRefresh(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    private long refreshInterval = 10;

    private BiConsumer<Sprite, Sprite> collisionHandler;

    public void onCollide(BiConsumer<Sprite, Sprite> handler) {
        collisionHandler = handler;
    }

}
