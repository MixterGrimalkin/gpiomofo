package net.amarantha.gpiomofo.service.pixeltape.matrix;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.amarantha.utils.properties.PropertiesService;

import java.util.LinkedList;
import java.util.List;

public class SpriteFactory {

    @Inject private Injector injector;
    @Inject private PropertiesService props;

    private List<Sprite> sprites = new LinkedList<>();

    public <T extends Sprite> T make(Class<T> clazz) {
        T sprite = injector.getInstance(clazz);
        props.injectPropertiesOrExit(sprite);
        sprites.add(sprite);
        sprite.init();
        return sprite;
    }

}
