package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.service.task.TaskService;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Singleton
public class PixelTapeController {

    @Inject private TaskService tasks;
    @Inject private PixelTape pixelTape;

    private Map<Integer, PixelTapePattern> patterns = new HashMap<>();
    private int totalPixels;

    public PixelTapeController addPattern(int startPixel, PixelTapePattern pattern) {
        patterns.put(startPixel, pattern);
        return this;
    }

    public void init(int totalPixels) {
        this.totalPixels = totalPixels;
        pixelTape.init(totalPixels);
        System.out.println("Initialising WS281x...");
    }

    public void start() {
        System.out.println("Starting PixelTape...");
        tasks.addRepeatingTask(this, 1, this::render);
    }

    public void render() {
        for ( Entry<Integer, PixelTapePattern> entry : patterns.entrySet() ) {
            int start = entry.getKey();
            RGB[] pattern = entry.getValue().render();
            for ( int i=0; i<pattern.length; i++ ) {
                RGB rgb = pattern[i];
                pixelTape.setPixelColourRGB(start+i, rgb.getGreen(), rgb.getRed(), rgb.getBlue());
            }
        }
        pixelTape.render();
    }

}
