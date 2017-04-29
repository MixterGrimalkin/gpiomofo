package net.amarantha.gpiomofo.service.gpio.ultrasonic;

import com.google.inject.Inject;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.service.gui.Gui;

import java.util.HashMap;
import java.util.Map;

import static net.amarantha.utils.math.MathUtils.round;

public class RangeSensorGUI extends RangeSensor {

    private Gui gui;

    private Stage window;
    private HBox sliderContainer = new HBox();
    private Map<String, Slider> sliders = new HashMap<>();

    @Inject
    public RangeSensorGUI(Gui gui) {
        super("Range Sensor GUI");
        this.gui = gui;
    }

    @Override
    protected void onStart() {
        if ( gui != null ) {
            window = gui.addStage("Range Sensor");
            Group root = new Group();
            Scene scene = new Scene(root);
            window.setScene(scene);
            scene.setFill(Color.BLACK);
            root.getChildren().add(sliderContainer);
            window.setHeight(200);
        }
        sensors.forEach((sensor)->{
            sliders.put(sensor.trigger+"-"+sensor.echo, new Slider(new ScrollBar()));
        });
        super.onStart();
        window.show();
    }

    @Override
    protected void init(int trigger, int echo) {
        if ( gui != null ) {
            Slider slider = sliders.get(trigger+"-"+echo);
            slider.scrollBar.setOrientation(Orientation.VERTICAL);
            slider.scrollBar.setPrefWidth(50);
            slider.scrollBar.setPrefHeight(200);
            sliderContainer.getChildren().add(slider.scrollBar);
        }
    }

    @Override
    protected long measure(int trigger, int echo) {
        Slider slider = sliders.get(trigger+"-"+echo);
        if ( slider!=null ) {
            return round(slider.scrollBar.getValue());
        }
        return 0;
    }

    private static class Slider {
        ScrollBar scrollBar;
        public Slider(ScrollBar scrollBar) {
            this.scrollBar = scrollBar;
        }
    }
}
