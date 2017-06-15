package net.amarantha.gpiomofo.service.pixeltape.matrix.sprites;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.pixeltape.matrix.ButterPong;

public class Paddle extends Sprite {

    @Inject private ButterPong game;

    private int positionAlongWall;

    public void setPlane(int positionAlongWall) {
        this.positionAlongWall = positionAlongWall;
    }

    public void moveTo(int position) {
//        setPositionAxis(game.getPaddleAxis(), position);
    }

//    @Override
//    public void doRender() {
//        for ( int i=0; i<game.getPaddleSize(); i++ ) {
//            int[] point = new int[2];
//            point[game.getWallAxis()] = positionAlongWall;
////            point[game.getPaddleAxis()] = position(game.getPaddleAxis()) + i;
////            surface.layer(layer).draw(point, colour);
//        }
//    }
}
