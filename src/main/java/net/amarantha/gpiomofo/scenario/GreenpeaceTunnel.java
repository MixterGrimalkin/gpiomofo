package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterflies;
import net.amarantha.gpiomofo.service.pixeltape.matrix.CrashingBlocks;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static net.amarantha.utils.colour.RGB.BLACK;

public class GreenpeaceTunnel extends Scenario {

    @Inject private AnimationService animationService;
    @Inject private Butterflies butterflies;
    @Inject private CrashingBlocks blocks;

    @Named("Switch1") private Trigger triggerSwitch1;
    @Named("Switch2") private Trigger triggerSwitch2;

    @Service private LightSurface surface;

//    @Named("PIR1") private Trigger pir1;
//    @Named("PIR2") private Trigger pir2;
//    @Named("PIR3") private Trigger pir3;
//    @Named("PIR4") private Trigger pir4;
//    @Named("PIR5") private Trigger pir5;
//    @Named("PIRALL") private Trigger pirAll;

    @Parameter("SpriteCount") private int spriteCount;
    @Parameter("TailLength") private int tailLength;
    @Parameter("LingerTime") private int lingerTime;
    @Parameter("PayoffTime") private int payoffTime;

    @Parameter("ButterflyColours") private String colourStr;
    @Parameter("OffsetMask") private boolean offsetMask;

    @Parameter("PinResistance") private String resistanceStr;
    @Parameter("PinTriggerState") private boolean triggerState;
    @Parameter("Foci") private String fociStr;

    @Parameter("TargetJitter") private String targetJitterStr;

    @Parameter("WinCount") private int winCount;

    private Map<Integer, RGB> colours = new HashMap<>();

    @Inject private WebService http;

    private Long startedPayoff = null;

    @Override
    public void setup() {

        int i = 0;
        for ( String s : colourStr.split(" ") ) {
            colours.put(i++, RGB.parse(s));
        }

        PinPullResistance resistance = PinPullResistance.valueOf(resistanceStr);

        for ( String s : fociStr.split(" ") ) {
            String[] parts = s.split(":");
            String[] coords = parts[1].split(",");
            int pin = parseInt(parts[0].trim());
            int x = parseInt(coords[0].trim());
            int y = parseInt(coords[1].trim());
            triggers
                .gpio("Sensor-"+pin, pin, resistance, triggerState)
                    .onFire((state)->{
                        if ( state ) {
                            butterflies.addFocus(pin, x, y);
                        } else {
                            butterflies.removeFocus(pin);
                        }
                    });
        }

        triggerSwitch1.onFire((state)->{
            butterflies.linearMode(state);
        });

        triggerSwitch2.onFire((state)->{
            butterflies.rest(true);
        });

        String[] jitterCoords = targetJitterStr.split(",");
        butterflies.setTargetJitter(parseInt(jitterCoords[0]), parseInt(jitterCoords[1]));

        butterflies.setLingerTime(lingerTime);
        butterflies.setWinCount(winCount);


    }

    private void startButterflies() {
        if ( startedPayoff!=null && System.currentTimeMillis()-startedPayoff > payoffTime*1000 ) {
            animationService.stop("CrashingBlocks");
            surface.clear();
            if (offsetMask) offsetMask(BLACK);
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

    @Override
    public void startup() {

        butterflies.init(spriteCount, colours, tailLength);

        animationService.add("Butterflies", butterflies);
        animationService.add("CrashingBlocks", blocks);
        animationService.start();

        animationService.play("Butterflies");

        if ( offsetMask ) {
            offsetMask(BLACK);
        }

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
