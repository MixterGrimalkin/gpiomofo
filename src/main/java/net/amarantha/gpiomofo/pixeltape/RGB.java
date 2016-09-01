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
        int r = (int)Math.round(Math.min(255, Math.max(0.0, red*brightness)));
        int g = (int)Math.round(Math.min(255, Math.max(0.0, green*brightness)));
        int b = (int)Math.round(Math.min(255, Math.max(0.0, blue*brightness)));
        return new RGB(r, g, b);
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
