package net.amarantha.gpiomofo.display.pixeltape;

import com.diozero.ws281xj.WS281x;
import com.google.inject.Inject;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;

import static net.amarantha.utils.shell.Utility.log;

@PropertyGroup("WS281x")
public class NeoPixelWS281X implements NeoPixel {

    @Inject private PropertiesService props;

    @Property("PwmPin") private int pin = 18;
    @Property("DMA") private int dma = 5;
    @Property("Frequency") private int frequency = 800000;
    @Property("ColourMode") private ColourMode colourMode = ColourMode.RGB;

    private WS281x ws281x;

    private double masterBrightness = 1.0;

    private int pixelCount;

    @Override
    public void init(int pixelCount) {
        log("Starting Native WS281x NeoPixel...");
        props.injectPropertiesOrExit(this, (type, value)->{
            System.out.println(value);
            if (type==ColourMode.class) {
                return ColourMode.valueOf(value);
            } else {
                return null;
            }
        });
        System.out.println("actual="+colourMode);
        this.pixelCount = pixelCount;
        ws281x = new WS281x(frequency, dma, pin, 255, pixelCount);
    }

    @Override
    public void setPixelColourRGB(int pixel, RGB colour) {
        if ( pixel < pixelCount && !colour.equals(getPixelRGB(pixel))) {
            dirty = true;
            RGB rgb = colour.withBrightness(masterBrightness);
            if (colourMode==ColourMode.RGB) {
                ws281x.setPixelColourRGB(pixel, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
            } else {
                ws281x.setPixelColourRGB(pixel, rgb.getGreen(), rgb.getRed(), rgb.getBlue());
            }
        }
    }

    private boolean dirty = false;

    @Override
    public void setPixelColourRGB(int pixel, int red, int green, int blue) {
        setPixelColourRGB(pixel, new RGB(red, green, blue));
    }

    @Override
    public void render() {
        if ( dirty ) {
            ws281x.render();
        }
        dirty = false;
    }

    @Override
    public void close() {
        if ( ws281x!=null ) {
            ws281x.close();
        }
    }

    @Override
    public void allOff() {
        if ( ws281x!=null ) {
            ws281x.allOff();
        }
    }


    @Override
    public void setMasterBrightness(double brightness) {
        masterBrightness = brightness;
    }

    @Override
    public double getMasterBrightness() {
        return masterBrightness;
    }

    @Override
    public RGB getPixelRGB(int pixel) {
        if ( ws281x!=null ) {
            return new RGB(ws281x.getGreenComponent(pixel), ws281x.getRedComponent(pixel), ws281x.getBlueComponent(pixel));
        }
        return null;
    }
}
