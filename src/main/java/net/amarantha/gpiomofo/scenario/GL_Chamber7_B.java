package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.core.Constants;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;

import static java.lang.System.currentTimeMillis;

public class GL_Chamber7_B extends Scenario {

    @Inject private NeoPixel neoPixel;
    @Service private TaskService tasks;

    @Named("GreenB") private Trigger greenBriefing;
    @Named("RedB") private Trigger redBriefing;

    @Parameter("PixelCount") private int pixelCount;
    @Parameter("Refresh") private int refreshInterval;
    @Parameter("HeadDelta") private int headDelta;
    @Parameter("TailDelta") private int tailDelta;
    @Parameter("MaxC") private int maxC;
    @Parameter("CDelta") private int cDelta ;
    @Parameter("TopLightSize") private int lighted;
    @Parameter("TopLightColour") private RGB lightedColour;

    @Parameter("FlashCount") private int flashes;
    @Parameter("FlashColour") private RGB flashColour;

    private int head = 1;
    private int tail = 0;
    private int c = 0;

    @Override
    public void setup() {
        Constants.neoPixelGUIWidths = new int[]{150};
        neoPixel.init(pixelCount);

        greenBriefing.onFire((state) -> {
            if ( state ) {
                doneReset = false;
                restColour = RGB.BLACK;
                runPattern = !runPattern;
                if ( runPattern ) {
                    flashing = false;
                    flashCount = flashes;
                }
            }
        });
        redBriefing.onFire((state)->{
            if ( state ) {
                panicMode = !panicMode;
                if ( panicMode ) {
                    doneReset = false;
                    restColour = RGB.BLACK;
                } else {
                    doneReset = false;
                    restColour = RGB.WHITE;
                    runPattern = false;
                }
            }
        });
    }

    private boolean doneFlash;

    private boolean panicMode = false;
    private boolean runPattern = false;
    private RGB restColour = RGB.BLACK;
    private boolean doneReset = false;

    private void flashTape(RGB rgb) {
        for (int i = 0; i < pixelCount; i++) {
            neoPixel.setPixel(i, rgb);
        }
    }

    private double flashOnTime = 50;
    private double flashOffTime = 100;
    private int flashCount = 3;
    private boolean flashing = false;
    private Long lastFlashChange = null;

    @Override
    public void startup() {
        tasks.addRepeatingTask("SlidePixels", refreshInterval, ()->{
            if ( runPattern ) {
                neoPixel.allOff();
                if ( flashCount > 0 ) {
                    if ( lastFlashChange==null ) {
                        flashTape(flashColour);
                        flashing = true;
                        lastFlashChange = currentTimeMillis();
                    } else {
                        if ( flashing && currentTimeMillis() - lastFlashChange >= flashOnTime ) {
                            flashTape(RGB.BLACK);
                            lastFlashChange = currentTimeMillis();
                            flashing = false;
                            flashCount--;
                        }
                        if ( !flashing && currentTimeMillis() - lastFlashChange >= flashOffTime ) {
                            flashTape(flashColour);
                            lastFlashChange = currentTimeMillis();
                            flashing = true;
                        }
                    }


                } else {
                    for (int i = 0; i < lighted; i++) {
                        neoPixel.setPixel(i, lightedColour);
                    }
                    for (int i = tail; i < head; i++) {
                        if (tail + i < pixelCount) {
                            neoPixel.setPixel(tail + i, new RGB(255, c, c / 2));
                        }
                    }
                    head = (head + headDelta);
                    tail = (tail + tailDelta);
                    if (head >= pixelCount) {
                        head = 1;
                        tail = 0;
                        c = 0;
                    }
                }
                c = MathUtils.max(c + cDelta, maxC);
                neoPixel.render();
            } else {
                head = 1;
                tail = 0;
                c = 0;
                neoPixel.allOff();
                for (int i = 0; i < lighted; i++) {
                    neoPixel.setPixel(i, lightedColour);
                }
                if ( !restColour.equals(RGB.BLACK) ) {
                    for ( int i=0; i<pixelCount; i++ ) {
                        neoPixel.setPixel(i, restColour);
                    }
                }
                neoPixel.render();
                doneReset = true;
            }
        });


    }

    @Override
    public void shutdown() {
        neoPixel.allOff();
        neoPixel.render();
    }
}
