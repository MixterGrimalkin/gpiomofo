package net.amarantha.gpiomofo.display.pixeltape;

import com.google.inject.Inject;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.core.GpioMofo;
import net.amarantha.gpiomofo.service.gui.Gui;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.file.FileService;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.time.TimeGuard;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static javafx.scene.paint.Color.color;
import static net.amarantha.gpiomofo.core.Constants.neoPixelGUIWidths;
import static net.amarantha.utils.math.MathUtils.max;
import static net.amarantha.utils.math.MathUtils.round;
import static net.amarantha.utils.shell.Utility.log;

@PropertyGroup("NeoPixelGUI")
public class NeoPixelGUI implements NeoPixel {

    public static int defaultWidth = 25;

    private int pixelCount;

    @Inject private Gui gui;
    @Inject private GpioMofo application;
    @Inject private PropertiesService props;
    @Inject private FileService files;

    @Property("LedSize") private int radius = 5;
    @Property("Spacer") private int spacer = 2;
    @Property("CustomLayout") private String customLayoutFilename;

    private Circle[] pixels;
    private RGB[] colours;

    private double masterBrightness = 1.0;

    private Group tape;

    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    private Map<Integer, int[]> customLayout;

    private void loadCustomLayout() {
        if ( files.exists(customLayoutFilename) ) {
            customLayout = new HashMap<>();
            String data = files.readFromFile(customLayoutFilename);
            String[] rows = data.split("\n");
            for (int i = 0; i < rows.length; i++) {
                String[] coords = rows[i].split(",");
                if (coords.length == 2) {
                    customLayout.put(i, new int[]{parseInt(coords[0]), parseInt(coords[1])});
                } else if ( coords.length == 3 ) {
                    customLayout.put(i, new int[]{parseInt(coords[0]), parseInt(coords[1]), parseInt(coords[2])});
                }
            }
        }
    }

    private void saveCustomLayout() {
        if ( customLayout!=null ) {
            StringBuilder sb = new StringBuilder();
            customLayout.forEach((p, point) -> {
                sb.append(point[0]).append(",").append(point[1]);
                if ( point.length>2 ) sb.append(",").append(point[2]);
                sb.append("\n");
            });
            files.writeToFile(customLayoutFilename, sb.toString());
        }
    }

    private void addPixelMouseHandler(Circle pixel, final int pixelNumber) {
        pixel.setOnMousePressed((event -> {
            System.out.println("Pixel: " + pixelNumber);
            int[] currentLayout = customLayout.get(pixelNumber);
            if ( event.isControlDown() ) {
                int newRadius = (int)(pixel.getRadius() + (event.isShiftDown() ? -5 : 5));
                if ( newRadius > 0 ) {
                    pixel.setRadius(newRadius);
                    customLayout.put(pixelNumber, new int[]{currentLayout[0], currentLayout[1], newRadius});
                    saveCustomLayout();
                }
            }
        }));
        pixel.setOnMouseClicked(event->{
        });
        pixel.setOnMouseReleased(event -> {
            saveCustomLayout();
        });
        pixel.setOnMouseDragged((event -> {
            pixel.setCenterX(event.getX());
            pixel.setCenterY(event.getY());
            customLayout.put(pixelNumber, new int[]{round(event.getX()), round(event.getY()), round(pixel.getRadius())});
        }));
    }

    @Override
    public void init(final int pixelCount) {

        log("Starting GUI NeoPixel...");

        props.injectPropertiesOrExit(this);

        loadCustomLayout();

        this.pixelCount = pixelCount;
        pixels = new Circle[pixelCount];
        colours = new RGB[pixelCount];

        // Build UI components
        final Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(30,30,30);");
        tape = new Group();
        pane.getChildren().add(tape);

        int margin = 5;

        int x = 0;
        int y = 0;
        int p = 0;
        int maxWidth = 0;
        while (p < pixelCount) {
            int widthCheck = y >= neoPixelGUIWidths.length ? defaultWidth : neoPixelGUIWidths[y];
            maxWidth = Math.max(maxWidth, widthCheck);
            if (x >= widthCheck) {
                x = 0;
                y++;
            }
            Circle pixel = new Circle(radius, color(0, 0, 0));
            pixels[p] = pixel;
            int left = margin + radius + ((2 * radius) + spacer) * x;
            int top = margin + radius + ((2 * radius) + spacer) * y;
            if (customLayout != null) {
                if (p < customLayout.size()) {
                    int[] layout = customLayout.get(p);
                    left = layout[0];
                    top = layout[1];
                    if ( layout.length > 2 ) {
                        pixel.setRadius(layout[2]);
                    }
                } else {
                    customLayout.put(p, new int[]{left, top, radius});
                }
                addPixelMouseHandler(pixel, p);
            }
            pixel.setCenterX(left);
            pixel.setCenterY(top);
            tape.getChildren().add(pixel);
            x++;
            p++;
        }

        saveCustomLayout();

        // RIGHT-CLICK => reset UI window (because it messes up all the time - bug in JaxaFX maybe???)
        pane.setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                stage.hide();
                stage = new Stage();
                init(pixelCount);
            } else {
                if (e.isControlDown() && e.isAltDown()) {
                    stage.hide();
                    stage = new Stage();
                    init(pixelCount);
                }
            }
        });


        stage = gui.addStage("NeoPixel");
        if ( customLayout==null ) {
            width = (2 * margin) + (radius * 2 * maxWidth) + (spacer * (maxWidth - 1));
            height = (2 * margin) + (radius * 2 * (y + 1)) + (spacer * (y));
        } else {
            width = 0;
            height = 0;
            customLayout.forEach((k,point)->{
                width = max(width, point[0]+(point.length>2?point[2]:radius)+spacer);
                height = max(height, point[1]+(point.length>2?point[2]:radius)+spacer);
            });
        }
        stage.setScene(new Scene(pane, width, height));
        stage.show();

    }

    private int width;
    private int height;

    private Stage stage;

    @Override
    public void setPixelColourRGB(int pixel, RGB rgb) {
        if (pixel < pixelCount) {
            colours[pixel] = rgb;
        }
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
        guard.every(refreshInterval, "render", () -> {
            for (int i = 0; i < pixels.length; i++) {
                if (colours[i] != null) {
                    RGB rgb = colours[i].withBrightness(masterBrightness);
                    if (pixels[i] != null) {
                        pixels[i].setFill(color(rgb.getRed() / 255.0, rgb.getGreen() / 255.0, rgb.getBlue() / 255.0));
                    }
                }
            }
        });
    }

    @Override
    public void close() {
        if (customLayout != null) {
            saveCustomLayout();
        }
    }

    @Override
    public void allOff() {
        for (int i = 0; i < pixelCount; i++) {
            colours[i] = new RGB(0, 0, 0);
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
