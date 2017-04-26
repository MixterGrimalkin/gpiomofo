package net.amarantha.gpiomofo.service.pixeltape;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.utils.task.TaskService;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.service.AbstractService;

import java.util.LinkedList;
import java.util.List;

@Singleton
@PropertyGroup("PixelTape")
public class PixelTape extends AbstractService {

    private List<PixelTapeTarget> patterns = new LinkedList<>();
    private int totalPixels;

    @Inject private PropertiesService props;
    @Inject private NeoPixel neoPixel;
    @Inject private TaskService tasks;

    public PixelTape() {
        super("Pixel Tape");
    }

    // -------------------------------------------------------------------------------------------
    // Patterns must be added before starting PixelTape
    // -------------------------------------------------------------------------------------------
    public PixelTape addPattern(PixelTapeTarget pattern) {
        patterns.add(pattern);
        totalPixels = Math.max(totalPixels, pattern.getStartPixel()+pattern.getPixelCount());
        return this;
    }
    // -------------------------------------------------------------------------------------------

    @Property("TapeRefresh") private int tapeRefresh = 5;

    @Override
    public void onStart() {
        neoPixel.init(totalPixels);
        for ( int i=0; i<15; i++) {
            neoPixel.setPixelColourRGB(i, 0, 255, 0);
        }
        neoPixel.render();
        tasks.addRepeatingTask(this, tapeRefresh, this::render);
    }

    @Override
    public void onStop() {
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
