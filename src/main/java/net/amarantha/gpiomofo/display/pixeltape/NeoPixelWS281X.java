package net.amarantha.gpiomofo.display.pixeltape;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.pixeltape.ws281xj.WS281x;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.Property;
import net.amarantha.utils.properties.PropertyGroup;

@PropertyGroup("WS281x")
public class NeoPixelWS281X implements NeoPixel {

    @Inject private PropertiesService props;

    @Property("PwmPin") private int pin = 18;

    private WS281x ws281x;

    private double masterBrightness = 1.0;

    private int pixelCount;

    @Override
    public void init(int pixelCount) {
        props.injectPropertiesOrExit(this);
        this.pixelCount = pixelCount;
        ws281x = new WS281x(pin, 255, pixelCount);
    }

    @Override
    public void setPixelColourRGB(int pixel, RGB colour) {
        setPixelColourRGB(pixel, colour, false);
    }

    @Override
    public void setPixelColourRGB(int pixel, RGB colour, boolean forceRGB) {
        if ( pixel < pixelCount && !colour.equals(getPixelRGB(pixel))) {
            dirty = true;
            RGB rgb = colour.withBrightness(masterBrightness);
            if (forceRGB) {
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
