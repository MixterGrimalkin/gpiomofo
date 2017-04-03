package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.service.pixeltape.pattern.SolidColourWithWhite;
import net.amarantha.utils.colour.RGBW;

public class Test extends Scenario {

    private Target stopPixel;
    private Target pixel;

    @Override
    protected void setupTargets() {
        pixel = targets
                .pixelTape(SolidColourWithWhite.class)
                .setColour(new RGBW(255,0,255,0))
//                .pixelTape(SolidColour.class)
//                .setColour(new RGB(255,0,0))
                .init(0, 24);

        stopPixel = targets.stopPixelTape();
    }

    @Override
    protected void setup() {
        links.link(triggers.get("Button"), pixel);
        links.link(triggers.get("Pad1"), stopPixel);
    }

    @Override
    protected void startup() {
    }
}

