package net.amarantha.gpiomofo.display.entity;

import net.amarantha.gpiomofo.core.Constants;
import net.amarantha.utils.colour.RGB;

import java.util.Deque;
import java.util.LinkedList;

import static java.lang.Math.max;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.colour.RGB.BLACK;
import static net.amarantha.utils.colour.RGB.WHITE;
import static net.amarantha.utils.math.MathUtils.min;

/**
 * A pattern of dots
 */
public class Pattern {

    private final int width;
    private final int height;
    private final RGB[][] pixels;
    private final boolean transparent;

    public Pattern(RGB[][] data) {
        this(data.length, data[0].length, false);
        for ( int x=0; x<width; x++ ) {
            for ( int y=0; y<height; y++ ) {
                pixels[x][y] = data[x][y];
            }
        }
    }

    public Pattern(int width, int height) {
        this(width, height, false);
    }

    public Pattern(int width, int height, boolean transparent) {
        pixels = new RGB[this.width = max(1, width)][this.height = max(1, height)];
        if (!(this.transparent = transparent)) {
            fill(BLACK);
        }
    }

    public Pattern(int cols, String data) {
        int rows = data.length() / cols;
        pixels = new RGB[this.width = max(1, cols)][this.height = max(1, rows)];
        transparent = true;
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (data.charAt(i) == '#') {
                    pixels[x][y] = RGB.WHITE;
                } else {
                    pixels[x][y] = null;//RGB.BLACK;
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
        eachPixel((x, y, rgb) -> result[x][y] = rgb);
        return result;
    }

    public RGB rgb(int x, int y) {
        if (inBounds(x, y)) {
            return pixels[x][y];
        }
        return null;
    }

    ////////////////
    // Processing //
    ////////////////

    public void eachPixel(PixelCallback callback) {
        eachPixel(0, 0, width, height, callback);
    }

    public void eachPixel(int xStart, int yStart, int regionWidth, int regionHeight, PixelCallback callback) {
        for (int x = max(0, xStart); x < min(xStart + regionWidth, width); x++) {
            for (int y = max(0, yStart); y < min(yStart + regionHeight, height); y++) {
                callback.process(x, y, pixels[x][y]);
            }
        }
    }

    public void eachPixel(Region region, PixelCallback callback) {
        eachPixel(region.left, region.top, region.width, region.height, callback);
    }

    //////////
    // Draw //
    //////////

    private RGB penColour = WHITE;

    public Pattern pen(RGB penColour) {
        this.penColour = penColour;
        return this;
    }

    public Pattern draw(int[] point) {
        return draw(point, penColour);
    }

    public Pattern draw(int[] point, RGB colour) {
        return draw(point[X], point[Y], colour);
    }

    public Pattern draw(int x, int y) {
        return draw(x, y, penColour);
    }

    public Pattern draw(int x, int y, RGB colour) {
        if (inBounds(x, y)) {
            pixels[x][y] = colour == null && !transparent ? BLACK : colour;
        }
        return this;
    }

    public Pattern draw(int x, int y, Pattern other) {
        other.eachPixel((innerX, innerY, rgb) -> draw(x + innerX, y + innerY, rgb));
        return this;
    }

    public Pattern fill(RGB colour) {
        eachPixel((x, y, rgb) -> draw(x, y, colour));
        return this;
    }

    public Pattern clear() {
        fill(null);
        return this;
    }

    public Pattern fillRegion(int x, int y, int width, int height, RGB colour) {
        eachPixel(x, y, width, height, (x1, y1, rgb) -> draw(x1, y1, colour));
        return this;
    }

    public Pattern clearRegion(int x, int y, int width, int height) {
        return fillRegion(x, y, width, height, null);
    }

    public Pattern clearRegion(Region region) {
        return clearRegion(region.left, region.top, region.width, region.height);
    }

    public Pattern outlineRegion(Region region, RGB colour) {
        eachPixel(region, (x,y,rgb)->{
            if ( x==region.left || x==region.right || y==region.top || y==region.bottom ) {
                draw(x, y, colour);
            }
        });
        return this;
    }

    public void drawLine(int x1, int y1, int x2, int y2, RGB colour) {
        // Adapted from - https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java

        // delta of exact value and rounded value of the dependant variable
        int d = 0;

        int dy = Math.abs(y2 - y1);
        int dx = Math.abs(x2 - x1);

        int dy2 = (dy << 1); // slope scaling factors to avoid floating
        int dx2 = (dx << 1); // point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        if (dy <= dx) {
            for (;;) {
                draw(x1, y1, colour);
                if (x1 == x2)
                    break;
                x1 += ix;
                d += dy2;
                if (d > dx) {
                    y1 += iy;
                    d -= dx2;
                }
            }
        } else {
            for (;;) {
                draw(x1, y1, colour);
                if (y1 == y2)
                    break;
                y1 += iy;
                d += dx2;
                if (d > dy) {
                    x1 += ix;
                    d -= dy2;
                }
            }
        }
    }

