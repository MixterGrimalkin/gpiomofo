package net.amarantha.gpiomofo.display.pixeltape;

import com.google.inject.Inject;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.time.TimeGuard;

import java.util.function.Consumer;

import static net.amarantha.utils.colour.RGB.BLACK;

public class PixelAnimation {

    @Inject private TimeGuard guard;
    @Inject private NeoPixel neoPixel;

    private int frameRate;
    private int updateInterval;

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public void setRenderer(Consumer<PixelAnimation> renderer) {
        this.renderer = renderer;
    }

    public Consumer<PixelAnimation> getRenderer() {
        return renderer;
    }

    private RGB[] frame;

    public void init(int pixelCount) {
        frame = new RGB[pixelCount];
    }

    private Consumer<PixelAnimation> renderer;

    private boolean running = false;

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public boolean update() {
        if ( running ) {
            guard.every(frameRate, this, this::render);
        }
        return running;
    }

    protected void render() {
        renderer.accept(this);
    }

    public RGB[] getFrame() {
        return frame;
    }

    public void clear() {
        fill(0, frame.length, BLACK);
    }

    public void draw(int p, RGB rgb) {
        if ( p >= 0 && p < frame.length ) {
            frame[p] = rgb;
        }
    }

    public void fill(int startP, int endP, RGB rgb) {
        for ( int p = startP; p <= endP; p++ ) {
            draw(p, rgb);
        }
    }

}
