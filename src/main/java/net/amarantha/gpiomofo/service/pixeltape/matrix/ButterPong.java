package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.service.pixeltape.matrix.sprites.*;
import net.amarantha.utils.colour.RGB;

import java.util.*;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFlip;
import static net.amarantha.utils.math.MathUtils.round;

@Singleton
public class ButterPong extends Animation {

    @Inject private SpriteField field;

    private int delta;

    private int wallAxis = X;
    private int paddleAxis = Y;



    private int paddleSize = 8;
    private RGB paddleColour;

    private Map<Integer, Paddle> paddles = new HashMap<>();

    private Ball ball;
    private Paddle leftPaddle;
    private Paddle rightPaddle;

    public Ball getBall() {
        return ball;
    }

    public Paddle getLeftPaddle() {
        return leftPaddle;
    }

    public Paddle getRightPaddle() {
        return rightPaddle;
    }

    public int getPaddleSize() {
        return paddleSize;
    }

    public boolean isBallInPlay() {
        return ballInPlay;
    }

    public int getWallAxis() {
        return wallAxis;
    }

    public int getPaddleAxis() {
        return paddleAxis;
    }

    @Override
    public void start() {

//        surface.clear();
//        final Sprite explosion =
//                field.make(Explosion.class)
//                        .setSparkCount(150)
//                        .setSpeed(0.1, 4.0)
//                        .addColours(RGB.RED, RGB.YELLOW)
//                        .setLayer(1);
//
//        ball = field.make(Ball.class);
//        ball.setLayer(2);
//        ball.setColour(RGB.YELLOW);
//        ball.show();
//        ball.onBounce((axis, max)->{
//            if ( axis==wallAxis ) {
//                Paddle p = max ? rightPaddle : leftPaddle;
//                if ( ball.position(paddleAxis) >= p.position(paddleAxis)
//                        && ball.position(paddleAxis) < p.position(paddleAxis) + paddleSize) {
//                } else {
//                    if ( isBallInPlay() ) {
//                        ballInPlay = false;
//                        ball.hide();
//                        ball.setLinearDelta(0, 0);
//                        explosion.setColour(max ? RGB.RED : RGB.GREEN);
//                        explosion.setPosition(ball.position());
//                        explosion.reset();
//                        explosion.show();
//                        System.out.println("BOOM!");
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                ball.show();
//                                playRound();
//                            }
//                        }, 3500);
//                    }
//                }
//            }
//        });
//
//        leftPaddle = field.make(Paddle.class);
//        leftPaddle.setColour(RGB.RED);
//        leftPaddle.setPlane(0);
//        leftPaddle.setLayer(2);
//        leftPaddle.show();
//
//        rightPaddle = field.make(Paddle.class);
//        rightPaddle.setPlane(surface.size()[wallAxis]-1);
//        rightPaddle.setColour(RGB.GREEN);
//        rightPaddle.setLayer(2);
//        rightPaddle.show();
//
//        field.setRefresh(100);
//        field.start();
//
//        playRound();
    }

    @Override
    public void stop() {
//        ball.hide();
//        leftPaddle.hide();
//        rightPaddle.hide();
//        field.stop();
    }

    private boolean ballInPlay = false;

    public void playRound() {
//        field.resume();
//        ballInPlay = false;
//        ball.reset();
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("BEGIN!");
////                ball.show();
////                ball.setLinearDeltaAxis(paddleAxis, randomFlip(randomBetween(5.0, 6.0)));
////                ball.setLinearDeltaAxis(wallAxis, randomFlip(randomBetween(5.0, 11.0)));
////                ballInPlay = true;
//            }
//        }, 2000);
    }

    public void setPaddleAxis(int paddleAxis) {
        this.paddleAxis = paddleAxis;
    }

    public void setLeftPosition(double value) {
        movePaddle(leftPaddle, value);
    }

    public void setRightPosition(double value) {
        movePaddle(rightPaddle, value);
    }

    private void movePaddle(Paddle paddle, double value) {
        if ( paddle!=null ) {
            paddle.moveTo(round(value * (surface.size()[paddleAxis] - paddleSize)));
        }
    }

    public void setPaddleColour(RGB paddleColour) {
        this.paddleColour = paddleColour;
    }

    @Override
    public void refresh() {
//        if ( ball!=null && leftPaddle!=null && rightPaddle!=null ) {
//            ball.updateCentre();
//            ball.render();
//            leftPaddle.render();
//            rightPaddle.render();
//        }
    }

    @Override
    public void onFocusAdded(int focusId) {

    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {

    }
}
