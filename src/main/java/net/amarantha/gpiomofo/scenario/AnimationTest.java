package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.font.Font;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterflies;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnimationTest extends Scenario {

    @Service private LightSurface surface;

    @Inject private AnimationService animation;
    @Inject private Butterflies butterflies;

    @Override
    public void startup() {

        Font font = new Font();
        font.loadFont("fonts/SimpleFont.fnt");

        Pattern p = font.renderString("X");

        surface.layer(0).draw(0, 0, p);

//        Map<Integer, RGB> colours = new HashMap<>();
//        colours.put(0, RGB.RED);
//        colours.put(1, RGB.GREEN);
//        colours.put(2, RGB.BLUE);
//        colours.put(3, RGB.YELLOW);
//        colours.put(4, RGB.CYAN);
//
//        butterflies.init(50, colours, 5);
//        animation.start();
//        animation.play(butterflies);

    }
}
