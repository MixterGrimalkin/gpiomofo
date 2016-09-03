package net.amarantha.gpiomofo.pixeltape;

import com.google.inject.Inject;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.amarantha.gpiomofo.Main;
import net.amarantha.gpiomofo.utility.TimeGuard;

import static javafx.scene.paint.Color.color;
import static net.amarantha.gpiomofo.scenario.GingerlineBriefingRoom.*;

public class NeoPixelGUI implements NeoPixel {

    private int pixelCount;

    @Inject private Main main;
    @Inject private Stage stage;

//    private int[] widths = { 7, 21, 7, 21, 7 };
    private int[] widths = { 47, 47, 47, 47, PIPE_4_SIZE, PIPE_3_SIZE, PIPE_2_SIZE, PIPE_1_SIZE};

    private Circle[] pixels;
    private RGB[] colours;

    private double masterBrightness = 1.0;

    private Group tape;
    private int defaultWidth = 50;

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
            int widthCheck = y >= widths.length ? defaultWidth : widths[y] ;
            maxWidth = Math.max(maxWidth, widthCheck);
            if ( x >= widthCheck ) {
                x = 0;
                y++;
            }
            Circle pixel = new Circle(r, color(0, 0, 0));
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
            main.stop();
        });

    }

    @Override
    public void setPixelColourRGB(int pixel, RGB rgb) {
        if ( pixel < pixelCount ) {
            colours[pixel] = rgb;
        }
    }

    @Override
    public void setPixelColourRGB(int pixel, RGB colour, boolean forceRGB) {
        setPixelColourRGB(pixel, colour);
    }

    @Override
    public void setPixelColourRGB(int pixel, int red, int green, int blue) {
        setPixelColourRGB(pixel, new RGB(red, green, blue));
    }

    @Override
    public RGB getPixelRGB(int pixel) {
        return colours[pixel];
    }

    @Inject private TimeGuard guard;

    private long refreshInterval = 100;

    @Override
    public void render() {
        guard.every(refreshInterval, "render", ()->{
            for (int i = 0; i < pixels.length; i++) {
                if ( colours[i]!=null ) {
                    RGB rgb = colours[i].withBrightness(masterBrightness);
                    pixels[i].setFill(color(rgb.getRed() / 255.0, rgb.getGreen() / 255.0, rgb.getBlue() / 255.0));
                }
            }
        });
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

    @Override
    public void setMasterBrightness(double brightness) {
        masterBrightness = brightness;
    }

    @Override
    public double getMasterBrightness() {
        return masterBrightness;
    }
}
