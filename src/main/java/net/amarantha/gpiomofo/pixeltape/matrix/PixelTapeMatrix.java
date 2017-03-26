package net.amarantha.gpiomofo.pixeltape.matrix;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.utility.TimeGuard;
import net.amarantha.utils.colour.RGB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.amarantha.gpiomofo.utility.Utility.log;

@Singleton
public class PixelTapeMatrix {

    @Inject private NeoPixel neoPixel;
    @Inject private TaskService tasks;
    @Inject private TimeGuard guard;

    private RGB[][] pixels;
    private int width;
    private int height;

    public void init(int width, int height, boolean alternateRows) {
        log("Starting Pixel Tape Matrix " + width + " x " + height);
        this.width = width;
        this.height = height;
        pixels = new RGB[width][height];
        neoPixel.init(width * height);
        tasks.addRepeatingTask("Matrix", 100, this::refresh);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    ///////////////
    // Animation //
    ///////////////

    private Animation animation;

    public void startAnimation(Animation animation) {
        stopAnimation();
        animation.start();
        this.animation = animation;
    }

    public void stopAnimation() {
        if ( animation!=null ) {
            animation.stop();
        }
    }

    /////////////
    // Refresh //
    /////////////

    private void refresh() {
        updateFoci();
        if ( animation!=null ) {
            guard.every(animation.getRefreshInterval(), "Animation", () -> animation.refresh());
        }
        render();
    }

    private void render() {
        if ( dirty ) {
            int pixel = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    neoPixel.setPixelColourRGB(pixel++, pixels[x][y]);
                }
            }
            neoPixel.render();
            dirty = false;
        }
    }

    //////////
    // Foci //
    //////////

    private Map<Integer, Integer[]> foci = new HashMap<>();
    private Map<Integer, Long> cancelledFoci = new HashMap<>();
    private long persistFocusDelay = 1000;

    public void addFocus(int id, int x, int y) {
        foci.put(id, new Integer[]{x, y});
        if ( animation!=null ) {
            animation.onFocusAdded(id);
        }
    }

    public void removeFocus(int id) {
        cancelledFoci.put(id, System.currentTimeMillis());
    }

    Map<Integer, Integer[]> foci() {
        return foci;
    }

    private void updateFoci() {
        List<Integer> fociToRemove = new ArrayList<>();
        cancelledFoci.forEach((id, time) -> {
            if (System.currentTimeMillis() - time >= persistFocusDelay) {
                fociToRemove.add(id);
            }
        });
        if (!fociToRemove.isEmpty()) {
            fociToRemove.forEach((id) -> {
                cancelledFoci.remove(id);
                foci.remove(id);
            });
            if ( animation!=null ) {
                animation.onFocusRemoved(fociToRemove);
            }
        }
    }

    /////////////
    // Drawing //
    /////////////

    private boolean dirty = false;

    void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = RGB.BLACK;
            }
        }
        dirty = true;
    }

    void draw(int x, int y, RGB colour) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            dirty = true;
            pixels[x][y] = colour;
        }
    }

}
