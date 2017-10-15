package net.amarantha.gpiomofo.display.pixeltape;

import com.diozero.ws281xj.WS281x;
import com.google.inject.Inject;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;

import static net.amarantha.utils.shell.Utility.log;

@PropertyGroup("WS281x")
public class NeoPixelWS281X extends AbstractNeoPixel {

    @Inject private PropertiesService props;

    @Property("PwmPin") private int pin = 18;
    @Property("DMA") private int dma = 5;
    @Property("Frequency") private int frequency = 800000;
    @Property("ColourMode") private ColourMode colourMode = ColourMode.RGB;

    private WS281x ws281x;

    @Override
    public void init(int pixelCount) {
        log("Starting Native WS281x NeoPixel...");
        super.init(pixelCount);
        props.injectPropertiesOrExit(this, (type, value)->{
            if (type==ColourMode.class) {
                return ColourMode.valueOf(value);
            } else {
                return null;
            }
        });
        ws281x = new WS281x(frequency, dma, pin, 255, getPixelCount());
    }

    @Override
    public void setPixel(int pixel, RGB colour) {
        super.setPixel(pixel, colour);
        Integer adjustedPixel = getAdjustedPixel(pixel);
        if ( adjustedPixel!=null && adjustedPixel < getPixelCount() ) { //&& !colour.equals(super.getPixel(pixel))) {
            dirty = true;
            RGB rgb = colour.withBrightness(getMasterBrightness());
            if (colourMode==ColourMode.RGB) {
                ws281x.setPixelColourRGB(adjustedPixel, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
            } else {
                ws281x.setPixelColourRGB(adjustedPixel, rgb.getGreen(), rgb.getRed(), rgb.getBlue());
            }
        }
    }

    private boolean dirty = false;

    @Override
    public void render() {
        super.render();
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
        super.allOff();
        if ( ws281x!=null ) {
            ws281x.allOff();
        }
    }


    @Override
    public RGB getPixel(int pixel) {
        if ( ws281x!=null ) {
            return new RGB(ws281x.getGreenComponent(pixel), ws281x.getRedComponent(pixel), ws281x.getBlueComponent(pixel));
        }
        return null;
    }
}
