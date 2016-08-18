package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.amarantha.gpiomofo.Main;
import net.amarantha.gpiomofo.utility.Now;

import static net.amarantha.gpiomofo.scenario.GingerlineBriefingRoom.*;

public class PixelTapeGUI implements PixelTape {

    private int pixelCount;

    @Inject private Main main;
    @Inject private Stage stage;

    private int[] widths = { 47, 47, 47, 47, PIPE_4_SIZE, PIPE_3_SIZE, PIPE_2_SIZE, PIPE_1_SIZE};

    private Circle[] pixels;
    private RGB[] colours;

    private Group tape;

    @Override
    public void init(final int pixelCount) {

        this.pixelCount = pixelCount;
        pixels = new Circle[pixelCount];
        colours = new RGB[pixelCount];

        // Build UI components
        final Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(30,30,30);");
        tape = new Group();
        pane.getChildren().add(tape);

        int margin = 5;
        int r = 5;
        int s = 1;

        int x = 0;
        int y = 0;
        int p = 0;
        int maxWidth = 0;
        while ( p < pixelCount ) {
            maxWidth = Math.max(maxWidth, widths[y]);
            if ( x >= widths[y] ) {
                x = 0;
                y++;
            }
            Circle pixel = new Circle(r, Color.color(0, 0, 0));
            pixels[p] = pixel;
            int left = margin + r + ((2*r)+s)*x;
            int top = margin + r + ((2*r)+s)*y;
            pixel.setCenterX(left);
            pixel.setCenterY(top);
            tape.getChildren().add(pixel);
            x++;
            p++;
        }

        // Start UI
        stage.setScene(new Scene(pane, (2*margin)+(r*2*maxWidth)+(s*(maxWidth-1)), (2*margin)+(r*2*(y+1))+(s*(y))));
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(true);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Pixel Tape");
        stage.show();

        // Shut down application when window is closed
        stage.setOnCloseRequest(event -> {
            System.out.println("Exit");
            main.stop();
        });

        System.out.println("Board Ready");

    }

    @Override
    public void setPixelColourRGB(int pixel, RGB rgb) {
        colours[pixel] = rgb;
    }

    @Override
    public void setPixelColourRGB(int pixel, int red, int green, int blue) {
        setPixelColourRGB(pixel, new RGB(red, green, blue));
    }

    @Override
    public RGB getPixelRGB(int pixel) {
        return colours[pixel];
    }

    @Inject private Now now;
    private long lastDrawn;
    private long refreshInterval = 100;

    @Override
    public void render() {
        if ( now.epochMilli()-lastDrawn >= refreshInterval ) {
            for (int i = 0; i < pixels.length; i++) {
                if ( colours[i]!=null ) {
                    pixels[i].setFill(Color.color(colours[i].getRed() / 255.0, colours[i].getGreen() / 255.0, colours[i].getBlue() / 255.0));
                }
            }
            lastDrawn = now.epochMilli();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void allOff() {
        for ( int i=0; i<pixelCount; i++ ) {
            colours[i] = new RGB(0,0,0);
        }
    }
}
