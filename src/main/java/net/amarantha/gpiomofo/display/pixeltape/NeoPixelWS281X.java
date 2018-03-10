package net.amarantha.gpiomofo.display.pixeltape;

import com.diozero.ws281xj.WS281x;
import com.google.inject.Inject;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;

import static net.amarantha.utils.colour.RGB.BLACK;
import static net.amarantha.utils.math.MathUtils.round;
import static net.amarantha.utils.shell.Utility.log;

@PropertyGroup("WS281x")
public class NeoPixelWS281X extends AbstractNeoPixel {

    @Inject private PropertiesService props;

    @Property("PwmPin") private int pin = 18;
    @Property("DMA") private int dma = 5;
    @Property("Frequency") private int frequency = 800000;
    @Property("ColourMode") private ColourMode colourMode = ColourMode.RGB;
    @Property("PadWhite") private boolean padWhite = false;

    private WS281x ws281x;
    private int realPixelCount;

    @Override
    public void init(int pixelCount) {
        log("Starting Native WS281x NeoPixel...");
        props.injectPropertiesOrExit(this, (type, value)->{
            if (type==ColourMode.class) {
                return ColourMode.valueOf(value);
            } else {
                return null;
            }
        });
        super.init(pixelCount);
        realPixelCount = padWhite ? round(pixelCount*(4.0/3.0)) : getPixelCount();
        ws281x = new WS281x(frequency, dma, pin, 255, realPixelCount);
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
            if ( padWhite ) {
                int p = 0;
                for ( int i=0; i<getPixelCount(); i+=3 ) {
                    RGB a = i < getPixelCount() ? super.getPixel(i) : BLACK;
                    RGB b = (i+1) < getPixelCount() ? super.getPixel(i+1) : BLACK;
                    RGB c = (i+2) < getPixelCount() ? super.getPixel(i+2) : BLACK;
                    if (colourMode==ColourMode.RGB) {
                        if ( p < realPixelCount ) ws281x.setPixelColourRGB(p,       a.getRed(), a.getGreen(), a.getBlue());
                        if ( p+1 < realPixelCount ) ws281x.setPixelColourRGB(p+1,   0, b.getRed(), b.getGreen());
                        if ( p+2 < realPixelCount ) ws281x.setPixelColourRGB(p+2,   b.getBlue(), 0, c.getRed());
                        if ( p+3 < realPixelCount ) ws281x.setPixelColourRGB(p+3,   c.getGreen(), c.getBlue(), 0);
                    } else {
                        if ( p < realPixelCount ) ws281x.setPixelColourRGB(p, a.getGreen(), a.getRed(), a.getBlue());
                        if ( p+1 < realPixelCount ) ws281x.setPixelColourRGB(p+1,   0, b.getGreen(), b.getRed());
                        if ( p+2 < realPixelCount ) ws281x.setPixelColourRGB(p+2,   b.getBlue(), 0, c.getGreen());
                        if ( p+3 < realPixelCount ) ws281x.setPixelColourRGB(p+3,   c.getRed(), c.getBlue(), 0);
                    }
                    p += 4;
                }
            }
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
