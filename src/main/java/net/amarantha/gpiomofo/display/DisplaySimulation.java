package net.amarantha.gpiomofo.display;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixel;
import net.amarantha.gpiomofo.display.pixeltape.NeoPixelGUI;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceGUI;
import net.amarantha.gpiomofo.service.gpio.touch.TouchSensor;
import net.amarantha.gpiomofo.service.gpio.touch.TouchSensorMock;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensor;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeSensorGUI;

public class DisplaySimulation extends AbstractModule {

    private Stage stage;

    public DisplaySimulation(Stage stage) {
        this.stage = stage;
    }

    @Override
    protected void configure() {
        bind(GpioService.class).to(GpioServiceGUI.class).in(Scopes.SINGLETON);
        bind(NeoPixel.class).to(NeoPixelGUI.class).in(Scopes.SINGLETON);
        bind(TouchSensor.class).to(TouchSensorMock.class).in(Scopes.SINGLETON);
        bind(RangeSensor.class).to(RangeSensorGUI.class).in(Scopes.SINGLETON);
        bind(Stage.class).toInstance(stage);
    }

}
