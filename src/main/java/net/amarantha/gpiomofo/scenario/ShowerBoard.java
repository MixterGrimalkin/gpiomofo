package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.animation.AnimationService;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.entity.Region;
import net.amarantha.gpiomofo.display.font.Font;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Rain;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Scroller;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import java.util.*;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.colour.RGB.*;

public class ShowerBoard extends Scenario {

    @Service
    private LightSurface surface;

    @Inject
    private AnimationService animationService;
    @Inject
    private Rain rain;

    @Inject
    private Scroller scroller;

    private String scrollingMessage;
    private boolean showersOpen = false;

    private String femaleTicket = "...";
    private String maleTicket = "...";

    private int rainLayer = 0;
    private int labelLayer = 1;
    private int femaleLayer = 2;
    private int maleLayer = 3;
    private int scrollBorderLayer = 4;
    private int scrollerLayer = 5;
    private int imageLayer = 6;

    private Font largeFont = Font.fromFile("LargeFont.fnt");
    private Font smallFont = Font.fromFile("SmallFont.fnt");
    private Font mediumFont = Font.fromFile("SimpleFont.fnt");

    private int ticketNumbersTop = 1;

    private int labelLeft = 14;
    private int labelTop = ticketNumbersTop + 6;

    private int femaleLeft = labelLeft + 65;
    private int maleLeft = femaleLeft + 52;

