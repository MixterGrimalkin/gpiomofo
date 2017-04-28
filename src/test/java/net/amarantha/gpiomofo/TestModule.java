package net.amarantha.gpiomofo;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixelMock;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceMock;
import net.amarantha.gpiomofo.service.gpio.touch.TouchSensor;
import net.amarantha.gpiomofo.service.gpio.touch.TouchSensorMock;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensor;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensorMock;
import net.amarantha.utils.UtilityModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new UtilityModule(false));
        bind(GpioService.class).to(GpioServiceMock.class).in(Scopes.SINGLETON);
        bind(NeoPixel.class).to(NeoPixelMock.class).in(Scopes.SINGLETON);
        bind(TouchSensor.class).to(TouchSensorMock.class).in(Scopes.SINGLETON);
        bind(RangeSensor.class).to(RangeSensorMock.class).in(Scopes.SINGLETON);
    }

}
