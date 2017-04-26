package net.amarantha.gpiomofo.display.entity;

import net.amarantha.utils.colour.RGB;

import static java.lang.Math.max;
import static net.amarantha.utils.colour.RGB.BLACK;
import static net.amarantha.utils.colour.RGB.WHITE;

/**
 * A pattern of dots
 */
public class Pattern {

    private final int width;
    private final int height;
    private final RGB[][] pixels;
    private final boolean transparent;

    public Pattern(int width, int height) {
        this(width, height, false);
    }

    public Pattern(int width, int height, boolean transparent) {
        pixels = new RGB[this.width = max(1, width)][this.height = max(1, height)];
        if ( !(this.transparent = transparent) ) {
            fill(BLACK);
        }
    }

    public Pattern(int cols, String data) {
        int rows = data.length()/cols;
        pixels = new RGB[this.width = max(1, cols)][this.height = max(1, rows)];
        transparent = true;
        int i = 0;
        for ( int x=0; x<width; x++ ) {
            for (int y = 0; y < height; y++) {
                if ( data.charAt(i)=='#' ) {
                    pixels[x][y] = RGB.WHITE;
                } else {
                    pixels[x][y] = RGB.BLACK;
                }
                i++;
            }
        }
    }

    //////////
    // Read //
    //////////

    public RGB[][] rgb() {
        RGB[][] result = new RGB[width][height];
        eachPixel((x,y,rgb)-> result[x][y] = rgb);
        return result;
    }

    public RGB rgb(int x, int y) {
        if ( inBounds(x,y) ) {
            return pixels[x][y];
        }
        return null;
    }

    ////////////////
    // Processing //
    ////////////////

    public void eachPixel(PixelCallback callback) {
        for ( int x=0; x<width; x++ ) {
            for ( int y=0; y<height; y++ ) {
                callback.process(x, y, pixels[x][y]);
            }
        }
    }

    //////////
    // Draw //
    //////////

    private RGB penColour = WHITE;

    public Pattern pen(RGB penColour) {
        this.penColour = penColour;
        return this;
    }

    public Pattern draw(int x, int y) {
        return draw(x, y, penColour);
    }

    public Pattern draw(int x, int y, RGB colour) {
        if ( inBounds(x,y) ) {
            pixels[x][y] = colour==null && !transparent ? BLACK : colour;
        }
        return this;
    }

    public Pattern draw(int x, int y, Pattern other) {
        other.eachPixel((innerX, innerY, rgb) -> draw(x + innerX, y + innerY, rgb));
        return this;
    }

    public Pattern fill(RGB colour) {
        eachPixel((x,y,rgb)->draw(x,y,colour));
        return this;
    }

    public boolean inBounds(int x, int y) {
        return x>=0 && x<width && y>=0 && y<height;
    }

    public Pattern slice(int xStart, int yStart, int subWidth, int subHeight) {
        Pattern result = new Pattern(subWidth, subHeight);
        for (int x = xStart; x < (xStart + subWidth); x++) {
            for (int y = yStart; y < (yStart + subHeight); y++) {
                result.draw(x - xStart, y - yStart, rgb(x, y));
            }
        }
        return result;
    }

    public interface PixelCallback {
        void process(int x, int y, RGB rgb);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
