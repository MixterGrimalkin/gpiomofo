package net.amarantha.gpiomofo.pixeltape;

public class PixelTapeMock implements PixelTape {

    @Override
    public void init(int pixelCount) {

    }

    @Override
    public void setPixelColourRGB(int pixel, RGB rgb) {
        System.out.print(rgb.textValue());
    }

    @Override
    public void setPixelColourRGB(int pixel, int red, int green, int blue) {
        setPixelColourRGB(pixel, new RGB(red, green, blue));
    }

    @Override
    public void render() {
        System.out.println();
    }
}
