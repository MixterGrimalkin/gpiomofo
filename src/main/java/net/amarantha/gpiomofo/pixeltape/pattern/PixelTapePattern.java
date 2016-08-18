package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.utility.Now;

public abstract class PixelTapePattern {

    @Inject private Now now;
    @Inject private PixelTapeController controller;

    private int startPixel;

    protected RGB[] currentPattern;

    public RGB[] render() {
        if ( currentPattern==null ) {
            throw new IllegalStateException("Pattern not initialised");
        }
        if ( now.epochMilli()-lastRefreshed >= refreshInterval ) {
            update();
            lastRefreshed = now.epochMilli();
        }
        return currentPattern;
    }

    public void init(int startPixel, int pixelCount) {
        if ( currentPattern!=null ) {
            throw new IllegalStateException("Pattern already initialised");
        }
        this.startPixel = startPixel;
        this.pixelCount = pixelCount;
        currentPattern = new RGB[pixelCount];
    }

    public int getStartPixel() {
        return startPixel;
    }

    protected void setPixel(int pixel, RGB rgb) {
        setPixel(pixel, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }

    protected void setPixel(int pixel, int red, int green, int blue) {
        currentPattern[pixel] = new RGB(red, green, blue);
    }

    protected RGB getPixel(int pixel) {
        return controller.getPixel(pixel);
    }

    protected void setAll(RGB colour) {
        for ( int i=0; i<pixelCount; i++ ) {
            currentPattern[i] = colour;
        }

    }

    protected abstract void update();

    private long refreshInterval = 10;
    private long lastRefreshed;

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public PixelTapePattern setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
        return this;
    }

    protected void clear() {
        for ( int i=0; i<currentPattern.length; i++ ) {
            currentPattern[i] = new RGB(0,0,0);
        }
    }

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void start() {
        active = true;
    }

    public void stop() {
        active = false;
    }

    ////////////
    // Config //
    ////////////

    private int pixelCount;
    private double speed = 0;
    private double intensity = 0;

    public int getPixelCount() {
        return pixelCount;
    }

    public double getSpeed() {
        return speed;
    }

    public double getIntensity() {
        return intensity;
    }

    public PixelTapePattern setPixelCount(int pixelCount) {
        this.pixelCount = pixelCount;
        return this;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

}
