package net.amarantha.gpiomofo.display.lightboard;

import net.amarantha.utils.colour.RGB;

import static net.amarantha.utils.colour.RGB.G;
import static net.amarantha.utils.colour.RGB.R;

/**
 * Created by grimalkin on 01/04/17.
 */
public abstract class NativeWrapper implements LightBoard {

    @Override
    public void update(RGB[][] data) {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                setPoint(y, x, data[x][y].normalised()[R] >= 0.5, data[x][y].normalised()[G] >= 0.5);
            }
        }
    }

    abstract void setPoint(int row, int col, boolean red, boolean green);

}
