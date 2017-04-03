package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.ServiceFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.touch.TouchSensor;
import net.amarantha.gpiomofo.service.pixeltape.PixelTape;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.midi.MidiService;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.time.Now;

import static net.amarantha.gpiomofo.core.Constants.WITH_SERVER;
import static net.amarantha.utils.shell.Utility.log;

public class Scenario {

    @Inject private ServiceFactory services;

    public final void start() {

        log(" Starting Up... ", true);

        services.startAll();

//        if ( requiresGpio() ) {
//            gpio.start();
//        }
        if ( requiresTouch() ) {
            touch.start();
        }
        if ( requiresMidi() ) {
            midi.start();
        }
        if ( requiresPixelTape() ) {
            pixel.start();
        }
        startup();
        tasks.start();

        if ( props.isArgumentPresent(WITH_SERVER) ) {
            web.start();
        }

        log(true, now.time().toString() + ": Ready");

    }

    public final void stop() {

        log(true, " Shutting Down... ", true);

        if ( props.isArgumentPresent(WITH_SERVER) ) {
            web.stop();
        }

        tasks.stop();
        shutdown();
        services.stopAll();
//        if ( requiresGpio() ) {
//            gpio.stop();
//        }
        if ( requiresTouch() ) {
            touch.stop();
        }
        if ( requiresMidi() ) {
            midi.stop();
        }
        if ( requiresPixelTape() ) {
            pixel.stop();
        }

        log(true, " So long, and thanks for all the fish! ", true);

    }

    ///////////////
    // Lifecycle //
    ///////////////

    protected void setupTriggers() {}

    protected void setupTargets() {}

    protected void setupLinks() {}

    protected void setup() {}

    protected void startup() {}

    protected void shutdown() {}

    ///////////////
    // Factories //
    ///////////////

    @Inject protected TriggerFactory triggers;
    @Inject protected TargetFactory targets;
    @Inject protected LinkFactory links;

    ///////////////////////
    // Required Services //
    ///////////////////////

    @Inject private Now now;
    @Inject private GpioService gpio;
    @Inject private TouchSensor touch;
    @Inject private MidiService midi;
    @Inject private PixelTape pixel;
    @Inject private WebService web;
    @Inject private TaskService tasks;
    @Inject private PropertiesService props;
    @Inject private NeoPixel neoPixel;

    private boolean requiresGpio() {
        return triggers.isGpioUsed() || targets.isGpioUsed();
    }

    private boolean requiresTouch() {
        return triggers.isTouchUsed();
    }

    private boolean requiresMidi() {
        return targets.isMidiUsed();
    }

    private boolean requiresPixelTape() {
        return targets.isPixelTapeUsed();
    }

    //////////
    // Name //
    //////////

    private String name;

    public final String getName() {
        return name;
    }

    public final Scenario setName(String name) {
        this.name = name;
        return this;
    }

}
