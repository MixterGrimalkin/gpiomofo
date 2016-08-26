package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.gpiomofo.utility.Now;

public class SlidingBars extends PixelTapeTarget {

    @Inject private Now now;
    private int barSize = 10;
    private int spaceSize = 5;
    private int offset = 0;
    private RGB colour = new RGB(255, 255, 255);
    private RGB backColour = new RGB(0, 0, 0);

    public SlidingBars setColour(RGB colour) {
        this.colour = colour;
        return this;
    }

    public SlidingBars setSpaceSize(int spaceSize) {
        this.spaceSize = spaceSize;
        return this;
    }

    private long timeStarted = -1;
    private long fadeInTime = 1000;
    private double fadeDelta;
    private double fadeValue;
    private boolean reverse;

    public SlidingBars setReverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    public SlidingBars setFadeInTime(long fadeInTime) {
        this.fadeInTime = fadeInTime;
        return this;
    }

    private int refreshInitial;
    private int refreshMain;
    private int refreshIncrement;

    public SlidingBars setRefreshRange(int initial, int main, int increment) {
        this.refreshInitial = initial;
        this.refreshMain = main;
        this.refreshIncrement = increment;
        setRefreshInterval(initial);
        return this;
    }

    @Override
    public SlidingBars setRefreshInterval(long refreshInterval) {
        super.setRefreshInterval(refreshInterval);
        return this;
    }

    @Override
    protected void update() {
        if ( refreshIncrement!=0 ) {
            long refresh = getRefreshInterval();
            if ( (refreshIncrement>0 && refresh < refreshMain)
                    || (refreshIncrement<0 && refresh > refreshMain )) {
                setRefreshInterval(refresh+refreshIncrement);
            }
        }
        if ( fadeDelta > 0 && fadeValue < 1.0 ) {
            fadeValue += fadeDelta;
            if ( fadeValue>1.0 ) {
                fadeValue = 1.0;
            }
        }
        int p = 0;

        while (p < getPixelCount()) {
            drawBar(p + offset, barSize, colour.withBrightness(fadeValue));
            p += barSize;
            drawBar(p + offset, spaceSize, backColour);
            p += spaceSize;
        }
        if ( reverse ) {
            offset--;
            if (offset < 0) {
                offset = getPixelCount()-1;
            }
        } else {
            offset++;
            if (offset >= getPixelCount()) {
                offset = 0;
            }
        }
        if ( barSizeDelta!=0 ) {
            barSize += dBarSize;
            spaceSize -= dBarSize;
            if (barSize <= minBarSize) {
                barSize = minBarSize;
                dBarSize = barSizeDelta;
            } else if (barSize >= maxBarSize) {
                barSize = maxBarSize;
                dBarSize = -barSizeDelta;
            }
        }
    }

    private int minBarSize;
    private int maxBarSize;
    private int barSizeDelta;
    private int dBarSize;

    public SlidingBars setBarChange(int minBarSize, int maxBarSize, int delta) {
        this.minBarSize = minBarSize;
        this.maxBarSize = maxBarSize;
        this.barSizeDelta = delta;
        dBarSize = barSizeDelta;
        return this;
    }

    private void drawBar(int startPixel, int size, RGB rgb) {
        for (int i = 0; i < size; i++) {
            int p = (startPixel + i) % getPixelCount();
            setPixel(p, rgb);
        }
    }

    @Override
    public void start() {
        super.start();
        if ( refreshIncrement!=0 ) {
            setRefreshInterval(refreshInitial);
        }
        if ( fadeInTime>0 ) {
            fadeValue = 0.0;
            fadeDelta = 1.0 / ((double) fadeInTime / (double) getRefreshInterval());
        } else {
            fadeValue = 1.0;
        }
    }

    public SlidingBars setBarSize(int barSize, int spaceSize) {
        this.barSize = barSize;
        this.spaceSize = spaceSize;
        return this;
    }

}