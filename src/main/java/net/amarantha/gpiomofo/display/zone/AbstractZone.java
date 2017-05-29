package net.amarantha.gpiomofo.display.zone;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.entity.*;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.display.zone.transition.AbstractTransition;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.task.TaskService;

public abstract class AbstractZone {

    @Inject private LightSurface surface;
    @Inject private TaskService sync;
//    @Inject private SceneLoader sceneLoader;

    /**
     * Implement to obtain the next pattern to display
     */
    protected abstract Pattern getNextPattern();

    /////////////////
    // Init & Tick //
    /////////////////

    public void init() {
        initialised = true;
        paused = true;
        if ( region==null ) {
            region = surface.getBoardRegion();
        }
        if ( id==null ) {
            id = ""+nextId++;
        }
        if ( standalone ) {
            sync.addRepeatingTask(this, tick, ()->{
                if (!paused) {
                    tick();
                }
            });
        }
    }

    public final void setTick(long tick) {
        if ( initialised ) {
            throw new IllegalStateException("Must call setTick() before init()");
        }
        this.tick = tick;
    }

    public final void setStandalone(boolean standalone) {
        if ( initialised ) {
            throw new IllegalStateException("Must call setStandalone() before init()");
        }
        this.standalone = standalone;
    }

    public final void pause() {
        paused = true;
        surface.layer(canvasLayer).clearRegion(region);
    }

    public final void tick() {
        if ( !paused ) {
            switch (direction) {
                case IN:
                    inTransition.tick();
                    break;
                case DISPLAY:
                    doTick();
                    break;
                case OUT:
                    outTransition.tick();
                    break;
            }
            if (outline != null) {
                surface.layer(canvasLayer).outlineRegion(region, outline);
            }
        }
    }

    private void doTick() {
        if ( pattern==null ) {
            pattern = getNextPattern();
            if ( pattern!=null ) {
                in();
            }
        } else {
            if (startTime != null && System.currentTimeMillis() - startTime > displayTime) {
                startTime = null;
                if ( onDisplayComplete !=null ) {
                    onDisplayComplete.execute();
                }
                if (autoOut) {
                    out();
                }
            }
        }
    }

    /////////////
    // Drawing //
    /////////////

    public void drawPoint(int x, int y, RGB colour) {
        surface.layer(canvasLayer).draw(x+region.left+xOffset, y+region.top+yOffset, colour);
    }

    public void drawPattern(int x, int y, Pattern pattern) {
        if ( pattern!=null ) {
            surface.layer(canvasLayer).draw(x + region.left + xOffset, y + region.top + yOffset, pattern);
        }
    }

    public void clear() {
        surface.layer(canvasLayer).clearRegion(region);
    }

    public AbstractZone setOutline(RGB outline) {
        this.outline = outline;
        return this;
    }

    ///////////////////
    // Display Cycle //
    ///////////////////

    public final AbstractZone in() {
        if ( paused ) {
            paused = false;
        } else {
            surface.layer(canvasLayer).clearRegion(region);
            if (inTransition != null) {
                inTransition.transition(this, this::display, onInAt, onInAtProgress);
                direction = Transitioning.IN;
            } else {
                display();
            }
        }
        return this;
    }

    public final AbstractZone display() {
        if ( onInComplete!=null ) {
            onInComplete.execute();
        }
        if ( displayTime==0 ) {
            drawPattern(getRestX(), getRestY(), pattern);
            out();
        } else {
            startTime = System.currentTimeMillis();
            direction = Transitioning.DISPLAY;
            drawPattern(getRestX(), getRestY(), pattern);
        }
        return this;
    }

    public final AbstractZone out() {
        if ( outTransition!=null ) {
            outTransition.transition(this, this::end, onOutAt, onOutAtProgress);
            direction = Transitioning.OUT;
        } else {
            end();
        }
        return this;
    }

    public final AbstractZone end() {
        surface.layer(canvasLayer).clearRegion(region);
        direction = Transitioning.DISPLAY;
        if ( onOutComplete !=null ) {
            onOutComplete.execute();
        }
        pattern = getNextPattern();
        if ( autoNext ) {
            if ( pattern != null ) {
                in();
            }
        }
        return this;
    }

    public final AbstractZone reset() {
        switch ( direction ) {
            case IN:
                break;
            case DISPLAY:
                break;
            case OUT:
                break;
        }
        return this;
    }

    /////////////////
    // Transitions //
    /////////////////

    public enum Transitioning {
        IN, DISPLAY, OUT
    }

    public AbstractZone setInTransition(AbstractTransition inTransition) {
        this.inTransition = inTransition;
        return this;
    }

    public AbstractZone setOutTransition(AbstractTransition outTransition) {
        this.outTransition = outTransition;
        return this;
    }

    public AbstractZone setDisplayTime(long displayTime) {
        this.displayTime = displayTime;
        return this;
    }

