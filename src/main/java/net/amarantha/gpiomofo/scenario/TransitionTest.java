package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.font.Font;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.Butterfly;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Ball;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.SpriteField;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import static net.amarantha.utils.colour.RGB.*;

public class TransitionTest extends Scenario {

    @Service private LightSurface surface;

    @Inject
    private SpriteField sprites;

    @Override
    public void startup() {

        Font font = Font.fromFile("SimpleFont.fnt");

        Ball ball = sprites.make(Ball.class);
        ball.setSize(3).setColour(RGB.RED).setLinearDelta(3, 1).setLayer(7).show();

        Butterfly butterfly = sprites.make(Butterfly.class);
        butterfly.setColour(RGB.GREEN).show();


        sprites.setRefresh(50).start();

//        surface.layer(0).fillRegion(5, 25, 45, 25, RGB.GREEN);
//        surface.layer(0).fillRegion(50, 25, 100, 25, RGB.RED);
//
//        surface.layer(6).draw(80, 30, font.renderString("Some Words and Stuff", GREEN));
//        surface.layer(2).draw(21, 31, font.renderString("Some Words and Stuff", RGB.BLACK));
//
//        surface.layer(4).drawLine(5, 5, 50, 20, RGB.GREEN);



        surface.layer(4).fillCircle(40, 16, 6, RED);

        surface.layer(4).drawRect(20, 32, 50, 25, YELLOW);




    }
}
