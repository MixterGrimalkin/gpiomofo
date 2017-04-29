package net.amarantha.gpiomofo.display.pixeltape;

import net.amarantha.utils.colour.RGB;

import static net.amarantha.utils.shell.Utility.log;

public class NeoPixelMock implements NeoPixel{

    private RGB[] pixels;

    @Override
    public void init(int pixelCount) {
        log("Starting Mock NeoPixel...");
        pixels = new RGB[pixelCount];
    }

    @Override
    public void close() {

    }

    @Override
    public void allOff() {
        for ( int i=0; i<pixels.length; i++ ) {
            pixels[i] = new RGB(0,0,0);
        }
    }

    @Override
    public void setPixelColourRGB(int pixel, RGB colour) {
        pixels[pixel] = colour;
    }

//    @Override
//    public void setPixelColourRGB(int pixel, RGB colour, boolean forceRGB) {
//        pixels[pixel] = colour;
//    }

    @Override
    public void setPixelColourRGB(int pixel, int red, int green, int blue) {
        setPixelColourRGB(pixel, new RGB(red, green, blue));
    }

    @Override
    public RGB getPixelRGB(int pixel) {
        return pixels[pixel];
    }

    @Override
    public void render() {

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
