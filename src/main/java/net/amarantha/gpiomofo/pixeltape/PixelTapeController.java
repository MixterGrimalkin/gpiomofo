package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.module.TapeRefresh;
import net.amarantha.gpiomofo.pixeltape.pattern.PixelTapePattern;
import net.amarantha.gpiomofo.service.task.TaskService;

import java.util.LinkedList;
import java.util.List;

@Singleton
public class PixelTapeController {

    private TaskService tasks;
    private PixelTape pixelTape;
    private int tapeRefresh;

    private List<PixelTapePattern> patterns = new LinkedList<>();
    private int totalPixels;

    @Inject
    public PixelTapeController(PixelTape pixelTape, @TapeRefresh int tapeRefresh, TaskService tasks) {
        this.pixelTape = pixelTape;
        this.tapeRefresh = tapeRefresh;
        this.tasks = tasks;
    }

    public PixelTapeController addPattern(PixelTapePattern pattern) {
        patterns.add(pattern);
        return this;
    }

    public void init(int totalPixels) {
        this.totalPixels = totalPixels;
        pixelTape.init(totalPixels);
    }

    public void start() {
        System.out.println("Starting PixelTape..." + tapeRefresh);
        tasks.addRepeatingTask(this, tapeRefresh, this::render);
    }

    public void stop() {
        pixelTape.close();
        System.out.println("Shutting down pixel tape");
    }

    public void render() {
        for ( PixelTapePattern pattern : patterns ) {
            if ( pattern.isActive() ) {
                int start = pattern.getStartPixel();
                RGB[] patternContents = pattern.render();
                for (int i = 0; i < patternContents.length; i++) {
                    RGB rgb = patternContents[i];
                    if (rgb != null) {
                        pixelTape.setPixelColourRGB(start + i, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
                    }
                }
            }
        }
        pixelTape.render();
    }

    public void stopAll() {
        stopAll(true);
    }

    public void stopAll(boolean clearPixels) {
        patterns.forEach(PixelTapePattern::stop);
        if ( clearPixels ) {
            pixelTape.allOff();
        }
    }

    public void setAll(RGB colour) {
        for ( int i=0; i<totalPixels; i++ ) {
            pixelTape.setPixelColourRGB(i, colour);
        }
    }

    public RGB getPixel(int pixel) {
        return pixelTape.getPixelRGB(pixel);
    }

}
