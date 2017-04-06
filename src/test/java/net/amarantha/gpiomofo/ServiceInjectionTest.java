package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.trigger.GpioTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.service.ServiceFactory;
import net.amarantha.utils.string.StringUtils;
import org.junit.Before;
import org.junit.runner.RunWith;

import static net.amarantha.utils.reflection.ReflectionUtils.reflectiveGet;
import static net.amarantha.utils.string.StringUtils.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class ServiceInjectionTest {

    @Inject private ServiceFactory services;
    @Inject private TriggerFactory triggers;

    @Before
    public void setup() {
        services.stopAll();
    }

    @Story
    public void testServiceInjection() {
        Trigger t = new GpioTrigger();
        assertNull(reflectiveGet(t, "gpio"));
        services.injectServices(t);
        assertNotNull(reflectiveGet(t, "gpio"));
    }

    @Story
    public void testTriggerFactory() {
        GpioTrigger t =
                triggers.create(GpioTrigger.class, asMap("pin=1; resistance=PULL_UP; triggerState=true; holdTime=1234"));
        assertNotNull(t);
        assertNotNull(reflectiveGet(t, "gpio"));
        assertEquals(1, ((Integer)reflectiveGet(t, "pinNumber")).intValue());
        assertEquals(PinPullResistance.PULL_UP, reflectiveGet(t, "resistance"));
        assertEquals(true, reflectiveGet(t, "triggerState"));
        assertEquals(1234, ((Integer)reflectiveGet(t, "holdTime")).intValue());
    }


}
