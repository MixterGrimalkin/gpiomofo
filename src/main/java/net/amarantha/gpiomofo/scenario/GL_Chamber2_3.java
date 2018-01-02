package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.Trigger.TriggerCallback;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.task.TaskService;
import net.amarantha.utils.time.TimeGuard;

public class GL_Chamber2_3 extends Scenario {

    @Inject private NeoPixel neoPixel;
    @Inject private TimeGuard guard;

    @Service private TaskService tasks;

    @Parameter("Colour") private RGB colour;
    @Parameter("Colour2") private RGB colour2;
    @Parameter("Colour3") private RGB colour3;
    @Parameter("Refresh") private int refreshInterval;
    @Parameter("Slow") private int slowBlockMove;
    @Parameter("Medium") private int mediumBlockMove;
    @Parameter("Fast") private int fastBlockMove;
    @Parameter("ScrambleRefresh") private int scrambleRefresh;
    @Parameter("ScrambleSize") private int scrambleSize;
    @Parameter("PulseDelta") private double pulseDelta = 0.2;

    @Named("ScramblePixels") private Trigger scramblePixels;
    @Named("ScramblePixels2") private Trigger scramblePixels2;
    @Named("StopPixels") private Trigger stopPixels;
    @Named("SlowPixels") private Trigger slowPixels;
    @Named("MediumPixels") private Trigger mediumPixels;
    @Named("FastPixels") private Trigger fastPixels;

    private int block = 0;

    private int[] blockStarts = {0,2,5,9,15};
    private int[] blockLengths = {2,3,4,6,5};

    private int blockMove = 1000;

    private RGB baseColour = RGB.BLUE;
    private RGB scrambleColour = RGB.RED;

    @Override
    public void setup() {
        scramblePixels.onFire((state)->{
            if ( state ) {
                scrambleColour = colour;
                runPattern = true;
                scramble = true;
            }
        });
        scramblePixels2.onFire((state)->{
            if ( state ) {
                scrambleColour = colour2;
                runPattern = true;
                scramble = true;
                pulse = false;
            }
        });
        stopPixels.onFire((state)->{
            if ( state ) {
                runPattern = false;
                pulse = false;
            }
        });
        slowPixels.onFire(chaseCallback(slowBlockMove, colour2));
        mediumPixels.onFire(chaseCallback(mediumBlockMove, colour));
        fastPixels.onFire((state)->{
            baseColour = colour3;
            runPattern = true;
            pulse = true;
            scramble = false;
        });
        neoPixel.init(20);
    }

    private TriggerCallback chaseCallback(int move, RGB rgb) {
        return (state)-> {
            if ( state ) {
                baseColour = rgb;
                runPattern = true;
                scramble = false;
                pulse = false;
                blockMove = move;
            }
        };
    }

    private boolean runPattern = false;
    private boolean scramble = false;
    private boolean pulse = false;

    private double pulseBrightness = 0.0;


    @Override
    public void startup() {
        tasks.addRepeatingTask("RefreshTape", refreshInterval, ()->{
            if ( runPattern ) {
                neoPixel.allOff();
                if ( scramble ) {
                    guard.every(scrambleRefresh, "Scramble", () -> {
                        for (int i = 0; i < scrambleSize; i++) {
                            int p = MathUtils.randomBetween(0, 19);
                            neoPixel.setPixel(p, scrambleColour);
                        }
                    });
                } else if ( pulse ) {
                    for ( int i=0; i<20; i++ ) {
                        neoPixel.setPixel(i, baseColour.withBrightness(pulseBrightness));
                    }
                    pulseBrightness += pulseDelta;
                    if ( pulseBrightness > 1.0 ) {
                        pulseBrightness = 1.0;
                        pulseDelta *= -1;
                    } else if ( pulseBrightness < 0.0 ) {
                        pulseBrightness = 0.0;
                        pulseDelta *= -1;
                    }

                } else {
                    int start = blockStarts[block];
                    int length = blockLengths[block];
                    for (int i = start; i < start + length; i++) {
                        neoPixel.setPixel(i, baseColour);
                    }
                    guard.every(blockMove, "Move", () -> {
                        block = (block + 1) % blockStarts.length;
                    });
                }
                neoPixel.render();
            } else {
                neoPixel.allOff();
                neoPixel.render();
            }

        });
    }

    @Override
    public void shutdown() {
    }

}
