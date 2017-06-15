package net.amarantha.gpiomofo.service.pixeltape.matrix.sprites;

import net.amarantha.gpiomofo.display.entity.Point;

public class MotionMaker {

    private Point currentCentre = new Point(0,0);
    public Point updateCentre(Point centre) {
        return centre;
    }

    public void setCentre(Point centre) {
        currentCentre = centre;
    }

    public Point currentCentre() {
        return currentCentre;
    }

}

