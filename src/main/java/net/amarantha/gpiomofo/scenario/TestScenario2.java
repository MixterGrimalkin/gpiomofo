package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.pixeltape.PixelTapeController;
import net.amarantha.gpiomofo.pixeltape.pattern.Wipe;
import net.amarantha.gpiomofo.target.Target;

import static net.amarantha.gpiomofo.scenario.GingerlineBriefingRoom.*;

public class TestScenario2 extends Scenario {

    @Inject private PixelTapeController pixelTape;

    @Override
    public void setupTriggers() {

    }

    @Override
    public void setupTargets() {

        Target pipe1 =
            targets.pixelTape(Wipe.class)
                .setRefreshInterval(50)
                .setReverse(true)
                .init(PIPE_1_START,PIPE_1_SIZE);

        pixelTape.init(WHOLE_TAPE).start();

        pipe1.activate();


    }

    @Override
    public void setupLinks() {


    }

}
