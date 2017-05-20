package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;

import java.util.List;

public abstract class Animation {

    @Inject protected LightSurface surface;
    @Inject protected OldSpriteFactory sprites;

    public abstract void start();

    public abstract void stop();

    public abstract void refresh();

    public abstract void onFocusAdded(int focusId);

    public abstract void onFocusRemoved(List<Integer> focusIds);

    private long refreshInterval = 80;

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

}
