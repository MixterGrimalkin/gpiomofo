package net.amarantha.gpiomofo.pixeltape;

public interface PixelTape {

    void init(int pixelCount);
    void setPixelColourRGB(int pixel, RGB rgb);
    void setPixelColourRGB(int pixel, int red, int green, int blue);
    void render();

}
