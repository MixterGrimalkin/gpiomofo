package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.utils.colour.RGB;

import java.util.*;

import static net.amarantha.gpiomofo.core.Constants.X;
import static net.amarantha.gpiomofo.core.Constants.Y;
import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFlip;
import static net.amarantha.utils.math.MathUtils.round;

@Singleton
public class ButterPong extends Animation {

    @Inject private SpriteFactory sprites;

    private int delta;

    private int wallAxis = X;
    private int paddleAxis = Y;



    private int paddleSize = 11;
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
        ball = sprites.make(Ball.class);
        ball.setColour(RGB.YELLOW);
        leftPaddle = sprites.make(Paddle.class);
        leftPaddle.setColour(RGB.RED);
        leftPaddle.setPlane(0);
        rightPaddle = sprites.make(Paddle.class);
        rightPaddle.setPlane(surface.size()[wallAxis]-1);
        rightPaddle.setColour(RGB.GREEN);
        playRound();
    }

    @Override
    public void stop() {

    }

    private boolean ballInPlay = false;

    void playRound() {
        ballInPlay = false;
        ball.reset();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ball.start();
                ballInPlay = true;
            }
        }, 2000);
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
        surface.clear();
        if ( ball!=null && leftPaddle!=null && rightPaddle!=null ) {
            ball.updatePosition();
            ball.render();
            leftPaddle.render();
            rightPaddle.render();
        }
    }

    @Override
    public void onFocusAdded(int focusId) {

    }

    @Override
    public void onFocusRemoved(List<Integer> focusIds) {

    }
}
