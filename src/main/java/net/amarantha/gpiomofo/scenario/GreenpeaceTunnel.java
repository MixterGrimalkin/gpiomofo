package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.Named;
import net.amarantha.gpiomofo.factory.Parameter;
import net.amarantha.gpiomofo.pixeltape.PixelTapeMatrix;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.colour.RGB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFrom;

public class GreenpeaceTunnel extends Scenario {

    @Inject
    private PixelTapeMatrix matrix;

    @Named("PIR1")
    private Trigger pir1;
    @Named("PIR2")
    private Trigger pir2;
    @Named("PIR3")
    private Trigger pir3;
    @Named("PIR4")
    private Trigger pir4;
    @Named("PIR5")
    private Trigger pir5;

    @Parameter("Width")
    private int width;
    @Parameter("Height")
    private int height;
    @Parameter("SpriteCount")
    private int spriteCount;

    @Parameter("Colour1")
    private RGB colour1;
    @Parameter("Colour2")
    private RGB colour2;
    @Parameter("Colour3")
    private RGB colour3;
    @Parameter("Colour4")
    private RGB colour4;
    @Parameter("Colour5")
    private RGB colour5;

    @Parameter("TailLength")
    private int tailLength;

    @Override
    public void setup() {

        Map<Integer, RGB> colours = new HashMap<>();
        colours.put(0, colour1);
        colours.put(1, colour2);
        colours.put(2, colour3);
        colours.put(3, colour4);
        colours.put(4, colour5);

        matrix.setTailLength(tailLength);
        matrix.setNumberOfColours(colours.size());
        matrix.init(width, height, false);

        for (int i = 0; i < colours.size(); i++) {
            for (int j = 0; j < spriteCount / colours.size(); j++) {
                matrix.addSprite(i, colours.get(i));
            }
        }

        pir1.onFire(callback(0, 1));
        pir2.onFire(callback(1, 12));
        pir3.onFire(callback(2, 22));
        pir4.onFire(callback(3, 32));
        pir5.onFire(callback(4, 43));

    }

    private Trigger.TriggerCallback callback(final int id, final int y) {
        return (state) -> {
            if (state) {
                matrix.addFocus(id, width / 2, y);
            } else {
                matrix.removeFocus(id);
            }
        };
    }

}
