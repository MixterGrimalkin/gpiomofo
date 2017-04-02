package net.amarantha.gpiomofo.service.gui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.amarantha.gpiomofo.core.GpioMofo;
import net.amarantha.utils.properties.PropertiesService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static net.amarantha.gpiomofo.service.shell.Utility.log;

@Singleton
public class Gui {

    private PropertiesService props;

    @Inject
    public Gui(GpioMofo application, Stage stage, PropertiesService props) {

        log(" - ( simulation mode ) -", true);

        this.props = props;

        File logoFile = new File(props.getString("Gui/LogoFile", "/home/grimalkin/code/gpiomofo/gpiomofo-logo.png"));
        Image logo = new Image(logoFile.toURI().toString());
        double width = logo.getWidth();
        double height = logo.getHeight();

        HBox box = new HBox();
        ImageView view = new ImageView();
        view.setImage(logo);
        box.getChildren().add(view);

        Group root = new Group();
        Scene scene = new Scene(root, width, height);
        scene.setFill(Color.BLACK);
        root.getChildren().add(box);

        stage.setScene(scene);

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.resizableProperty().setValue(false);
        stage.setAlwaysOnTop(true);

        final String title = "GpioMofo";
        windows.put(title, stage);
        stage.setTitle(title);
        restorePosition(title);

        stage.setOnCloseRequest(event -> {
            savePosition(title);
            windows.forEach((name,window)->window.close());
            application.stop();
        });

        stage.show();
    }

    private Map<String, Stage> windows = new HashMap<>();

    public Stage addStage(String title) {
        final Stage stage = new Stage();
        windows.put(title, stage);
        stage.setTitle(title);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(Event::consume);

        stage.setOnHiding(event -> savePosition(title));
        restorePosition(title);

        return stage;
    }

    private String makePropName(String title) {
        return "Gui/Window-"+title.replaceAll(" ", "-");
    }

    private String makePropString(String title) {
        Stage stage = windows.get(title);
        return stage.getX()+","+stage.getY();
    }

    private void savePosition(String title) {
        props.setProperty(makePropName(title), makePropString(title));
    }

    private void restorePosition(String title) {
        String options = props.getString(makePropName(title), makePropString(title));
        String[] pieces = options.split(",");
        if ( pieces.length==2 ) {
            try {
                double x = Double.parseDouble(pieces[0]);
                double y = Double.parseDouble(pieces[1]);
                Stage stage = windows.get(title);
                stage.setX(x);
                stage.setY(y);
            } catch (NumberFormatException ignored){}
        }
    }


}
