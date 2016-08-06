package net.amarantha.gpiomofo.service.task;

public class Task {

    private Long lastFired;
    private Long interval;
    private boolean repeating;

    private TaskCallback callback;

    Task(Long created, Long interval, boolean repeating, TaskCallback callback) {
        this.lastFired = created;
        this.interval = interval;
        this.repeating = repeating;
        this.callback = callback;
    }

    void run(long time) {
        this.lastFired = time;
        callback.call();
    }

    Long getLastFired() {
        return lastFired;
    }

    Long getInterval() {
        return interval;
    }

    boolean isRepeating() {
        return repeating;
    }

    TaskCallback getCallback() {
        return callback;
    }

    public interface TaskCallback {
        void call();
    }

}
