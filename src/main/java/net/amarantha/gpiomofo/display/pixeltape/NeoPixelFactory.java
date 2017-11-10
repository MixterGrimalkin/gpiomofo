package net.amarantha.gpiomofo.display.pixeltape;

import net.amarantha.utils.task.TaskService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class NeoPixelFactory {

    @Inject private TaskService tasks;
    @Inject private NeoPixel neoPixel;

    private int updateInterval = 10;

    private Map<Integer, Pixel> pixels = new HashMap<>();

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public Pixel create(int number) {
        Pixel p = new Pixel(neoPixel, updateInterval, number);
        pixels.put(number, p);
        return p;
    }

    public Pixel get(int number) {
        return pixels.get(number);
    }

    public void start() {
        neoPixel.allOff();
        tasks.addRepeatingTask(TASK_NAME, updateInterval, () -> {
            pixels.forEach((i, pixel) -> pixel.update());
            neoPixel.render();
        });
    }

    public void stop() {
        tasks.removeTask(TASK_NAME);
        neoPixel.allOff();
    }

    private final static String TASK_NAME = "PixelFactoryUpdate";

}
