package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.module.TapeRefresh;
import net.amarantha.gpiomofo.pixeltape.pattern.PixelTapePattern;
import net.amarantha.gpiomofo.service.task.TaskService;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Singleton
public class PixelTapeController {

    private TaskService tasks;
    private PixelTape pixelTape;
    private int tapeRefresh;

    private Map<Integer, PixelTapePattern> patterns = new HashMap<>();
    private int totalPixels;

    @Inject
    public PixelTapeController(PixelTape pixelTape, @TapeRefresh int tapeRefresh, TaskService tasks) {
        this.pixelTape = pixelTape;
        this.tapeRefresh = tapeRefresh;
        this.tasks = tasks;
    }

    public PixelTapeController addPattern(int startPixel, PixelTapePattern pattern) {
        patterns.put(startPixel, pattern);
        return this;
    }

    public void init(int totalPixels) {
        this.totalPixels = totalPixels;
        pixelTape.init(totalPixels);
        System.out.println("inited");
    }

    public void start() {
        System.out.println("Starting PixelTape..." + tapeRefresh);
        tasks.addRepeatingTask(this, tapeRefresh, this::render);
    }

    public void render() {
        for ( Entry<Integer, PixelTapePattern> entry : patterns.entrySet() ) {
            int start = entry.getKey();
            RGB[] pattern = entry.getValue().render();
            for ( int i=0; i<pattern.length; i++ ) {
                RGB rgb = pattern[i];
                if ( rgb!=null ) {
                    pixelTape.setPixelColourRGB(start + i, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
                }
            }
        }
        pixelTape.render();
    }

}
