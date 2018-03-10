package net.amarantha.gpiomofo.display.pixeltape;

import com.google.inject.Injector;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.task.TaskService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static net.amarantha.utils.colour.RGB.BLACK;
import static net.amarantha.utils.math.MathUtils.round;

@Singleton
public class LayeredNeoPixelFactory {

    @Inject  private TaskService tasks;
    @Inject  private Injector injector;
    @Inject  private NeoPixel neoPixel;

    private int updateInterval = 10;

    private Map<Integer, NeoPixelFactory> layers = new HashMap<>();

    public void initialize(int layerCount, int pixelCount) {
        for ( int i=0; i<layerCount; i++ ) {
            createLayer(i, pixelCount);
        }
        layer(0).eachPixel((p, pixel)->{
            pixel.rgb(BLACK);
            pixel.jump(1.0);
        });
        neoPixel.init(pixelCount);
    }

    public LayeredNeoPixelFactory createLayer(int number, int pixelCount) {
        NeoPixelFactory factory = injector.getInstance(NeoPixelFactory.class);
        factory.createAllPixels(pixelCount);
        layers.put(number, factory);
        return this;
    }

    public NeoPixelFactory layer(int number) {
        return layers.get(number);
    }

    private Map<Integer, RGB> pixels = new HashMap<>();

    public void start() {
        neoPixel.allOff();
        tasks.addRepeatingTask(TASK_NAME, updateInterval, () -> {
            update();
            render();
        });
    }

    public void update() {
        pixels = new HashMap<>();
        layers.forEach((i, layer)->{
            layer.update();
            layer.eachPixel((p, pixel) ->
                pixels.put(p, overlay(pixels.get(p), pixel.rgb(), pixel.current()))
            );
        });
    }

    public void render() {
        pixels.forEach((p, pixel)-> neoPixel.setPixel(p, pixel));
        neoPixel.render();
    }

    private RGB overlay(RGB baseColour, RGB overlayColour, double opacity) {
        if ( opacity == 0.0 || overlayColour==null) {
            return baseColour;
        }
        if ( opacity == 1.0 || baseColour==null ) {
            return overlayColour;
        }
        int baseR = baseColour.getRed();
        int baseG = baseColour.getGreen();
        int baseB = baseColour.getBlue();
        int overR = overlayColour.getRed();
        int overG = overlayColour.getGreen();
        int overB = overlayColour.getBlue();
        int r = round(baseR + ((overR - baseR)*opacity));
        int g = round(baseG + ((overG - baseG)*opacity));
        int b = round(baseB + ((overB - baseB)*opacity));
        return new RGB(r, g, b);
    }

    public void stop() {
        tasks.removeTask(TASK_NAME);
        neoPixel.allOff();
    }

    private final static String TASK_NAME = "LayeredPixelFactoryUpdate";

}
