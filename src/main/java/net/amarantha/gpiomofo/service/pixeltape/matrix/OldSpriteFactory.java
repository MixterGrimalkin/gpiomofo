package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.utils.colour.RGB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OldSpriteFactory {

    @Inject private LightSurface surface;

    private List<OldSprite> oldSprites = new ArrayList<>();

    private int tailLength;

    public OldSpriteFactory setTailLength(int tailLength) {
        this.tailLength = tailLength;
        return this;
    }

    public int tailLength() {
        return tailLength;
    }

    public OldSprite create(int group, RGB colour) {
        OldSprite oldSprite = new OldSprite(group, colour, surface.width(), surface.height(), tailLength);
        oldSprites.add(oldSprite);
        return oldSprite;
    }

    public List<OldSprite> get() {
        return oldSprites;
    }

    public void forEach(Consumer<OldSprite> action) {
        oldSprites.forEach(action);
    }

}
