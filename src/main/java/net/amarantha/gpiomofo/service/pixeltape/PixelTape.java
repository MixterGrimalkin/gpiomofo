package net.amarantha.gpiomofo.service.pixeltape;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.Property;
import net.amarantha.utils.properties.PropertyGroup;

import java.util.LinkedList;
import java.util.List;

import static net.amarantha.utils.shell.Utility.log;

@Singleton
@PropertyGroup("PixelTape")
public class PixelTape {

    private List<PixelTapeTarget> patterns = new LinkedList<>();
    private int totalPixels;

    @Inject private PropertiesService props;
    @Inject private NeoPixel neoPixel;
    @Inject private TaskService tasks;

    // -------------------------------------------------------------------------------------------
    // Patterns must be added before starting PixelTape
    // -------------------------------------------------------------------------------------------
    PixelTape addPattern(PixelTapeTarget pattern) {
        patterns.add(pattern);
        totalPixels = Math.max(totalPixels, pattern.getStartPixel()+pattern.getPixelCount());
        return this;
    }
    // -------------------------------------------------------------------------------------------

    @Property("TapeRefresh") private int tapeRefresh = 5;

    public void start() {
        log("Starting PixelTape...");
        props.injectPropertiesOrExit(this);
        neoPixel.init(totalPixels);
        tasks.addRepeatingTask(this, tapeRefresh, this::render);
    }

    public void stop() {
        log("Stopping PixelTape...");
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
