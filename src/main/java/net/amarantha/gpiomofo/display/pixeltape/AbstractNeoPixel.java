package net.amarantha.gpiomofo.display.pixeltape;

import net.amarantha.utils.colour.RGB;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractNeoPixel implements NeoPixel {

    private RGB[] pixels;

    private Map<Integer, Consumer<RGB>> interceptors = new HashMap<>();

    @Override
    public void init(int pixelCount) {
        pixels = new RGB[pixelCount];
        fireInteceptors();
    }

    public int getPixelCount() {
        return pixels.length - interceptors.size();
    }

    protected Integer getAdjustedPixel(int pixel) {
        if ( interceptors.isEmpty() ) {
            return pixel;
        } else if ( interceptors.get(pixel)==null ) {
            int regress = 0;
            for (Map.Entry<Integer, Consumer<RGB>> entry : interceptors.entrySet() ) {
                if ( entry.getKey() < pixel ) {
                    regress++;
                }
            }
            return pixel - regress;
        }
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void intercept(int pixel, Consumer<RGB> interceptor) {
        interceptors.put(pixel, interceptor);
    }

    @Override
    public void render() {
        fireInteceptors();
    }

    private void fireInteceptors() {
        if ( !interceptors.isEmpty() ) {
            interceptors.forEach((pixel, interceptor)-> interceptor.accept(pixels[pixel]));
        }
    }

    @Override
    public void setPixel(int pixel, RGB colour) {
        pixels[pixel] = colour;
    }

    @Override
    public final void setPixel(int pixel, int red, int green, int blue) {
        setPixel(pixel, new RGB(red, green, blue));
    }

    @Override
    public RGB getPixel(int pixel) {
        return pixels[pixel];
    }

    @Override
    public void allOff() {
        for ( int i=0; i<pixels.length; i++ ) {
            pixels[i] = new RGB(0,0,0);
        }
        fireInteceptors();
    }

    private double masterBrightness = 1.0;

    @Override
    public void setMasterBrightness(double brightness) {
        this.masterBrightness = brightness;
    }

    @Override
    public double getMasterBrightness() {
        return masterBrightness;
    }

}
