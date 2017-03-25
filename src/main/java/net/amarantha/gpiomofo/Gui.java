package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

import static net.amarantha.gpiomofo.utility.Utility.log;

@Singleton
public class Gui {

    @Inject
    public Gui(GpioMofo application, Stage stage) {

        log("                s i m u l a t i o n\n");

        File logoFile = new File("/home/grimalkin/code/gpiomofo/gpiomofo-logo.png");

        Image logo = new Image(logoFile.toURI().toString());
        ImageView view = new ImageView();
        view.setImage(logo);

        Group root = new Group();
        Scene scene = new Scene(root, logo.getWidth(), logo.getHeight());
        scene.setFill(Color.BLACK);
        HBox box = new HBox();
        box.getChildren().add(view);
        root.getChildren().add(box);

        stage.setScene(scene);

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.resizableProperty().setValue(false);
        stage.setAlwaysOnTop(true);

        stage.setTitle("GpioMofo");
        stage.show();

        stage.setOnCloseRequest(event -> application.stopApplication());

    }

    public Stage addStage(String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(event -> event.consume());
        return stage;
    }


}
