package net.amarantha.gpiomofo.display.lightboard;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixelGUI;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.utils.colour.RGB;

import static net.amarantha.gpiomofo.service.shell.Utility.log;

public class NeoPixelBoard implements LightBoard {

    @Inject private NeoPixel neoPixel;
    @Inject private TaskService tasks;

    private int width;
    private int height;

    @Override
    public void init(int width, int height) {
        log("Starting Pixel Tape Board " + width + " x " + height);
        this.width = width;
        this.height = height;
        if ( neoPixel instanceof NeoPixelGUI ) {
            ((NeoPixelGUI)neoPixel).setDefaultWidth(width);
        }
        neoPixel.init(width * height);
    }

    @Override
    public void update(RGB[][] data) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                neoPixel.setPixelColourRGB(getPixelNumber(x, y), data[x][y]);
            }
        }
        neoPixel.render();
    }

    private boolean rowsFirst = true;
    private boolean loopback = false;

    private int getPixelNumber(int x, int y) {
        if (rowsFirst) {
            if (loopback && y % 2 == 1) {
                return (y * width) + (width - 1 - x);
            } else {
                return (y * width) + x;
            }
        } else {
            if (loopback && x % 2 == 1) {
                return (x * height) + (height - 1 - x);
            } else {
                return (x * height) + y;
            }
        }
    }

    @Override
    public Long interval() {
        return 10L;
    }

    @Override
    public boolean needsOwnThread() {
        return false;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

}
