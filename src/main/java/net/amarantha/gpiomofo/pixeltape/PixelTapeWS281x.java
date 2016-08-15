package net.amarantha.gpiomofo.pixeltape;

import com.diozero.ws281xj.WS281x;

public class PixelTapeWS281x implements PixelTape {

    public static final int PWM_GPIO = 18;

    private WS281x ws281x;

    @Override
    public void init(int pixelCount) {
        ws281x = new WS281x(PWM_GPIO, 255, pixelCount);
    }

    @Override
    public void setPixelColourRGB(int pixel, RGB rgb) {
        ws281x.setPixelColourRGB(pixel, rgb.getGreen(), rgb.getRed(), rgb.getBlue());
    }

    @Override
    public void setPixelColourRGB(int pixel, int red, int green, int blue) {
        ws281x.setPixelColourRGB(pixel, green, red, blue);

    }

    @Override
    public void render() {
        ws281x.render();
    }
}
