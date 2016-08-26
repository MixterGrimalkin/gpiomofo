package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.utility.TimeGuard;

public abstract class PixelTapeTarget extends Target {

    @Inject private PixelTapeController pixelTapeController;

    @Inject private TimeGuard guard;
    private boolean forceRGB;
    private boolean requiresRender = true;

    public boolean isRequiresRender() {
        return requiresRender;
    }

    public void setRequiresRender(boolean requiresRender) {
        this.requiresRender = requiresRender;
    }

    @Override
    protected void onActivate() {
        start();
    }

    @Override
    protected void onDeactivate() {
        stop();
    }

    private int startPixel;

    protected RGB[] currentPattern;

    protected abstract void update();

    public RGB[] render() {
        if ( currentPattern==null ) {
            throw new IllegalStateException("Pattern not initialised");
        }
        guard.every(refreshInterval, "render", this::update);
        return currentPattern;
    }

    public PixelTapeTarget init(int startPixel, int pixelCount) {
        if ( currentPattern!=null ) {
            throw new IllegalStateException("Pattern already initialised");
        }
        this.startPixel = startPixel;
        this.pixelCount = pixelCount;
        currentPattern = new RGB[pixelCount];
        pixelTapeController.addPattern(this);
        return this;
    }

    protected void setPixel(int pixel, RGB rgb) {
        setPixel(pixel, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }

    protected void setPixel(int pixel, int red, int green, int blue) {
//        int actualPixel = reverse ? currentPattern.length-pixel-1 : pixel;
        currentPattern[pixel] = new RGB(red, green, blue);
    }

    private boolean reverse;

    public PixelTapeTarget setReverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    public boolean isReverse() {
        return reverse;
    }

    protected RGB getPixel(int pixel) {
        return pixelTapeController.getPixel(pixel);
    }

    protected void setAll(RGB colour) {
        for ( int i=0; i<pixelCount; i++ ) {
            currentPattern[i] = colour;
        }

    }

    private long refreshInterval = 10;
    private long lastRefreshed;

    protected void clear() {
        for ( int i=0; i<currentPattern.length; i++ ) {
            currentPattern[i] = new RGB(0,0,0);
        }
    }

    private boolean running;

    public boolean isRunning() {
        return running;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public int getStartPixel() {
        return startPixel;
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

    public PixelTapeTarget setPixelCount(int pixelCount) {
        this.pixelCount = pixelCount;
        return this;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public boolean isForceRGB() {
        return forceRGB;
    }

    public void setForceRGB(boolean forceRGB) {
        this.forceRGB = forceRGB;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public PixelTapeTarget setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
        return this;
    }

}
