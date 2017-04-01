package net.amarantha.gpiomofo.display.pixeltape;

import net.amarantha.utils.colour.RGB;

public interface NeoPixel {

    void init(int pixelCount);
    void close();
    void allOff();

    void setPixelColourRGB(int pixel, RGB rgb);
    void setPixelColourRGB(int pixel, RGB colour, boolean forceRGB);
    void setPixelColourRGB(int pixel, int red, int green, int blue);

    RGB getPixelRGB(int pixel);

    void render();

    void setMasterBrightness(double brightness);
    double getMasterBrightness();

}
