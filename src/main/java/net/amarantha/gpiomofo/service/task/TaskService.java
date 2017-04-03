package net.amarantha.gpiomofo.service.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.service.task.Task.TaskCallback;
import net.amarantha.utils.time.Now;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Singleton
public class TaskService {

    @Inject private Now now;

    private Map<Object, Task> tasks = new HashMap<>();
    private Map<Object, Task> newTasks = new HashMap<>();
    private List<Object> deadTaskKeys = new LinkedList<>();

    private Thread taskThread;

    private boolean run = false;

    public synchronized void addTask(Object key, long interval, TaskCallback callback) {
        newTasks.put(key, new Task(now.epochMilli(), interval, false, callback));
    }

    public synchronized void addRepeatingTask(Object key, long interval, TaskCallback callback) {
        newTasks.put(key, new Task(now.epochMilli(), interval, true, callback));
    }

    public synchronized void removeTask(Object key) {
        if ( tasks.containsKey(key) ) {
            deadTaskKeys.add(key);
        }
    }

    public void start() {
        System.out.println("Starting Task AbstractService...");
        run = true;
        taskThread = new Thread() {
            @Override
            public void run() {
                while ( run ) {
                    scanTasks();
                }
            }
        };
        taskThread.start();
    }

    public void stop() {
        System.out.println("Stopping Task AbstractService...");
        run = false;
        taskThread = null;
    }

    public synchronized void scanTasks() {
        for ( Entry<Object, Task> entry : newTasks.entrySet() ) {
            tasks.put(entry.getKey(), entry.getValue());
        }
        newTasks.clear();
        for ( Entry<Object, Task> entry : tasks.entrySet() ) {
            if ( checkAndRun(entry.getValue()) ) {
                deadTaskKeys.add(entry.getKey());
            }
        }
        for ( Object key : deadTaskKeys ) {
            tasks.remove(key);
        }
        deadTaskKeys.clear();
    }

    /**
     * Check task, and run if necessary
     * @param task The task
     * @return <code>true</code> if task is now completed
     */
    private boolean checkAndRun(Task task) {
        if ( (now.epochMilli() - task.getLastFired()) >= task.getInterval() ) {
            task.run(now.epochMilli());
            if ( !task.isRepeating() ) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        tasks.clear();
        newTasks.clear();
        deadTaskKeys.clear();
    }

}
