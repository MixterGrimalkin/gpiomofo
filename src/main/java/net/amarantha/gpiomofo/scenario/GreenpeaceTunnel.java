package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.font.Font;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterflies;
import net.amarantha.gpiomofo.service.pixeltape.matrix.CrashingBlocks;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;
import static net.amarantha.utils.colour.RGB.BLACK;
import static net.amarantha.utils.colour.RGB.RED;
import static net.amarantha.utils.colour.RGB.WHITE;

public class GreenpeaceTunnel extends Scenario {

    @Inject private AnimationService animationService;
    @Inject private Butterflies butterflies;
    @Inject private CrashingBlocks blocks;
    @Inject private NeoPixel neoPixel;

    @Named("Switch1") private Trigger triggerSwitch1;
    @Named("Switch2") private Trigger triggerSwitch2;

    @Service private LightSurface surface;

    @Parameter("SpriteCount") private int spriteCount;
    @Parameter("TailLength") private int tailLength;
    @Parameter("LingerTime") private int lingerTime;
    @Parameter("PayoffTime") private int payoffDuration;
    @Parameter("PayoffRestTime") private int payoffRestTime;

    @Parameter("ButterflyColours") private String colourStr;
    @Parameter("OffsetMask") private boolean offsetMask;

    @Parameter("PinResistance") private String resistanceStr;
    @Parameter("PinTriggerState") private boolean triggerState;
    @Parameter("Foci") private String fociStr;

    @Parameter("TargetJitter") private String targetJitterStr;

    @Parameter("WinCount") private int winCount;

    private Map<Integer, RGB> colours = new HashMap<>();

    @Inject private WebService http;

    private int activeCount;

    private Long startedPayoff = null;
    private Long finishedLastPayoff = null;

    private void addDmxInterceptors(int startPixel, int pixelCount) {
        for ( int i = 0; i < pixelCount; i++ ) {
            neoPixel.intercept(startPixel + i, (rgb)->{});
        }
    }

    @Override
    public void setup() {

//        addDmxInterceptors(62, 23);
//        addDmxInterceptors(85, 24);
//        addDmxInterceptors(236, 19);
//        addDmxInterceptors(255, 18);
//        addDmxInterceptors(409, 16);
//        addDmxInterceptors(425, 14);

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
                            activeCount++;
                        } else {
                            butterflies.removeFocus(pin);
                            activeCount--;
                        }
                        if (activeCount >= winCount
                                && startedPayoff==null
                                && (finishedLastPayoff==null
                                    || currentTimeMillis() - finishedLastPayoff >= payoffRestTime*1000)

                                ) {
                            startedPayoff = currentTimeMillis();
                            finishedLastPayoff = null;
                            startBlocks();
                        } else if ( startedPayoff!=null && currentTimeMillis() - startedPayoff >= payoffDuration *1000) {
                            startedPayoff = null;
                            finishedLastPayoff = currentTimeMillis();
                            startButterflies();
                        }
                    });
        }

        triggerSwitch1.onFire((state) -> {
            if (state) {
                startBlocks();
            } else {
                startButterflies();
            }

        });

        triggerSwitch2.onFire((state)-> butterflies.rest(true));

        String[] jitterCoords = targetJitterStr.split(",");
        butterflies.setTargetJitter(parseInt(jitterCoords[0]), parseInt(jitterCoords[1]));

        butterflies.setLingerTime(lingerTime);
        butterflies.setWinCount(winCount);
    }

    private void startButterflies() {
        animationService.stop("CrashingBlocks");
        surface.clear();
        butterflies.reset();
        if (offsetMask) offsetMask(BLACK);
        animationService.play("Butterflies");
    }

    private void startBlocks() {
        animationService.stop("Butterflies");
        blocks.reset();
        surface.clear();
        animationService.play("CrashingBlocks");
    }

    private Font maskFont = Font.fromFile("ButterflyMask.fnt");

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

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int maskLayer = 8;

    private void clearMask() {
        surface.layer(maskLayer).clear();
    }

    private void offsetMask(RGB colour) {
        surface.layer(maskLayer).draw(0,0,maskFont.renderString("M").getMask(BLACK));
    }

}
