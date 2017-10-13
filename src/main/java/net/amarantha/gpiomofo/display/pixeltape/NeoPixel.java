package net.amarantha.gpiomofo.display.pixeltape;

import net.amarantha.utils.colour.RGB;

import java.util.function.Consumer;

public interface NeoPixel {

    void init(int pixelCount);
    void allOff();
    void close();

    void setPixel(int pixel, RGB rgb);
    void setPixel(int pixel, int red, int green, int blue);

    RGB getPixel(int pixel);

    void render();

    void setMasterBrightness(double brightness);
    double getMasterBrightness();

    void intercept(int pixel, Consumer<RGB> interceptor);

}
