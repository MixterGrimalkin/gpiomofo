package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;

public class ZapperScenario extends Scenario {

    @Override
    public void setupTriggers() {

        Trigger one = triggers.gpio("One", 0, PULL_DOWN, true);
        Trigger two = triggers.gpio("Two", 3, PULL_DOWN, false);
        Trigger three = triggers.gpio("Three", 4, PULL_DOWN, false);

        triggers.composite("One-Two",       one,    two);
        triggers.composite("One-Three",     one,    three);
        triggers.composite("Two-Three",     two,    three);

        triggers.composite("All",           one,    two,    three);

    }

    @Override
    public void setupTargets() {

        HttpCommand lightboard = new HttpCommand("POST", "192.168.1.60", 8001, "lightboard", "", "");

        targets.gpio("setup1", 1, true);
        targets.gpio("setup2", 2, true);
        targets.gpio("setup3", 4, true);

        targets.chain("Background")
//            .add(targets.gpio(1, false))
//            .add(targets.gpio(2, false))
//            .add(targets.gpio(4, false))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/clear")))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/add").withPayload("-;;-;;-")))
            .add(targets.http(lightboard.withPath("scene/splash/load")))
        .build().oneShot(true);

//        targets.chain("Light-Cycle")
//            .add(100, targets.gpio(1, true))
//            .add(100, targets.gpio(2, true))
//            .add(100, targets.gpio(4, true))
//            .add(100, targets.gpio(1, false))
//            .add(100, targets.gpio(2, false))
//            .add(100, targets.gpio(4, false))
//        .build().repeat(true, targets.get("Background")).oneShot(true);

        targets.chain("Finale-1")
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/clear")))
            .add(targets.http(lightboard.withPath("scene/zapper-finale/load")))
//            .add(targets.get("Light-Cycle"))
        .build().oneShot(true);

        targets.chain("Reset")
            .add(targets.get("Background"))
//            .add(targets.invert(targets.get("Light-Cycle")).oneShot(true))
        .build();

        targets.chain("Light-One")
//            .add(targets.invert(targets.get("Light-Cycle")).oneShot(true))
//            .add(targets.gpio(1, true))
//            .add(targets.gpio(2, false))
//            .add(targets.gpio(4, false))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/clear")))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/add").withPayload("A;;-;;-")))
            .add(targets.http(lightboard.withPath("scene/zapper/load")))
        .build().oneShot(true);

        targets.chain("Light-Two")
//            .add(targets.invert(targets.get("Light-Cycle")).oneShot(true))
//            .add(targets.gpio(1, false))
//            .add(targets.gpio(2, true))
//            .add(targets.gpio(4, false))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/clear")))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/add").withPayload("-;;B;;-")))
            .add(targets.http(lightboard.withPath("scene/zapper/load")))
        .build().oneShot(true);

        targets.chain("Light-Three")
//            .add(targets.invert(targets.get("Light-Cycle")).oneShot(true))
//            .add(targets.gpio(1, false))
//            .add(targets.gpio(2, false))
//            .add(targets.gpio(4, true))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/clear")))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/add").withPayload("-;;-;;C")))
            .add(targets.http(lightboard.withPath("scene/zapper/load")))
        .build().oneShot(true);

        targets.chain("Light-One-Two")
//            .add(targets.invert(targets.get("Light-Cycle")).oneShot(true))
//            .add(targets.gpio(1, true))
//            .add(targets.gpio(2, true))
//            .add(targets.gpio(4, false))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/clear")))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/add").withPayload("A;;B;;-")))
            .add(targets.http(lightboard.withPath("scene/zapper/load")))
        .build().oneShot(true);

        targets.chain("Light-One-Three")
//            .add(targets.invert(targets.get("Light-Cycle")).oneShot(true))
//            .add(targets.gpio(1, true))
//            .add(targets.gpio(2, false))
//            .add(targets.gpio(4, true))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/clear")))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/add").withPayload("A;;-;;C")))
            .add(targets.http(lightboard.withPath("scene/zapper/load")))
        .build().oneShot(true);

        targets.chain("Light-Two-Three")
//            .add(targets.invert(targets.get("Light-Cycle")).oneShot(true))
//            .add(targets.gpio(1, false))
//            .add(targets.gpio(2, true))
//            .add(targets.gpio(4, true))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/clear")))
            .add(targets.http(lightboard.withPath("scene/zapper/group/zaps/add").withPayload("-;;B;;C")))
            .add(targets.http(lightboard.withPath("scene/zapper/load")))
        .build().oneShot(true);

    }

    @Override
    public void setupLinks() {
        links
            .link("None",   "Reset")
            .link("Just-One", "Light-One")
            .link("Just-Two", "Light-Two")
            .link("Just-Three", "Light-Three")
            .link("One-Two", "Light-One-Two")
            .link("One-Three", "Light-One-Three")
            .link("Two-Three", "Light-Two-Three")
            .link("All", "Finale-1")
        ;
    }
}
