package net.amarantha.gpiomofo.service.pixeltape.matrix.sprites;

import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.entity.Point;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;

public class SpriteRenderer {

    private Pattern pattern;

    public void init() {
        pattern = new Pattern(1, "#");
    }

    public void updatePattern() {
    }

    public void render(LightSurface surface, Point centre) {
        int x = centre.xInt() - (pattern.getWidth()/2);
        int y = centre.yInt() - (pattern.getHeight()/2);
        updatePattern();
        surface.layer(centre.getLayer()).draw(x, y, pattern);
    }

}
