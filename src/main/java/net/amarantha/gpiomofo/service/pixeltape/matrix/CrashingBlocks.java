package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Ball;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Explosion;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Sprite;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.properties.entity.PropertyNotFoundException;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.PI;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;

@PropertyGroup("CrashingBlocks")
public class CrashingBlocks extends Animation {

    @Inject private OscService osc;

    @Property("AudioPlayerIP") private String playerIp;
    @Property("AudioPlayerPort") private int playerPort;
    @Property("MinSpeed") private double minSpeed = 0.5;
    @Property("MaxSpeed") private double maxSpeed = 3.0;
    @Property("Balls") private int ballCount;
    @Property("BallColour") private RGB ballColour;
    @Property("BackgroundSound") private String backgroundSoundFilename;
    private OscCommand backgroundSoundPlay;
    private OscCommand backgroundSoundStop;

    private List<RGB> explosionColours;

    @Inject private PropertiesService props;

    @Override
    public void init() {

        try {
            props.injectProperties(this);
            explosionColours = props.getRgbList("CrashingBlocks", "ExplosionColours");

            backgroundSoundPlay = new OscCommand(playerIp, playerPort, backgroundSoundFilename+"/loop");
            backgroundSoundStop = new OscCommand(playerIp, playerPort, backgroundSoundFilename+"/stop");

            for (int i = 0; i < ballCount; i++) {
                balls.add(makeBall());
            }

            Sprite collideSparker =
                    field.make(Explosion.class)
                            .setSparkCount(15)
                            .addColours(ballColour)
                            .setLayer(1);

            field.onCollide((s1, s2) -> {
                double[] delta1 = s1.getLinearDelta();
                double[] delta2 = s2.getLinearDelta();
                s1.setLinearDelta(-delta1[X], -delta1[Y]);
                s2.setLinearDelta(-delta2[X], -delta2[Y]);
                collideSparker.setPosition(s1.position());
                collideSparker.reset();
                collideSparker.show();
                balls.forEach((ball) -> {
                    ball.setAngularDelta(randomBetween(0, 2 * PI), randomBetween(minSpeed, maxSpeed));
                });
            });

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    balls.forEach((ball) -> {
                        if (randomBetween(0, 10) < 5) {
                            ball.setAngularDelta(randomBetween(0, 2 * PI), randomBetween(minSpeed, maxSpeed));
                        }
                    });
                }
            }, 0, 3000);

            field.setRefresh(50);

        } catch (PropertyNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        osc.send(backgroundSoundPlay);
        field.start();

    }

    @Override
    public void stop() {
        osc.send(backgroundSoundStop);
        field.stop();
    }

    @Override
    public void refresh() {

    }

    private Ball makeBall() {
        Ball ball = field.make(Ball.class, 0);
        ball.setSize(2).setColour(ballColour).setLayer(2);
        Explosion sparker =
                field.make(Explosion.class)
                        .setSparkCount(12)
                        .addColours(explosionColours.toArray(new RGB[1]));
        sparker.setLayer(1);
        ball.show();
        ball.reset();
        ball.onBounce((axis, max) -> {
            sparker.setPosition(ball.position());
            double[] ballDelta = ball.getLinearDelta();
            double averageDelta = (ballDelta[X] + ballDelta[Y])/2.0;
            sparker.setSpeed(0.75*averageDelta, 1.25*averageDelta);
            sparker.reset();
            sparker.show();
        });
        ball.setAngularDelta(PI / 2, 1.0);
        return ball;
    }

    private List<Sprite> balls = new LinkedList<>();

    @Override
    public void onFocusAdded(int focusId) {
    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {
    }
}
