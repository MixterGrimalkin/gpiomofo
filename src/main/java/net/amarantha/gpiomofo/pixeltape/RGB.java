package net.amarantha.gpiomofo.pixeltape;

public class RGB {

    public static final RGB WHITE = new RGB(255,255,255);

    private final int red;
    private final int green;
    private final int blue;

    public RGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RGB withBrightness(double brightness) {
        return new RGB((int)Math.round(red*brightness), (int)Math.round(green*brightness), (int)Math.round(blue*brightness));
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public String textValue() {
        return (red==0&&green==0&&blue==0 ) ? "-" : "X" ;
    }

    @Override
    public String toString() {
        return "RGB{"+red+","+green+","+blue+"}";
    }
}
