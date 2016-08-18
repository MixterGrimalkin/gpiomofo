package net.amarantha.gpiomofo.pixeltape;

public interface PixelTape {

    void init(int pixelCount);
    void setPixelColourRGB(int pixel, RGB rgb);

    void setPixelColourRGB(int pixel, RGB colour, boolean forceRGB);

    void setPixelColourRGB(int pixel, int red, int green, int blue);
    RGB getPixelRGB(int pixel);
    void render();
    void close();
    void allOff();
    void setMasterBrightness(double brightness);
    double getMasterBrightness();

}
