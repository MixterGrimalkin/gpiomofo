package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.utils.colour.RGB;

import java.util.Timer;
import java.util.TimerTask;

public class TestCustomLayout extends Scenario {

    @Inject private NeoPixel neoPixel;

    @Override
    public void setup() {

        neoPixel.init(size);

    }

    private int p = 0;
    private int size = 20;

    @Override
    public void startup() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                neoPixel.setPixelColourRGB(p, RGB.BLACK);
                if ( ++p >= size ) p = 0;
                neoPixel.setPixelColourRGB(p, RGB.WHITE);
                neoPixel.render();
            }
        }, 0, 150);

    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
