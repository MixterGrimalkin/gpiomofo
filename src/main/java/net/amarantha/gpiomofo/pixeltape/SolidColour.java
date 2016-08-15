package net.amarantha.gpiomofo.pixeltape;

public class SolidColour extends PixelTapePattern {

    private RGB colour;

    public SolidColour setColour(RGB rgb) {
        colour = rgb;
        return this;
    }

    @Override
    protected void update() {
        for ( int i=0; i<getPixelCount(); i++ ) {
            setPixel(i, colour);
        }
    }
}
