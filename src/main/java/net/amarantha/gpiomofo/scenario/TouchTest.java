package net.amarantha.gpiomofo.scenario;

public class TouchTest extends Scenario {

    @Override
    public void setup() {

//        TouchTriggerSet set = triggers.touchSet(0, 1);
        triggers.touch("TopLeft", 0, true);
        triggers.touch("TopRight", 1, true);
        triggers.touch("BottomLeft", 2, true);
        triggers.touch("BottomRight", 3, true);

    }
}
