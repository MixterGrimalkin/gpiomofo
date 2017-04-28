package net.amarantha.gpiomofo.display;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixelWS281X;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceRaspPi;
import net.amarantha.gpiomofo.service.gpio.touch.TouchSensor;
import net.amarantha.gpiomofo.service.gpio.touch.TouchSensorMPR121;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensor;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensorHCSR04;

public class DisplayLive extends AbstractModule {

    @Override
    protected void configure() {
        bind(GpioService.class).to(GpioServiceRaspPi.class).in(Scopes.SINGLETON);
        bind(NeoPixel.class).to(NeoPixelWS281X.class).in(Scopes.SINGLETON);
        bind(TouchSensor.class).to(TouchSensorMPR121.class).in(Scopes.SINGLETON);
        bind(RangeSensor.class).to(RangeSensorHCSR04.class).in(Scopes.SINGLETON);
    }

}
