package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.UltrasonicSensor;

public class TestScenario2 extends Scenario {

    @Inject private UltrasonicSensor sensor;

    @Override
    public void setupTriggers() {

    }

    @Override
    public void setupTargets() {

        Target t1 = targets.gpio(2, true);
        Target t2 = targets.gpio(3, true);
        Target t3 = targets.gpio(4, true);

        Target p1 = targets.python("python/strandtest.py clear");
        Target p2 = targets.python("python/strandtest.py chase-blue");
        Target p3 = targets.python("python/strandtest.py chase-red");

        sensor
            .addRange(0.0, 0.3, targets.chain().add(0, t1, p1).build())
            .addRange(0.3, 0.7, targets.chain().add(0, t2, p2).build())
            .addRange(0.7, 1.0, targets.chain().add(0, t3, p3).build())
        ;

        sensor.start();

    }

    @Override
    public void setupLinks() {




    }

}
