package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.utility.Now;

public abstract class PixelTapePattern {

    @Inject private Now now;

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

    public void init(int pixelCount) {
        if ( currentPattern!=null ) {
            throw new IllegalStateException("Pattern already initialised");
        }
        this.pixelCount = pixelCount;
        currentPattern = new RGB[pixelCount];
    }

    protected void setPixel(int pixel, RGB rgb) {
        setPixel(pixel, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }

    protected void setPixel(int pixel, int red, int green, int blue) {
        currentPattern[pixel] = new RGB(red, green, blue);
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

    public void start() {

    }

    public void stop() {

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
