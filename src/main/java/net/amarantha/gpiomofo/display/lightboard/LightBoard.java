package net.amarantha.gpiomofo.display.lightboard;

import net.amarantha.utils.colour.RGB;

/**
 * A LightBoard display
 */
public interface LightBoard {

    void init(int width, int height);

    void update(RGB[][] data);

    int width();

    int height();

    /**
     * Indicates how often the LightBoard should receive data via update(...)
     * Return <code>null</code> to update the lightboard as often as possible
     */
    Long interval();

    /**
     * @return <code>true</code> if #init() should be called in its own thread
     */
    boolean needsOwnThread();

}