    public void fillCircle(int x, int y, int radius, RGB colour) {
        fillCircle(x, y, radius, colour, colour);
    }

    public void fillCircle(int x, int y, int radius, RGB outlineColour, RGB fillColour) {
        drawCircle(x, y, radius, outlineColour);
        floodFill(x, y, fillColour);
    }

    public void drawCircle(final int centerX, final int centerY, final int radius, RGB colour) {
        // https://rosettacode.org/wiki/Bitmap/Midpoint_circle_algorithm#Java
        int d = (5 - radius * 4)/4;
        int x = 0;
        int y = radius;

        do {
            draw(centerX + x, centerY + y, colour);
            draw(centerX + x, centerY - y, colour);
            draw(centerX - x, centerY + y, colour);
            draw(centerX - x, centerY - y, colour);
            draw(centerX + y, centerY + x, colour);
            draw(centerX + y, centerY - x, colour);
            draw(centerX - y, centerY + x, colour);
            draw(centerX - y, centerY - x, colour);
            if (d < 0) {
                d += 2 * x + 1;
            } else {
                d += 2 * (x - y) + 1;
                y--;
            }
            x++;
        } while (x <= y);

    }

    public void drawRect(int x1, int y1, int width, int height, RGB colour) {
        fillRegion(x1, y1, width, 1, colour);
        fillRegion(x1, y1, 1, height, colour);
        fillRegion(x1+width-1, y1, 1, height, colour);
        fillRegion(x1, y1+height-1, width, 1, colour);
    }

    public void floodFill(int centreX, int centreY, RGB colour) {
        // from https://rosettacode.org/wiki/Bitmap/Flood_fill#Java
        RGB target = rgb(centreX, centreY);
        int[] node = new int[]{centreX,centreY};
        if (target != colour) {
            Deque<int[]> queue = new LinkedList<>();
            do {
                int x = node[X];
                int y = node[Y];
                while (x > 0 && rgb(x - 1, y) == target) {
                    x--;
                }
                boolean spanUp = false;
                boolean spanDown = false;
                while (x < width && rgb(x, y) == target) {
                    draw(x, y, colour);
                    if (!spanUp && y > 0 && rgb(x, y - 1) == target) {
                        queue.add(new int[]{x, y - 1});
                        spanUp = true;
                    } else if (spanUp && y > 0 && rgb(x, y - 1) != target) {
                        spanUp = false;
                    }
                    if (!spanDown && y < height - 1 && rgb(x, y + 1) == target) {
                        queue.add(new int[]{x, y + 1});
                        spanDown = true;
                    } else if (spanDown && y < height - 1 && rgb(x, y + 1) != target) {
                        spanDown = false;
                    }
                    x++;
                }
            } while ((node = queue.pollFirst()) != null);
        }
    }


    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
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
