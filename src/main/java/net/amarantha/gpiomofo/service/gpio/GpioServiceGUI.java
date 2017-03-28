package net.amarantha.gpiomofo.service.gpio;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.Gui;

import java.util.HashMap;
import java.util.Map;

public class GpioServiceGUI extends GpioService {

    protected Map<Integer, Boolean> inputStates = new HashMap<>();
    protected Map<Integer, Boolean> outputStates = new HashMap<>();

    private Gui gui;

    private Map<Integer, Button> inputButtons = new HashMap<>();
    private Map<Integer, Button> outputButtons = new HashMap<>();

    private HBox inputButtonContainer = new HBox();
    private HBox outputButtonContainer = new HBox();

    @Inject
    public GpioServiceGUI(Gui gui) {
        this.gui = gui;
    }

    @Override
    public void start(long period) {

        if ( gui!=null ) {
            Stage inputWindow = gui.addStage("Input GPIO");
            buildButtonContainer(inputWindow, inputButtonContainer);
            inputWindow.show();

            Stage outputWindow = gui.addStage("Output GPIO");
            buildButtonContainer(outputWindow, outputButtonContainer);
            outputWindow.show();
        }

        super.start(period);
    }

    private void buildButtonContainer(Stage window, HBox container) {
        Group root = new Group();
        Scene scene = new Scene(root);
        window.setScene(scene);
        scene.setFill(Color.BLACK);
        root.getChildren().add(container);
        scene.setOnKeyPressed((event)->{
            switch(event.getCode()) {
                case DIGIT1:
                    fireButton(0);
                    break;
                case DIGIT2:
                    fireButton(1);
                    break;
                case DIGIT3:
                    fireButton(2);
                    break;
                case DIGIT4:
                    fireButton(3);
                    break;
                case DIGIT5:
                    fireButton(4);
                    break;
                case DIGIT6:
                    fireButton(5);
                    break;
                case DIGIT7:
                    fireButton(6);
                    break;
                case DIGIT8:
                    fireButton(7);
                    break;
                case DIGIT9:
                    fireButton(8);
                    break;
                case DIGIT0:
                    fireButton(9);
                    break;
            }
        });
    }

    private void fireButton(int b) {
        Button button = inputButtons.get(b);
        if ( button!=null ) {
            button.fire();
        }

    }

    private void redrawGui() {
        if ( gui!=null ) {
            inputButtons.clear();
            inputButtonContainer.getChildren().clear();
            if (inputStates.isEmpty()) {
                inputButtonContainer.getChildren().add(new Button("(none)"));
            } else {
                inputStates.forEach((pin, state) -> {
                    Button button = new Button("Gpio-" + pin);
                    inputButtons.put(pin, button);
                    button.setOnAction(event -> {
                        inputStates.put(pin, !inputStates.get(pin));
                        refreshGui();
                    });
                    inputButtonContainer.getChildren().add(button);
                });
            }
            outputButtons.clear();
            outputButtonContainer.getChildren().clear();
            if (outputStates.isEmpty()) {
                outputButtonContainer.getChildren().add(new Button("(none)"));
            } else {
                outputStates.forEach((pin, state) -> {
                    Button button = new Button("Gpio-" + pin);
                    outputButtons.put(pin, button);
                    outputButtonContainer.getChildren().add(button);
                });
            }
            refreshGui();
        }
    }

    private void refreshGui() {
        if ( gui!=null ) {
            inputButtons.forEach((pin, button) -> {
                if (inputStates.get(pin)) {
                    button.setStyle("-fx-background-color: red");
                } else {
                    button.setStyle("-fx-background-color: white");
                }
            });
            outputButtons.forEach((pin, button) -> {
                if (outputStates.get(pin)) {
                    button.setStyle("-fx-background-color: red");
                } else {
                    button.setStyle("-fx-background-color: white");
                }
            });
        }
    }

    ////////////////////
    // Simulated GPIO //
    ////////////////////

    @Override
    public boolean isValidPin(int pinNumber) {
        return pinNumber>=0 && pinNumber<=29;
    }

    @Override
    protected boolean digitalRead(int pinNumber) {
        return inputStates.get(pinNumber)==null ? outputStates.get(pinNumber) : inputStates.get(pinNumber);
    }

    @Override
    protected void provisionDigitalInput(int pinNumber, PinPullResistance resistance) {
        inputStates.put(pinNumber, false);
        redrawGui();
    }

    @Override
    protected void digitalWrite(int pinNumber, boolean state) {
        outputStates.put(pinNumber, state);
        refreshGui();
    }

    @Override
    protected void provisionDigitalOutput(int pinNumber, PinState initialState) {
        outputStates.put(pinNumber, initialState==PinState.HIGH);
        redrawGui();
    }

}