    public AbstractZone setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
        return this;
    }

    public AbstractZone setAutoOut(boolean autoOut) {
        this.autoOut = autoOut;
        return this;
    }

    public AbstractZone setAutoNext(boolean autoNext) {
        this.autoNext = autoNext;
        return this;
    }

    ///////////////
    // Callbacks //
    ///////////////

    public interface ZoneCallback {
        void execute();
    }

    private ZoneCallback onInAt;
    private double onInAtProgress;
    private ZoneCallback onInComplete;
    private ZoneCallback onDisplayComplete;
    private ZoneCallback onOutAt;
    private double onOutAtProgress;
    private ZoneCallback onOutComplete;

    public final void onInAt(double progress, ZoneCallback callback) {
        this.onInAtProgress = progress;
        this.onInAt = callback;
    }

    public final void onInComplete(ZoneCallback callback) {
        this.onInComplete = callback;
    }

    public final void onDisplayComplete(ZoneCallback callback) {
        this.onDisplayComplete = callback;
    }

    public final void onOutAt(double progress, ZoneCallback callback) {
        this.onOutAtProgress = progress;
        this.onOutAt = callback;
    }

    public final void onOutComplete(ZoneCallback callback) {
        this.onOutComplete = callback;
    }

    ////////////
    // Domino //
    ////////////

    private void doZone(Domino domino, AbstractZone zone) {
        switch ( domino ) {
            case IN:
                zone.in();
                break;
            case OUT:
                zone.out();
                break;
            case EXIT:
        }
    }

    public final AbstractZone whenInAt(double progress, final Domino domino, final AbstractZone... zones) {
        onInAt(progress, () ->{
            for ( AbstractZone zone : zones ) {
                doZone(domino, zone);
            }
        });
        return this;
    }

    public final AbstractZone afterIn(final Domino domino, final AbstractZone... zones) {
        onInComplete(() ->{
            for ( AbstractZone zone : zones ) {
                doZone(domino, zone);
            }
        });
        return this;
    }

    public final AbstractZone afterDisplay(final Domino domino, final AbstractZone... zones) {
        onDisplayComplete(() ->{
            for ( AbstractZone zone : zones ) {
                doZone(domino, zone);
            }
        });
        return this;
    }

    public final AbstractZone whenOutAt(double progress, final Domino domino, final AbstractZone... zones) {
        onOutAt(progress, () ->{
            for ( AbstractZone zone : zones ) {
                doZone(domino, zone);
            }
        });
        return this;
    }

    public final AbstractZone afterOut(final Domino domino, final AbstractZone... zones) {
        onOutComplete(() ->{
            for ( AbstractZone zone : zones ) {
                doZone(domino, zone);
            }
        });
        return this;
    }

    //////////////////////////
    // Position & Alignment //
    //////////////////////////

    public AbstractZone setRegion(Region region) {
        this.region = region;
        return this;
    }

    public AbstractZone setRegion(int left, int top, int width, int height) {
        return setRegion(surface.safeRegion(left, top, width, height));
    }

    public AbstractZone setOffsetX(int xOffset) {
        this.xOffset = xOffset;
        return this;
    }

    public AbstractZone setOffsetY(int yOffset) {
        this.yOffset = yOffset;
        return this;
    }

    public AbstractZone setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        return this;
    }

    public AbstractZone setCanvasLayer(int canvasLayer) {
        this.canvasLayer = canvasLayer;
        return this;
    }

    public AbstractZone setAlignV(AlignV alignV) {
        this.alignV = alignV;
        return this;
    }

    public AbstractZone setAlignH(AlignH alignH) {
        this.alignH = alignH;
        return this;
    }

    public void setId(String id) {
        if ( initialised ) {
            throw new IllegalStateException("Must call setId() before init()");
        }
        this.id = id;
    }

    /////////////
    // Getters //
    /////////////

    public String getId() {
        return id;
    }

    public int getRestX() {
        if ( pattern!=null ) {
            switch (alignH) {
                case LEFT:
                    return 0;
                case CENTRE:
                    return (region.width - pattern.getWidth()) / 2;
                case RIGHT:
                    return region.width - pattern.getWidth();
            }
        }
        return 0;
    }

    public int getRestY() {
        if ( pattern!=null ) {
            switch (alignV) {
                case TOP:
                    return 0;
                case MIDDLE:
                    return (region.height - pattern.getHeight()) / 2;
                case BOTTOM:
                    return region.height - pattern.getHeight();
            }
        }
        return 0;
    }

    public int getLeft() {
        return region.left;
    }

    public int getRight() {
        return region.right;
    }

    public int getTop() {
        return region.top;
    }

    public int getBottom() {
        return region.bottom;
    }

    public int getWidth() {
        return region.width;
    }

    public int getHeight() {
        return region.height;
    }

    public AlignH getAlignH() {
        return alignH;
    }

    public AlignV getAlignV() {
        return alignV;
    }

    public long getTick() {
        return tick;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean isStandalone() {
        return standalone;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public Transitioning getDirection() {
        return direction;
    }

    ////////////
    // Fields //
    ////////////

    private String id;
    private Pattern pattern;

    private Region region;
    private int canvasLayer = 0;
    private int xOffset = 0;
    private int yOffset = 0;
    private AlignH alignH = AlignH.CENTRE;
    private AlignV alignV = AlignV.MIDDLE;

    private Transitioning direction = Transitioning.DISPLAY;
    private AbstractTransition inTransition;
    private AbstractTransition outTransition;

    private boolean standalone = false;
    private boolean paused = false;

    private Long startTime;
    private long displayTime = 1000;

    private long tick = 25;
    private boolean initialised = false;

    private boolean autoStart = false;
    private boolean autoOut = false;
    private boolean autoNext = false;

    private RGB outline = null;

    private static int nextId = 0;

    @Override
    public String toString() {
        return getClass().getName()+": "+getId();
    }

}
