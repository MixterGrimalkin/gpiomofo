package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterflies;
import net.amarantha.gpiomofo.service.pixeltape.matrix.ButterPong;
import net.amarantha.gpiomofo.trigger.ContinuousTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import java.util.HashMap;
import java.util.Map;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;

public class GreenpeaceTunnel extends Scenario {

    @Inject private AnimationService animation;
    @Inject private Butterflies butterflies;
    @Inject private ButterPong butterPong;

    @Service private LightSurface surface;

    @Named("PIR1") private Trigger pir1;
    @Named("PIR2") private Trigger pir2;
    @Named("PIR3") private Trigger pir3;
    @Named("PIR4") private Trigger pir4;
    @Named("PIR5") private Trigger pir5;
    @Named("GameOn") private Trigger switchMode;
    @Named("Paddle1") private ContinuousTrigger paddle1;
    @Named("Paddle2") private ContinuousTrigger paddle2;

    @Parameter("SpriteCount") private int spriteCount;
    @Parameter("TailLength") private int tailLength;
    @Parameter("PaddleAxis") private String axis;
    @Parameter("LingerTime") private int lingerTime;

    @Parameter("Colour1") private RGB colour1;
    @Parameter("Colour2") private RGB colour2;
    @Parameter("Colour3") private RGB colour3;
    @Parameter("Colour4") private RGB colour4;
    @Parameter("Colour5") private RGB colour5;
    @Parameter("PaddleColour") private RGB paddleColour;

    private Map<Integer, RGB> colours = new HashMap<>();

    private boolean wide;
    private int step;

    private boolean gameOn = false;

    @Override
    public void setup() {

        colours.put(0, colour1);
        colours.put(1, colour2);
        colours.put(2, colour3);
        colours.put(3, colour4);
        colours.put(4, colour5);

        pir1.onFire(callback(0));
        pir2.onFire(callback(1));
        pir3.onFire(callback(2));
        pir4.onFire(callback(3));
        pir5.onFire(callback(4));

        paddle1.onMeasure(butterPong::setLeftPosition);
        paddle2.onMeasure(butterPong::setRightPosition);

        switchMode.onFire((state)->{
            if ( state ) {
                gameOn = !gameOn;
                animation.play(gameOn ? butterPong : butterflies);
            }
        });

    }

    private Trigger.TriggerCallback callback(final int id) {
        return (state) -> {
            if (state) {
                int x = wide ? (step/2) + (id*step) : surface.width() / 2;
                int y = wide ? surface.height() / 2 : (step/2) + (id*step);
                butterflies.addFocus(id, x, y);
            } else {
                butterflies.removeFocus(id);
            }
        };
    }

    @Override
    public void startup() {

        wide = surface.width() >= surface.height();
        step = wide ? surface.width() / colours.size() : surface.height() / colours.size();

        butterflies.setLingerTime(lingerTime);
        butterflies.init(spriteCount, colours, tailLength);

        butterPong.setPaddleAxis(axis.equalsIgnoreCase("X") ? X : Y);
        butterPong.setPaddleColour(paddleColour);

        animation.start();
        animation.play(butterflies);

    }

}
