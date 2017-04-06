package net.amarantha.gpiomofo.display.animation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Animation;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.TimeGuard;

@Singleton
public class AnimationService {

    @Inject private TaskService tasks;
    @Inject private TimeGuard guard;

    private Animation animation;

    public void start() {
        tasks.addRepeatingTask("AnimationService", 10, this::refresh);
    }

    private void refresh() {
        if ( animation!=null ) {
            guard.every(animation.getRefreshInterval(), "UpdateAnimation", animation::refresh);
        }
    }

    public void play(Animation animation) {
        this.animation = animation;
    }




}
