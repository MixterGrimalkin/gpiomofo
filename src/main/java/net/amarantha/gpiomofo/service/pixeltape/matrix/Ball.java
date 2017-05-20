package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;

import static net.amarantha.utils.math.MathUtils.randomBetween;
import static net.amarantha.utils.math.MathUtils.randomFlip;

class Ball extends Sprite {

    @Inject private ButterPong game;

    void reset() {
        setLinearDelta(0, 0);
        setPosition(surface.width()/2, surface.height()/2);
    }

    void start() {
        setLinearDeltaAxis(game.getPaddleAxis(), randomFlip(randomBetween(1.0, 3.0)));
        setLinearDeltaAxis(game.getWallAxis(), randomFlip(randomBetween(8.0, 12.0)));
    }

    @Override
    protected void bounce(int axis, boolean max) {
        if ( axis==game.getWallAxis() ) {
            Paddle p = max ? game.getRightPaddle() : game.getLeftPaddle();
            if ( position[game.getPaddleAxis()] >= p.position[game.getPaddleAxis()]
                    && position[game.getPaddleAxis()] <= p.position[game.getPaddleAxis()] + game.getPaddleSize()) {
            } else {
                if ( game.isBallInPlay() ) {
                    game.playRound();
                }
            }
        }
        super.bounce(axis, max);
    }
}