    private void drawTicketNumbers(String female, String male) {
        if (showersOpen) {
            RGB femaleColour = femaleTicket.equals(female) ? YELLOW : GREEN;
            RGB maleColour = maleTicket.equals(male) ? YELLOW : GREEN;
            boolean changed = !(femaleTicket.equals(female) && maleTicket.equals(male));

            surface.layer(femaleLayer)
                    .clear()
                    .draw(femaleLeft, ticketNumbersTop, largeFont.renderString("@", femaleColour))
                    .draw(femaleLeft + 13, ticketNumbersTop, largeFont.renderString(female, femaleColour));

            surface.layer(maleLayer)
                    .clear()
                    .draw(maleLeft, ticketNumbersTop, largeFont.renderString("~", maleColour))
                    .draw(maleLeft + 13, ticketNumbersTop, largeFont.renderString(male, maleColour));

            femaleTicket = female;
            maleTicket = male;

            if (changed) {
                drawArrow(GREEN, true);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        drawTicketNumbers(femaleTicket, maleTicket);
                    }
                }, 5000);
            } else {
                drawArrow(RED, false);
            }
        }
    }

    @Override
    public List<ApiParam> getApiTemplate() {
        List<ApiParam> result = new LinkedList<>();
        result.add(new ApiParam("female", "Female Ticket", femaleTicket + ""));
        result.add(new ApiParam("male", "Male Ticket", maleTicket + ""));
        result.add(new ApiParam("scroller", "Scrolling Message", scrollingMessage));
        return result;
    }

    @Override
    public void incomingApiCall(Map<String, String> params) {
        String openState = params.get("state");
        if (openState != null) {
            showersOpen = "open".equalsIgnoreCase(openState);
            if (showersOpen) {
                drawShowersOpenScreen();
            } else {
                drawShowersClosedScreen();
            }
        }
        if (showersOpen) {
            scrollingMessage = params.get("scroller");
            if (scrollingMessage != null) {
                scroller.setPattern(mediumFont.renderString("{green}" + scrollingMessage));
            }
            if (params.get("female") != null && params.get("male") != null) {
                drawTicketNumbers(params.get("female"), params.get("male"));
            }
        }
    }

    @Override
    public void setup() {

    }

    private void drawShowersOpenScreen() {
        surface.clear();
        drawArrow(RED, false);
        drawScroller();
        animationService.play("Scroller");
    }

    private void drawShowersClosedScreen() {
        animationService.stop("Scroller");
        surface.clear();
        surface.layer(imageLayer).draw(0, 0, image);
    }

    private Pattern image;

    @Override
    public void startup() {

        image = Pattern.fromImage("images/gp192x32.jpg", 192, 32);

        rain.setRefreshInterval(50);
        scroller.setRefreshInterval(30);

        animationService.add("Rain", rain);
        animationService.add("Scroller", scroller);
        animationService.start();

        drawShowersClosedScreen();

        animationService.play("Rain");
    }

    private void drawArrow(RGB colour, boolean invert) {
        int arrowThickness = 8;
        int arrowWidth = 60;
        int[] topLineLeft = new int[]{labelLeft - 7, labelTop - 2};
        int[] topLineRight = new int[]{topLineLeft[X] + arrowWidth, topLineLeft[Y]};
        int[] bottomLineLeft = new int[]{topLineLeft[X], topLineLeft[Y] + arrowThickness};
        int[] bottomLineRight = new int[]{topLineRight[X], topLineLeft[Y] + arrowThickness};
        int[] arrowRightPoint = new int[]{topLineRight[X] + 4, topLineRight[Y] + (arrowThickness / 2)};
        int[] arrowLeftPoint = new int[]{topLineLeft[X] + 4, topLineLeft[Y] + (arrowThickness / 2)};
        surface.layer(labelLayer)
                .clear()
                .drawLine(topLineLeft, topLineRight, colour)
                .drawLine(bottomLineLeft, bottomLineRight, colour)
                .drawLine(topLineRight, arrowRightPoint, colour)
                .drawLine(bottomLineRight, arrowRightPoint, colour)
                .drawLine(topLineLeft, arrowLeftPoint, colour)
                .drawLine(bottomLineLeft, arrowLeftPoint, colour);
        RGB textColour = colour;
        RGB fillColour = BLACK;
        if (invert) {
            fillColour = colour;
            textColour = BLACK;
        }
        surface.layer(labelLayer)
                .floodFill(arrowLeftPoint[X] + 20, arrowLeftPoint[Y], fillColour)
                .draw(labelLeft, labelTop, smallFont.renderString("NEXT SHOWERS", textColour))
        ;
    }

    private void drawScroller() {
        RGB scrollerColour = GREEN;
        int[] scrollerTopLeft = new int[]{2, surface.height() - 13};
        int scrollerWidth = 188;
        int scrollerHeight = 11;
        surface.layer(scrollBorderLayer)
                .clear()
                .drawRect(scrollerTopLeft[X], scrollerTopLeft[Y], scrollerWidth, scrollerHeight, scrollerColour)
                .floodFill(scrollerTopLeft[X] + 5, scrollerTopLeft[Y] + 5, BLACK)
                .draw(scrollerTopLeft[X], scrollerTopLeft[Y], BLACK)
                .draw(scrollerTopLeft[X] + 1, scrollerTopLeft[Y] + 1, scrollerColour)
                .draw(scrollerTopLeft[X] + scrollerWidth - 1, scrollerTopLeft[Y], BLACK)
                .draw(scrollerTopLeft[X] + scrollerWidth - 2, scrollerTopLeft[Y] + 1, scrollerColour)
                .draw(scrollerTopLeft[X], scrollerTopLeft[Y] + scrollerHeight - 1, BLACK)
                .draw(scrollerTopLeft[X] + 1, scrollerTopLeft[Y] + scrollerHeight - 2, scrollerColour)
                .draw(scrollerTopLeft[X] + scrollerWidth - 1, scrollerTopLeft[Y] + scrollerHeight - 1, BLACK)
                .draw(scrollerTopLeft[X] + scrollerWidth - 2, scrollerTopLeft[Y] + scrollerHeight - 2, scrollerColour)
        ;
        scroller.init(scrollerLayer, new Region(scrollerTopLeft[X] + 2, scrollerTopLeft[Y], scrollerWidth - 4, scrollerHeight));
        scroller.setY(scrollerTopLeft[Y] + 2);
    }
}
