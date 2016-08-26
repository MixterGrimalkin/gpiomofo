package net.amarantha.gpiomofo.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.RGB;
import net.amarantha.gpiomofo.target.PixelTapeTarget;
import net.amarantha.gpiomofo.utility.TimeGuard;

public class Wipe extends PixelTapeTarget {

    private RGB startColour = new RGB(255,255,255);
    private RGB wipeColour = new RGB(0,0,0);
    private long interval = 1;

    @Inject private TimeGuard guard;

    @Override
    public void start() {
        super.start();
        setAll(startColour);
        startPixel = 0;
    }

    private int startPixel = 0;

    @Override
    protected void update() {

        guard.every(interval, "wipe", ()->{
            if ( startPixel < getPixelCount() ) {
                for (int i = 0; i < startPixel; i++) {
                    setPixel(i, wipeColour);
                }
                startPixel += 3;
            } else {
                setAll(wipeColour);
            }
        });


    }
}
