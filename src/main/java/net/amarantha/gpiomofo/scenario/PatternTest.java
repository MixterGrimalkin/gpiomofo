package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.annotation.Named;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.font.Font;
import net.amarantha.gpiomofo.display.lightboard.LightSurface;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.service.Service;

public class PatternTest extends Scenario {

    @Service private LightSurface surface;

    @Named("Test") private Trigger testTrigger;

    @Override
    public void startup() {

        testTrigger.onFire((state)->{
        });

        Font f = new Font();
        f.loadFont("fonts/GreenpeaceFont.fnt");

        Pattern p = f.renderString("ABC");
//        Pattern p = new Pattern(3,"##--#---#");
//        Pattern p = new Pattern(3,"_##__#__#__#__##_#_#____");
        System.out.println(p.getWidth());
        System.out.println(p.getHeight());

        surface.layer(0).draw(5, 5, p);


    }
}
