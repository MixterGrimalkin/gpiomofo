package net.amarantha.gpiomofo.pixeltape;

public class RotatingBars extends PixelTapePattern {

    private int barSize = 10;
    private int spaceSize = 5;
    private int offset = 0;
    private RGB colour = new RGB(255,255,255);
    private RGB backColour = new RGB(0,0,0);

    public RotatingBars setColour(RGB colour) {
        this.colour = colour;
        return this;
    }

    @Override
    protected void update() {
        int p = 0;
        while ( p<getPixelCount() ) {
            drawBar(p+offset, barSize, colour);
            p += barSize;
            drawBar(p+offset, spaceSize, backColour);
            p += spaceSize;
        }
        offset++;
        if ( offset>=getPixelCount() ) {
            offset = 0;
        }
        barSize += dBarSize;
        spaceSize -= dBarSize;
        if ( barSize<=minBarSize ) {
            barSize = minBarSize;
            dBarSize = 1;
        } else if ( barSize>=maxBarSize ) {
            barSize = maxBarSize;
            dBarSize = -1;
        }
    }

    private int maxBarSize = 12;
    private int minBarSize = 5;
    private int dBarSize = 1;

    private void drawBar(int startPixel, int size, RGB rgb) {
        for ( int i=0; i<size; i++ ) {
            int p = (startPixel + i) % getPixelCount();
            setPixel(p, rgb);
        }
    }

    @Override
    public void init(int pixelCount) {
        super.init(pixelCount);
    }
}
