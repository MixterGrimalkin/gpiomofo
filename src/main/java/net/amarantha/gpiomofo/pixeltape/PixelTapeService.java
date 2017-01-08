package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.module.TapeRefresh;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.utils.colour.RGB;

import java.util.LinkedList;
import java.util.List;

@Singleton
public class PixelTapeService {

    private TaskService tasks;
    private NeoPixel neoPixel;
    private int tapeRefresh;

    private List<PixelTapeTarget> patterns = new LinkedList<>();
    private int totalPixels;

    @Inject
    public PixelTapeService(NeoPixel neoPixel, @TapeRefresh int tapeRefresh, TaskService tasks) {
        this.neoPixel = neoPixel;
        this.tapeRefresh = tapeRefresh;
        this.tasks = tasks;
    }

    public PixelTapeService addPattern(PixelTapeTarget pattern) {
        patterns.add(pattern);
        totalPixels = Math.max(totalPixels, pattern.getStartPixel()+pattern.getPixelCount());
        return this;
    }

    public void start() {
        System.out.println("Starting PixelTape Service...");
        neoPixel.init(totalPixels);
        tasks.addRepeatingTask(this, tapeRefresh, this::render);
    }

    public void stop() {
        System.out.println("Stopping PixelTape Service...");
        neoPixel.close();
    }

    public void render() {
        for ( PixelTapeTarget pattern : patterns ) {
            if ( pattern.isRunning() ) {
                int start = pattern.getStartPixel();
                RGB[] patternContents = pattern.render();
                for (int i = 0; i < patternContents.length; i++) {
                    RGB rgb = patternContents[pattern.isReverse()?patternContents.length-i-1:i];
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
