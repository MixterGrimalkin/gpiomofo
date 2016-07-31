package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.gpio.GpioProvider;
import net.amarantha.gpiomofo.midi.MidiCommand;

@Singleton
public class TargetFactory {

    @Inject private GpioProvider gpioProvider;
    @Inject private Injector injector;

    public GpioTarget gpio(TriggerConfig config, int outputPin, Boolean outputState) {

        if ( !gpioProvider.isDigitalOutput(outputPin) ) {
            gpioProvider.setupDigitalOutput(outputPin, outputState != null && !outputState);
        }
        GpioTarget target =
            injector.getInstance(GpioTarget.class)
                .outputPin(outputPin)
                .outputState(outputState);
        target.triggerState(config.getTriggerState());

        if ( !gpioProvider.isDigitalInput(config.getTriggerPin()) ) {
            gpioProvider.setupDigitalInput(config.getTriggerPin(), config.getResistance());
        }
        gpioProvider.onInputChange(config.getTriggerPin(), target::processTrigger);

        return target;
    }

    public MidiTarget midi(TriggerConfig config, MidiCommand onCommand, MidiCommand offCommand) {

        MidiTarget target =
            injector.getInstance(MidiTarget.class)
                .onCommand(onCommand)
                .offCommand(offCommand);
        target.triggerState(config.getTriggerState());

        if ( !gpioProvider.isDigitalInput(config.getTriggerPin()) ) {
            gpioProvider.setupDigitalInput(config.getTriggerPin(), config.getResistance());
        }
        gpioProvider.onInputChange(config.getTriggerPin(), target::processTrigger);

        return target;
    }

}
