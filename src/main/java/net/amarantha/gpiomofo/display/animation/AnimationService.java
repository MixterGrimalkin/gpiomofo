package net.amarantha.gpiomofo.display.animation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Animation;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.TimeGuard;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class AnimationService {

    @Inject private TaskService tasks;
    @Inject private TimeGuard guard;

    private Map<String, Animation> animations = new HashMap<>();
    private Map<String, Boolean> animationActive = new HashMap<>();

    public AnimationService add(String name, Animation animation) {
        animations.put(name, animation);
        animationActive.put(name, false);
        animation.init();
        return this;
    }

    public void start() {
        tasks.addRepeatingTask("AnimationService", 10, this::refresh);
    }

    private void refresh() {
        animations.forEach((name, animation) -> {
            if ( animationActive.get(name) ) {
                guard.every(animation.getRefreshInterval(), "UpdateAnimation" + name, animation::refresh);
            }
        });
    }

    public void play(String name) {
        if ( !animationActive.get(name) ) {
            animations.get(name).start();
            animationActive.put(name, true);
        }
    }

    public void stop(String name) {
        if ( animationActive.get(name) ) {
            animations.get(name).stop();
            animationActive.put(name, false);
        }
    }

    public void stopAll() {
        animations.forEach((name,animation)-> stop(name));
    }



}
