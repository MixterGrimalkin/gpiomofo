package net.amarantha.gpiomofo.display.lightboard;

import com.google.inject.Inject;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.Gui;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.Property;
import net.amarantha.utils.properties.PropertyGroup;

import static net.amarantha.utils.colour.RGB.*;

/**
 * UI Simulation of a colour LightBoard
 */
@PropertyGroup("LightBoardGUI")
public class LightBoardGUI implements LightBoard {

    @Property("OnlyRedGreen") private boolean onlyRedGreen;
    @Property("OnlyOnOff") private boolean onlyOnOff;
    @Property("LedSize") private int ledRadius = 2;
    @Property("Spacing") private int spacer = 0;
    @Property("RefreshInterval") private Long refreshInterval = 60L;

    private int width;
    private int height;
    private Circle[][] leds;
    private Stage stage;

    @Inject private PropertiesService props;
    @Inject private TaskService tasks;
    @Inject private Gui gui;

    private int d;

    @Override
    public void init(int width, int height) {

        System.out.println("Starting UI Simulation LightBoard...");

        props.injectPropertiesOrExit(this);

        this.width = width;
        this.height = height;

        leds = new Circle[width][height];
        d = ledRadius * 2;

        // Build UI components
        final Pane pane = new Pane();
        pane.setStyle("-fx-background-color: black;");
        Group board = new Group();
        pane.getChildren().add(board);

        // Create LED Board
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Circle led = new Circle(ledRadius - spacer, Color.color(0.0, 0.0, 0.0));
                led.setCenterX(d + x * d);
                led.setCenterY(d + y * d);
                board.getChildren().add(led);
                leds[x][y] = led;
            }
        }

        // RIGHT-CLICK => reset UI window (because it messes up all the time - bug in JaxaFX maybe???)
        pane.setOnMouseClicked((e) -> {
            if ( e.getButton()==MouseButton.SECONDARY ) {
                stage.hide();
                stage = gui.addStage("LightBoard");
                init(width, height);
            } else {
                if (e.isControlDown() && e.isAltDown()) {
                    stage.hide();
                    stage = gui.addStage("LightBoard");
                    init(width, height);
                }
            }
        });

        // Start UI
        stage = gui.addStage("LightBoard");
        stage.setScene(new Scene(pane, getWidthPixels(), getHeightPixels()));
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.show();

    }

    @Override
    public void update(RGB[][] data) {
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[0].length; y++) {
                Double[] rgb = data[x][y].normalised();
                if (leds[x][y] != null) {
                    double r = onlyOnOff ? (rgb[R] >= 0.5 ? 1.0 : 0.0) : rgb[R];
                    double g = onlyOnOff ? (rgb[G] >= 0.5 ? 1.0 : 0.0) : rgb[G];
                    double b = onlyRedGreen ? 0 : (onlyOnOff ? (rgb[B] >= 0.5 ? 1.0 : 0.0) : rgb[B]);
                    leds[x][y].setFill(Color.color(r, g, b));
                }
            }
        }
    }

    @Override
    public Long interval() {
        return refreshInterval;
    }

    @Override
    public boolean needsOwnThread() {
        return false;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    public int getWidthPixels() {
        return (width + 1) * d;
    }

    public int getHeightPixels() {
        return (height + 1) * d;
    }

}