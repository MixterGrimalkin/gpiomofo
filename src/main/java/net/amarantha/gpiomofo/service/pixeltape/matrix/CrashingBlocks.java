package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.audio.AudioFile;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Ball;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Explosion;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.Sprite;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.properties.entity.PropertyNotFoundException;

import java.util.*;

import static java.lang.Math.PI;
import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFrom;

@PropertyGroup("CrashingBlocks")
public class CrashingBlocks extends Animation {

    @Inject private OscService osc;

    @Property("MinSpeed") private double minSpeed = 0.5;
    @Property("MaxSpeed") private double maxSpeed = 3.0;
    @Property("Balls") private int ballCount;
    @Property("BallColour") private RGB ballColour;
    @Property("LowOSC") private OscCommand lowOsc;
    @Property("MedOSC") private OscCommand medOsc;
    @Property("HighOSC") private OscCommand highOsc;
    @Property("CrashOSC") private OscCommand crashOsc;

    private List<RGB> explosionColours;

    private List<OscCommand> dingCommands = new ArrayList<>();
    @Inject
    private PropertiesService props;

    private int polyphony = 1;
    private int soundsPlaying = 0;
    private boolean explosionPlaying = false;

    @Override
    public void init() {


//        dingSounds.add(high);
//        dingSounds.add(med);
//        dingSounds.add(low);
//
//        explosion.onPlaybackFinished(() -> {
//            explosionPlaying= false;
//        });

        try {
            props.injectProperties(this);
            dingCommands.add(highOsc);
            dingCommands.add(lowOsc);
            dingCommands.add(medOsc);
            explosionColours = props.getRgbList("CrashingBlocks", "ExplosionColours");

            for (int i = 0; i < ballCount; i++) {
                balls.add(makeBall());
            }

            Sprite collideSparker =
                    field.make(Explosion.class)
                            .setSparkCount(15)
                            .addColours(ballColour)
                            .setLayer(1);

            field.onCollide((s1, s2) -> {
//                if ( !explosionPlaying ) {
//                    explosionPlaying = true;
//                    explosion.play();
//                }
                osc.send(crashOsc);
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

    private AudioFile addSound(String filename) {
        AudioFile audio = new AudioFile(filename);
        audio.onPlaybackFinished(()->soundsPlaying--);
        return audio;
    }

    private void playSound(AudioFile audio) {
        if ( soundsPlaying < polyphony ) {
            audio.play();
            soundsPlaying++;
        }
    }

    @Override
    public void start() {
        field.start();

    }

    @Override
    public void stop() {
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
//            playSound(randomFrom(dingSounds));
            osc.send(randomFrom(dingCommands));
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

    private List<AudioFile> dingSounds = new ArrayList<>();
    private AudioFile low = addSound("audio/low.mp3");
    private AudioFile med = addSound("audio/med.mp3");
    private AudioFile high = addSound("audio/high.mp3");
    private AudioFile explosion = addSound("audio/organchord.mp3");




    private List<Sprite> balls = new LinkedList<>();

    @Override
    public void onFocusAdded(int focusId) {
    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {
    }
}
