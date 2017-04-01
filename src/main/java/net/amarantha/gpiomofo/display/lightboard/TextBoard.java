package net.amarantha.gpiomofo.display.lightboard;

import net.amarantha.utils.colour.RGB;

import javax.inject.Inject;
import java.io.PrintStream;

import static net.amarantha.gpiomofo.service.shell.Utility.log;

/**
 * Simple implementation of a colour LightBoard that dumps lightboard state to the console
 */
public class TextBoard implements LightBoard {

    private int height;
    private int width;
    private final PrintStream out;

    @Inject
    public TextBoard(PrintStream out) {
        this.out = out;
    }

    @Override
    public void init(int width, int height) {
        log("Starting TextBoard...");
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(RGB[][] data) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r< height; r++ ) {
            sb.append((r < 10 ? "0" : "")).append(r).append(":");
            for (int c = 0; c< width; c++ ) {
                sb.append(data[c][r].getLuminance()>=0.5 ? "#" : "-");
            }
            sb.append("\n");
        }
        sb.append("\n");
        log(sb.toString());
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public Long interval() {
        return 250L;
    }

    @Override
    public boolean needsOwnThread() {
        return false;
    }

}
