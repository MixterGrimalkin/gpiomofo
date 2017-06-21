package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterflies;
import net.amarantha.gpiomofo.service.pixeltape.matrix.ButterPong;
import net.amarantha.gpiomofo.service.pixeltape.matrix.CrashingBlocks;
import net.amarantha.gpiomofo.trigger.ContinuousTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.webservice.SystemResource;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import java.util.HashMap;
import java.util.Map;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.colour.RGB.BLACK;

public class GreenpeaceTunnel extends Scenario {

    @Inject private AnimationService animationService;
    @Inject private Butterflies butterflies;
    @Inject private CrashingBlocks blocks;

    @Service private LightSurface surface;

    @Named("PIR1") private Trigger pir1;
    @Named("PIR2") private Trigger pir2;
    @Named("PIR3") private Trigger pir3;
    @Named("PIR4") private Trigger pir4;
    @Named("PIR5") private Trigger pir5;
    @Named("PIRALL") private Trigger pirAll;

    @Parameter("SpriteCount") private int spriteCount;
    @Parameter("TailLength") private int tailLength;
    @Parameter("LingerTime") private int lingerTime;
    @Parameter("PayoffTime") private int payoffTime;

    @Parameter("Colour1") private RGB colour1;
    @Parameter("Colour2") private RGB colour2;
    @Parameter("Colour3") private RGB colour3;
    @Parameter("Colour4") private RGB colour4;
    @Parameter("Colour5") private RGB colour5;

    private Map<Integer, RGB> colours = new HashMap<>();

    private boolean wide;
    private int step;

    @Inject private WebService http;

    private Long startedPayoff = null;

    @Override
    public void setup() {

        colours.put(0, colour1);
        colours.put(1, colour2);
        colours.put(2, colour3);
        colours.put(3, colour4);
        colours.put(4, colour5);

        pir1.onFire(callback(0, 0));
        pir2.onFire(callback(1, 7));
        pir3.onFire(callback(2, 20));
        pir4.onFire(callback(3, 32));
        pir5.onFire(callback(4, 44));
        pirAll.onFire((state)->{
            if ( state ) {
                startBlocks();
            } else {
                startButterflies();
            }
        });


    }

    private void startButterflies() {
        if ( startedPayoff!=null && System.currentTimeMillis()-startedPayoff > payoffTime*1000 ) {
            animationService.stop("CrashingBlocks");
            surface.clear();
            offsetMask(BLACK);
            animationService.play("Butterflies");
            startedPayoff = null;
        }
    }

    private void startBlocks() {
        if ( startedPayoff==null ) {
            animationService.stop("Butterflies");
            surface.clear();
            animationService.play("CrashingBlocks");
            startedPayoff = System.currentTimeMillis();
        }
    }

    private Trigger.TriggerCallback callback(final int id, int position) {
        return (state) -> {
            if (state) {
                int x = wide ? (step/2) + (id*step) : surface.width() / 2;
                int y = position;//wide ? surface.height() / 2 : (step/2) + (id*step);
                butterflies.addFocus(id, x, y);
            } else {
                butterflies.removeFocus(id);
                startButterflies();
            }
        };
    }

    @Override
    public void startup() {

        wide = surface.width() >= surface.height();
        step = wide ? surface.width() / colours.size() : surface.height() / colours.size();

        butterflies.setLingerTime(lingerTime);
        butterflies.init(spriteCount, colours, tailLength);
        animationService.add("Butterflies", butterflies);

        animationService.add("CrashingBlocks", blocks);

        animationService.start();
        animationService.play("Butterflies");

        offsetMask(BLACK);

    }

    private int maskLayer = 8;

    private void clearMask() {
        surface.layer(maskLayer).clear();
    }

    private void offsetMask(RGB colour) {
        Pattern mask = new Pattern(surface.width(), surface.height(), true);
        mask.eachPixel((x,y,rgb)->{
            if ( x%2==0 ) {
                if ( y%2==0 ) {
                    mask.draw(x, y, colour);
                }
            } else {
                if ( y%2==1 ) {
                    mask.draw(x, y, colour);
                }
            }
        });
        surface.layer(maskLayer).draw(0,0,mask);
    }

}
