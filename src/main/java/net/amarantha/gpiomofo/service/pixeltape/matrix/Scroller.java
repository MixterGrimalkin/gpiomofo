package net.amarantha.gpiomofo.service.pixeltape.matrix;

import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.entity.Region;

import java.util.List;

public class Scroller extends Animation {

    private Pattern nextPattern;
    private Pattern currentPattern;

    public void setPattern(Pattern pattern) {
        nextPattern = pattern;
    }

    public void init(int layer, Region region) {
        this.layer = layer;
        this.region = region;
        surface.layer(layer).clipTo(region);
    }

    private Region region;
    private int x;
    private int y;
    private int deltaX;

    private int layer = 0;

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void init() {
        if ( region==null ) {
            region = new Region(0,0,surface.width(),surface.height());
        }
    }

    @Override
    public void start() {
        x = region.right+1;
        deltaX = -1;
    }

    @Override
    public void stop() {
        x = region.right+1;
        deltaX = 0;
    }

    @Override
    public void refresh() {
        if ( currentPattern==null ) {
            currentPattern = nextPattern;
            nextPattern = null;
        }
        if ( currentPattern!=null ) {
            x += deltaX;
            if (x < (region.left - currentPattern.getWidth())) {
                if ( nextPattern!=null ) {
                    currentPattern = nextPattern;
                    nextPattern = null;
                }
                x = region.right + 1;
            }
            surface.layer(layer)
                    .clear()
                    .draw(x, y, currentPattern);
        }
    }

    @Override
    public void onFocusAdded(int focusId) {

    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {

    }
}
