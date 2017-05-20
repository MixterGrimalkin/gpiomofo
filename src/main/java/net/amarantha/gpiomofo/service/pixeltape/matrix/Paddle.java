package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;

class Paddle extends Sprite {

    @Inject private ButterPong game;

    int positionAlongWall;

    void setPlane(int positionAlongWall) {
        this.positionAlongWall = positionAlongWall;
    }

    void moveTo(int position) {
        setPositionAxis(game.getPaddleAxis(), position);
    }

    @Override
    protected void render() {
        for ( int i=0; i<game.getPaddleSize(); i++ ) {
            int[] point = new int[2];
            point[game.getWallAxis()] = positionAlongWall;
            point[game.getPaddleAxis()] = position[game.getPaddleAxis()] + i;
            surface.layer(layer).draw(point, colour);
        }
    }
}
