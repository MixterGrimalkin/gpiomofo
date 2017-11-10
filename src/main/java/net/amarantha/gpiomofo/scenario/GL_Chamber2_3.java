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
    @Parameter("Refresh") private int refreshInterval;
    @Parameter("Slow") private int slowBlockMove;
    @Parameter("Medium") private int mediumBlockMove;
    @Parameter("Fast") private int fastBlockMove;
    @Parameter("ScrambleRefresh") private int scrambleRefresh;
    @Parameter("ScrambleSize") private int scrambleSize;

    @Named("ScramblePixels") private Trigger scramblePixels;
    @Named("StopPixels") private Trigger stopPixels;
    @Named("SlowPixels") private Trigger slowPixels;
    @Named("MediumPixels") private Trigger mediumPixels;
    @Named("FastPixels") private Trigger fastPixels;

    private int block = 0;

    private int[] blockStarts = {0,1,3,6,10};
    private int[] blockLengths = {1,2,3,4,5};

    private int blockMove = 1000;

    @Override
    public void setup() {
        scramblePixels.onFire((state)->{
            if ( state ) {
                runPattern = true;
                scramble = true;
            }
        });
        stopPixels.onFire((state)->{
            if ( state ) {
                runPattern = false;
            }
        });
        slowPixels.onFire(chaseCallback(slowBlockMove));
        mediumPixels.onFire(chaseCallback(mediumBlockMove));
        fastPixels.onFire(chaseCallback(fastBlockMove));
        neoPixel.init(15);
    }

    private TriggerCallback chaseCallback(int move) {
        return (state)-> {
            if ( state ) {
                runPattern = true;
                scramble = false;
                blockMove = move;
            }
        };
    }

    private boolean runPattern = false;
    private boolean scramble = false;

    @Override
    public void startup() {
        tasks.addRepeatingTask("RefreshTape", refreshInterval, ()->{
            if ( runPattern ) {
                neoPixel.allOff();
                if ( scramble ) {
                    guard.every(scrambleRefresh, "Scramble", ()->{
                        for (int i = 0; i< scrambleSize; i++ ) {
                            int p = MathUtils.randomBetween(0, 14);
                            neoPixel.setPixel(p, colour);
                        }
                    });
                } else {
                    int start = blockStarts[block];
                    int length = blockLengths[block];
                    for (int i = start; i < start + length; i++) {
                        neoPixel.setPixel(i, colour);
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
