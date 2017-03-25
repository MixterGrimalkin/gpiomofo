package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.Named;
import net.amarantha.gpiomofo.factory.Parameter;
import net.amarantha.gpiomofo.pixeltape.PixelTapeMatrix;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFrom;

public class GreenpeaceTunnel extends Scenario {

    @Inject private PixelTapeMatrix matrix;

    @Named("PIR1") private Trigger pir1;
    @Named("PIR2") private Trigger pir2;
    @Named("PIR3") private Trigger pir3;
    @Named("PIR4") private Trigger pir4;
    @Named("PIR5") private Trigger pir5;

    @Parameter("Width") private int width;
    @Parameter("Height") private int height;
    @Parameter("SpriteCount") private int spriteCount;

    @Parameter("Colour1") private RGB colour1;
    @Parameter("Colour2") private RGB colour2;
    @Parameter("Colour3") private RGB colour3;
    @Parameter("Colour4") private RGB colour4;

    @Parameter("TailLength") private int tailLength;

    @Override
    public void setup() {

        matrix.setTailLength(tailLength);
        matrix.init(width, height, false);

        List<RGB> colours = new ArrayList<>();
        colours.add(colour1);
        colours.add(colour2);
        colours.add(colour3);
        colours.add(colour4);

        for ( int i = 0; i < spriteCount; i++ ) {
            matrix.addSprite(randomFrom(colours));
        }

        pir1.onFire(callback(1));
        pir2.onFire(callback(11));
        pir3.onFire(callback(22));
        pir4.onFire(callback(30));
        pir5.onFire(callback(42));

    }

    private Trigger.TriggerCallback callback(int y) {
        return (state) -> {
            if ( state ) {
                matrix.addFocus(y, width/2, y);
            } else {
                matrix.removeFocus(y);
            }
        };
    }

}
