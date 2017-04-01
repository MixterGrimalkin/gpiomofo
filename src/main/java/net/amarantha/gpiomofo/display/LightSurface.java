package net.amarantha.gpiomofo.display;

import com.google.inject.Injector;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.entity.Region;
import net.amarantha.gpiomofo.display.lightboard.LightBoard;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.Property;
import net.amarantha.utils.properties.PropertyGroup;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.amarantha.gpiomofo.service.shell.Utility.log;

@Singleton
@PropertyGroup("LightSurface")
public class LightSurface {

    @Property("LayerCount") private int layerCount = 10;
    @Property("Width") private int width;
    @Property("Height") private int height;
    @Property("BoardClass") private Class<? extends LightBoard> lightBoardClass;

    private LightBoard board;

    @Inject private Injector injector;

    @Inject private TaskService tasks;
    @Inject private PropertiesService props;

    public static final int BG = 0;
    public static final int FG = -1;

    private Pattern[] layers = new Pattern[layerCount];
    private Region boardRegion;

    public LightSurface init() {

        log("Starting LightSurface....");

        props.injectPropertiesOrExit(this);

        board = injector.getInstance(lightBoardClass);

        layers[0] = new Pattern(width, height, false);
        for (int i = 1; i< layerCount; i++ ) {
            layers[i] = new Pattern(width, height, true);
        }

        boardRegion = safeRegion(0, 0, width, height);

        if (board.needsOwnThread()) {
            new Thread(() -> board.init(width, height)).start();
        } else {
            board.init(width, height);
        }

        tasks.addRepeatingTask("SurfaceRefresh", board.interval(), () -> board.update(composite().rgb()));

        log("Surface Active");

        return this;
    }

    public Pattern composite() {
        Pattern result = new Pattern(width, height, false);
        result.eachPixel((x,y,c)->{
            for (int i = layerCount -1; i>=0; i-- ) {
                RGB rgb = layers[i].rgb(x, y);
                if ( rgb!=null ) {
                    result.draw(x,y,rgb);
                }
            }
        });
        return result;
    }

    public void clear() {
        for (int i = 0; i< layerCount; i++ ) {
            layer(i).fill(null);
        }
    }

    public Pattern layer(int layer) {
        return layers[MathUtils.bound(0, layerCount -1, layer==FG ? layerCount -1 : layer )];
    }

    ////////////
    // Region //
    ////////////

    public Region safeRegion(int left, int top, int width, int height) {
        if ( left<0 ) {
            width += left;
        }
        if ( top<0 ) {
            height += top;
        }
        int safeLeft = left<0 ? 0 : left>= width() ? width()-1 : left;
        int safeTop = top<0 ? 0 : top>= height() ? height()-1 : top;
        int safeWidth = safeLeft+width > width() ? width()-safeLeft : width;
        int safeHeight = safeTop+height > height() ? height()-safeTop : height;
        return new Region(safeLeft, safeTop, safeWidth, safeHeight);
    }

    protected boolean pointInRegion(int x, int y, Region region) {
        if ( region==null ) {
            region = safeRegion(0, 0, height(), width());
        }
        return ( x>=region.left && x<=region.right && y>=region.top && y<=region.bottom );
    }

    public Region getBoardRegion() {
        return boardRegion;
    }

    public int height() {
        return height;
    }

    public int width() {
        return width;
    }

}
