package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.entity.HasEnable;
import net.amarantha.gpiomofo.factory.entity.HasName;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.Now;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.out;
import static net.amarantha.utils.shell.Utility.log;

public abstract class Target implements HasName, HasEnable {

    @Inject private TargetFactory targets;
    @Inject private Now now;

    @Service private TaskService tasks;

    @Parameter("oneShot") private boolean oneShot = false;
    @Parameter("triggerState") private boolean triggerState = true;
    @Parameter("followTrigger") private boolean followTrigger = true;
    @Parameter("clearDelay") private Long clearDelay = null;

    private boolean active = false;
    public Target offTarget;

    public final boolean isActive() {
        return active;
    }

    @Override
    public void enable() {

    }

    /////////////
    // Trigger //
    /////////////

    public void processTrigger(boolean inputState) {
        if ( inputState==triggerState) {
            if ( !active ) {
                activate();
            }
        } else if (followTrigger) {
            deactivate();
        }
    }

    //////////////
    // Activate //
    //////////////

    public final void activate() {
        String time = now.time().toString();
        if ( locked ) {
            log(time + ": -XX- [" + getName() + "]");
        } else {
            log(time + ": " + (oneShot ? "--" : "==") + ">> [" + getName() + "]");
            if ( lockTime!=null ) {
                for ( Target target : lockTargets ) {
                    target.lockFor(lockTime);
                }
            }
            tasks.removeTask(this);
            if (!oneShot) {
                if (clearDelay != null) {
                    tasks.addTask(this, clearDelay, this::deactivate);
                }
                active = true;
            }
            onActivate();
        }
    }

    protected abstract void onActivate();

    ////////////////
    // Deactivate //
    ////////////////

    protected final void deactivate(boolean force) {
        if ( active || force ) {
            out.println(now.time().toString() + ": -//- [" + getName() + "]");
            tasks.removeTask(this);
            active = false;
            onDeactivate();
        }
    }

    public final void deactivate() {
        deactivate(false);
    }

    protected abstract void onDeactivate();

    /////////////
    // Locking //
    /////////////

    private Long lockTime;

    public Target lock(Long lockTime, Target... ts) {
        this.lockTargets.addAll(Arrays.asList(ts));
        this.lockTime = lockTime;
        return this;
    }

    private List<Target> lockTargets = new LinkedList<>();

    private boolean locked;

    private void lockFor(long lockTime) {
        locked = true;
        tasks.addTask("Lock"+getName(), lockTime, this::unlock);
    }

    private void unlock() {
        locked = false;
    }


    //////////////////
    // Cancellation //
    //////////////////

    public Target cancel() {
        return targets.cancel(this).setForce(true);
    }


    ///////////
    // Setup //
    ///////////

    public Target oneShot(boolean oneShot) {
        this.oneShot = oneShot;
        return this;
    }

    public Target triggerState(boolean triggerState) {
        this.triggerState = triggerState;
        return this;
    }

    public Target followTrigger(boolean followTrigger) {
        this.followTrigger = followTrigger;
        return this;
    }

    public Target clearDelay(Long clearDelay) {
        this.clearDelay = clearDelay;
        return this;
    }

    public boolean isOneShot() {
        return oneShot;
    }

    public boolean getTriggerState() {
        return triggerState;
    }

    public boolean isFollowTrigger() {
        return followTrigger;
    }

    public Long getClearDelay() {
        return clearDelay;
    }

    //////////
    // Name //
    //////////

    private String name;

    @Override
    public void setName(String name) {
        this.name = name.replaceAll(" ", "-");
    }

    @Override
    public String getName() {
        return name;
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Target target = (Target) o;
        return name != null ? name.equals(target.name) : target.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
