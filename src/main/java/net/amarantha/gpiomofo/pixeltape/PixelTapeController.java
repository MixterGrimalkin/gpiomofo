package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.module.TapeRefresh;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.gpiomofo.service.task.TaskService;

import java.util.LinkedList;
import java.util.List;

@Singleton
public class PixelTapeController {

    private TaskService tasks;
    private NeoPixel neoPixel;
    private int tapeRefresh;

    private List<PixelTapeTarget> patterns = new LinkedList<>();
    private int totalPixels;

    @Inject
    public PixelTapeController(NeoPixel neoPixel, @TapeRefresh int tapeRefresh, TaskService tasks) {
        this.neoPixel = neoPixel;
        this.tapeRefresh = tapeRefresh;
        this.tasks = tasks;
    }

    public PixelTapeController addPattern(PixelTapeTarget pattern) {
        patterns.add(pattern);
        return this;
    }

    public PixelTapeController init(int totalPixels) {
        this.totalPixels = totalPixels;
        neoPixel.init(totalPixels);
        return this;
    }

    public void start() {
        System.out.println("Starting PixelTape...");
        tasks.addRepeatingTask(this, tapeRefresh, this::render);
    }

    public void stop() {
        neoPixel.close();
        System.out.println("Shutting down pixel tape");
    }

    public void render() {
        for ( PixelTapeTarget pattern : patterns ) {
            if ( pattern.isRunning() ) {
                int start = pattern.getStartPixel();
                RGB[] patternContents = pattern.render();
                for (int i = 0; i < patternContents.length; i++) {
                    RGB rgb = patternContents[i];
                    if (rgb != null) {
                        neoPixel.setPixelColourRGB(start + i, rgb, pattern.isForceRGB());
                    }
                }
            }
        }
        neoPixel.render();
    }

    public void stopAll() {
        stopAll(true);
    }

    public void stopAll(boolean clearPixels) {
        patterns.forEach(PixelTapeTarget::stop);
        if ( clearPixels ) {
            neoPixel.allOff();
        }
    }

    public void setAll(RGB colour) {
        for ( int i=0; i<totalPixels; i++ ) {
            neoPixel.setPixelColourRGB(i, colour);
        }
    }

    public RGB getPixel(int pixel) {
        return neoPixel.getPixelRGB(pixel);
    }

}
