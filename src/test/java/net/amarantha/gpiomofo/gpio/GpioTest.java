package net.amarantha.gpiomofo.gpio;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.TestModule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class GpioTest {

    @Inject private GpioProvider gpio;

    @Story
    @Ignore
    public void basic_test() {

        given_digital_output_pin_$1(0);

        when_set_output_$1_to_$2(0, true);

        then_pin_$1_is_$2(0, true);

    }

    void given_digital_output_pin_$1(int pin) {
        gpio.digitalOutput(pin);
    }

    void when_set_output_$1_to_$2(int pin, boolean state) {
        gpio.digitalOutput(pin, state);
    }

    void then_pin_$1_is_$2(int pin, boolean state) {
        Assert.assertEquals(state, gpio.read(pin));
    }

}
