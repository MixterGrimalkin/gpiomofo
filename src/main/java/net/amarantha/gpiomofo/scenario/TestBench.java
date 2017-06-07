package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.display.font.Font;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterflies;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.string.StringMap;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.*;

public class TestBench extends Scenario {

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
        surface.layer(7).clear();
        surface.layer(7).draw(10, 0, headingFont.renderString(lastHeading = params.get("heading")));
        surface.layer(7).draw(10, 17, dateFont.renderString(lastDate = params.get("date")));
        surface.layer(7).draw(10, 24, textFont.renderString(lastDescription = params.get("description")));
    }

    @Override
    public void setup() {

    }

    @Override
    public void startup() {

        Map<Integer, RGB> colours = new HashMap<>();
        colours.put(0, new RGB(255, 65, 29));
        colours.put(1, new RGB(65, 255, 29));
        colours.put(2, new RGB(65, 29, 255));

        butterflies.setLingerTime(100);
        butterflies.init(100, colours, 7);

        animationService.start();
        animationService.play(butterflies);

    }
}
