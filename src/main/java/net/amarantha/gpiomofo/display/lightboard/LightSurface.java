package net.amarantha.gpiomofo.display.lightboard;

import com.google.inject.Injector;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.entity.Region;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.service.AbstractService;
import net.amarantha.utils.task.TaskService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static net.amarantha.utils.colour.RGB.BLACK;

@Singleton
@PropertyGroup("LightSurface")
public class LightSurface extends AbstractService {

    @Property("LayerCount")
    private int layerCount = 10;
    @Property("Width")
    private int width;
    @Property("Height")
    private int height;
    @Property("BoardClass")
    private Class<? extends LightBoard> lightBoardClass;

    private LightBoard board;

    @Inject
    private Injector injector;

    @Inject
    private TaskService tasks;
    @Inject
    private PropertiesService props;

    public static final int BG = 0;
    public static final int FG = -1;

    private Pattern[] layers = new Pattern[layerCount];
    private Region boardRegion;

    public LightSurface() {
        super("Light Surface");
    }

    @Override
    public void onStart() {

        props.injectPropertiesOrExit(this);

        board = injector.getInstance(lightBoardClass);

        layers[0] = new Pattern(width, height, false);
        for (int i = 1; i < layerCount; i++) {
            layers[i] = new Pattern(width, height, true);
        }

        boardRegion = safeRegion(0, 0, width, height);

        if (board.needsOwnThread()) {
            new Thread(() -> board.init(width, height)).start();
        } else {
            board.init(width, height);
        }

        tasks.addRepeatingTask("SurfaceRefresh", board.interval(), () -> board.update(composite().rgb()));
    }

    @Override
    protected void onStop() {
        board.shutdown();
    }

    private Map<Integer, RGB> colouriseLayers = new HashMap<>();

    public void colouriseLayer(int layer, RGB colour) {
        colouriseLayers.put(layer, colour);
    }

    public Pattern composite() {
        Pattern result = new Pattern(width, height, false);
        result.eachPixel((x, y, c) -> {
            for (int i = 0; i < layerCount; i++) {
                RGB rgb = layers[i].rgb(x, y);
                RGB colourise = colouriseLayers.get(i);
                if (rgb != null) {
                    if (colourise == null || i == 0) {
                        result.draw(x, y, rgb);
                    } else {
                        if ( result.rgb(x, y)!=null && !result.rgb(x, y).equals(BLACK)) {
                            result.draw(x, y, colourise);
                        }
                    }
                }
            }
        });
        return result;
    }

    public void clear() {
        for (int i = 0; i < layerCount; i++) {
            layer(i).fill(null);
        }
    }

    public Pattern layer(int layer) {
        return layers[MathUtils.bound(0, layerCount - 1, layer == FG ? layerCount - 1 : layer)];
    }

    ////////////
    // Region //
    ////////////

    public Region safeRegion(int left, int top, int width, int height) {
        if (left < 0) {
            width += left;
        }
        if (top < 0) {
            height += top;
        }
        int safeLeft = left < 0 ? 0 : left >= width() ? width() - 1 : left;
        int safeTop = top < 0 ? 0 : top >= height() ? height() - 1 : top;
        int safeWidth = safeLeft + width > width() ? width() - safeLeft : width;
        int safeHeight = safeTop + height > height() ? height() - safeTop : height;
        return new Region(safeLeft, safeTop, safeWidth, safeHeight);
    }

    protected boolean pointInRegion(int x, int y, Region region) {
        if (region == null) {
            region = safeRegion(0, 0, height(), width());
        }
        return (x >= region.left && x <= region.right && y >= region.top && y <= region.bottom);
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

    public int[] size() {
        return new int[]{width, height};
    }

}
