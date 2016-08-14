package net.amarantha.gpiomofo.pixeltape;

import com.diozero.ws281xj.WS281x;
import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.task.TaskService;

public abstract class PixelTapePattern {

    public static final int PWM_GPIO = 18;

    protected WS281x pixelTape;

    private int pixelCount;

    private TaskService tasks;

    @Inject
    public PixelTapePattern(TaskService tasks) {
        this.tasks = tasks;
    }

    public void init(int pixelCount) {
        this.pixelCount = pixelCount;
        pixelTape = new WS281x(PWM_GPIO, 255, pixelCount);
    }

    public int getPixelCount() {
        return pixelCount;
    }

    protected abstract void update();

    private Thread runnerThread;

    private boolean alive;
    private boolean paused;

    public void start() {
        alive = true;
        paused = false;
        tasks.addRepeatingTask(this, 1, this::update);
    }

    private void tick() {
        while ( alive ) {
            update();
            while ( paused ) {}
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void stop() {
        alive = false;
        paused = false;
        pixelTape.allOff();
    }

    private double speed = 0;
    private double intensity = 0;

    public double getSpeed() {
        return speed;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    protected void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
