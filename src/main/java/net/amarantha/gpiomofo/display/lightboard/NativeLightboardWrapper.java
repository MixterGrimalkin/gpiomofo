package net.amarantha.gpiomofo.display.lightboard;

import net.amarantha.utils.colour.RGB;

import static net.amarantha.utils.colour.RGB.G;
import static net.amarantha.utils.colour.RGB.R;

public abstract class NativeLightboardWrapper implements LightBoard {

    @Override
    public void update(RGB[][] data) {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                setPoint(y, x, data[x][y].normalised()[R] >= 0.5, data[x][y].normalised()[G] >= 0.5);
            }
        }
    }

    abstract void setPoint(int row, int col, boolean red, boolean green);

    abstract void setPins(int clock, int store, int output,
                          int data1R, int data2R, int data1G, int data2G,
                          int addr0, int addr1, int addr2, int addr3);



}
