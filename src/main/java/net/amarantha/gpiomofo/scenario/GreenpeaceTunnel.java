package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.core.annotation.Named;
import net.amarantha.gpiomofo.core.annotation.Parameter;
import net.amarantha.gpiomofo.core.scenario.Scenario;
import net.amarantha.gpiomofo.core.trigger.Trigger;
import net.amarantha.gpiomofo.display.LightSurface;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterflies;
import net.amarantha.utils.colour.RGB;

import java.util.HashMap;
import java.util.Map;

public class GreenpeaceTunnel extends Scenario {

    @Inject private LightSurface surface;
    @Inject private AnimationService animation;
    @Inject private Butterflies butterflies;

    @Named("PIR1") private Trigger pir1;
    @Named("PIR2") private Trigger pir2;
    @Named("PIR3") private Trigger pir3;
    @Named("PIR4") private Trigger pir4;
    @Named("PIR5") private Trigger pir5;

    @Parameter("Width") private int width;
    @Parameter("Height") private int height;
    @Parameter("SpriteCount") private int spriteCount;
    @Parameter("TailLength") private int tailLength;

    @Parameter("Colour1") private RGB colour1;
    @Parameter("Colour2") private RGB colour2;
    @Parameter("Colour3") private RGB colour3;
    @Parameter("Colour4") private RGB colour4;
    @Parameter("Colour5") private RGB colour5;

    private Map<Integer, RGB> colours = new HashMap<>();

    @Override
    public void setup() {
        pir1.onFire(callback(0, 5));
        pir2.onFire(callback(1, 30));
        pir3.onFire(callback(2, 60));
        pir4.onFire(callback(3, 90));
        pir5.onFire(callback(4, 115));
        colours.put(0, colour1);
        colours.put(1, colour2);
        colours.put(2, colour3);
        colours.put(3, colour4);
        colours.put(4, colour5);
    }

    @Override
    protected void startup() {
        surface.init();
        butterflies.init(spriteCount, colours, tailLength);
        animation.start();
        animation.play(butterflies);
    }

    private Trigger.TriggerCallback callback(final int id, final int pos) {
        return (state) -> {
            if (state) {
                butterflies.addFocus(id, pos, height / 2);
            } else {
                butterflies.removeFocus(id);
            }
        };
    }

}
