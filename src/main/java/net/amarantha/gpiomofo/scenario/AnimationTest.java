package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Ball;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Explosion;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Sprite;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.SpriteField;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.service.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.PI;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFrom;

public class AnimationTest extends Scenario {

    @Service private LightSurface surface;

    @Inject private SpriteField field;

    private Ball makeBall(RGB colour) {
        Ball ball = field.make(Ball.class, 0);
        ball.setSize(2).setColour(RGB.GREEN).setLayer(2);
        Sprite sparker =
                field.make(Explosion.class)
                        .setSparkCount(12)
                        .addColours(colour)
                        .setLayer(1);
        ball.show();
        ball.reset();
        ball.onBounce((axis,max)->{
            sparker.setPosition(ball.position());
            sparker.reset();
            sparker.show();
        });
        ball.setAngularDelta(PI/2, 1.0);
        return ball;
    }

    private List<Sprite> balls = new LinkedList<>();

    private int ballCount = 5;

    @Override
    public void startup() {

        for ( int i=0; i<ballCount; i++ ) {
            balls.add(makeBall(randomBetween(0,10)>5 ? RGB.RED : RGB.YELLOW ));
        }

        Sprite collideSparker =
                field.make(Explosion.class)
                        .setSparkCount(35)
                        .addColours(RGB.GREEN)
                        .setLayer(1);

        field.onCollide((s1, s2)->{
            double[] delta1 = s1.getLinearDelta();
            double[] delta2 = s2.getLinearDelta();
            s1.setLinearDelta(-delta1[X], -delta1[Y]);
            s2.setLinearDelta(-delta2[X], -delta2[Y]);
            collideSparker.setPosition(s1.position());
            collideSparker.reset();
            collideSparker.show();
            balls.forEach((ball)->{
                ball.setAngularDelta(randomBetween(0, 2*PI), randomBetween(7.0, 9.0));
            });
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                balls.forEach((ball)->{
                    if ( randomBetween(0,10) < 5 ) {
                        ball.setAngularDelta(randomBetween(0, 2*PI), randomBetween(1.0, 4.0));
                    }
                });
            }
        }, 0, 3000);


        field.setRefresh(50);
        field.start();

        int offset = 5;
        int width = surface.width()-(offset*2);
        int height = surface.height()-(offset*2);

//        surface.layer(3).fillRegion(offset, offset, width, height, RGB.RED);
//        surface.layer(4).fillRegion(offset+1, offset+1, width-2, height-2, RGB.BLACK);
//        surface.layer(5).fillRegion(offset+2, offset+2, width-4, height-4, RGB.RED);
//        surface.layer(6).fillRegion(offset+3, offset+3, width-6, height-6, RGB.BLACK);

    }
}
