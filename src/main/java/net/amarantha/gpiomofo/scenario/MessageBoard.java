package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.font.Font;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterflies;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import java.util.*;

import static net.amarantha.utils.colour.RGB.*;

public class MessageBoard extends Scenario {

    @Service private LightSurface surface;

    @Inject private WebService http;

    @Inject private Butterflies butterflies;

    @Inject private AnimationService animationService;

    private Font headingFont = Font.fromFile("LargeFont.fnt");
    private Font dateFont = Font.fromFile("SmallFont.fnt");
    private Font textFont = Font.fromFile("SimpleFont.fnt");

    @Override
    public List<ApiParam> getApiTemplate() {
        List<ApiParam> result = new LinkedList<>();
        result.add(new ApiParam("heading", "Event Name", lastHeading));
        result.add(new ApiParam("date", "Date/Time", lastDate));
        result.add(new ApiParam("description", "Details", lastDescription));
        return result;
    }

    private String lastHeading = "";
    private String lastDate = "";
    private String lastDescription = "";

    @Override
    public void incomingApiCall(Map<String, String> params) {
        lastHeading = params.get("heading");
        lastDate = params.get("date");
        lastDescription = params.get("description");
        if ( lastHeading!=null || lastDate!=null || lastDescription!= null ) {
            surface.layer(7).clear();
            drawCentred(params.get("heading"), 3);
            drawCentred(params.get("date"), 21);
            drawCentred(params.get("description"), 39);
        }
    }

    private void drawCentred(String text, int y) {
        if (text != null) {
            Pattern pattern = headingFont.renderString(text, YELLOW);
            Pattern shadow = headingFont.renderString(text, BLACK);
            int x = (surface.width() - pattern.getWidth()) / 2;
            surface.layer(7).drawWithOutline(x, y, pattern, BLACK);
//            surface.layer(7).draw(x - 1, y - 1, shadow);
//            surface.layer(7).draw(x + 1, y - 1, shadow);
//            surface.layer(7).draw(x - 1, y + 1, shadow);
//            surface.layer(7).draw(x + 1, y + 1, shadow);
//            surface.layer(7).draw(x, y, pattern);
        }
    }



    @Override
    public void setup() {
    }

    @Override
    public void startup() {

        Map<Integer, RGB> colours = new HashMap<>();
        colours.put(0, new RGB(255, 0, 0));

        butterflies.setLingerTime(100);
        butterflies.init(500, colours, 7);
        butterflies.setUseAudio(false);

        animationService.start();
        animationService.add("Butterflies", butterflies);
        animationService.play("Butterflies");

        Pattern leaves = Pattern.fromImage("images/leaves.jpg", 256, 64);
        surface.layer(3).draw(0,0,leaves);
        surface.colouriseLayer(3, GREEN);

        butterflies.setTargetJitter(30, 60);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if ( !sweeping ) {
                    sweep(0, -10, 10);
                }
            }
        }, 5000, 20000);


    }

    private boolean sweeping = false;

    private void sweep(final int id, final int start, final int width) {
        sweeping = true;
        butterflies.addFocus(id, start, 32);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                butterflies.removeFocus(id);
                if ( start <= surface.width()+32 ) {
                    sweep(id+1, start+32, width);
                } else {
                    sweeping = false;
                }

            }
        }, 500);

    }
}
