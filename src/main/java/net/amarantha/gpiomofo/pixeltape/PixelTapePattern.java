package net.amarantha.gpiomofo.pixeltape;

import com.diozero.ws281xj.WS281x;
import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.task.TaskService;

public abstract class PixelTapePattern {

    @Inject private TaskService tasks;

    public static final int PWM_GPIO = 18;

    protected WS281x pixelTape;


    protected abstract void update();


    public void start() {
        System.out.println("Starting WS281x...");
        pixelTape = new WS281x(PWM_GPIO, 255, pixelCount);
        tasks.addRepeatingTask(this, 1, this::update);
    }

    public void stop() {
        pixelTape.close();
    }

    protected void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ////////////
    // Config //
    ////////////

    private int pixelCount;
    private double speed = 0;
    private double intensity = 0;

    public int getPixelCount() {
        return pixelCount;
    }

    public double getSpeed() {
        return speed;
    }

    public double getIntensity() {
        return intensity;
    }

    public PixelTapePattern setPixelCount(int pixelCount) {
        this.pixelCount = pixelCount;
        return this;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

}
