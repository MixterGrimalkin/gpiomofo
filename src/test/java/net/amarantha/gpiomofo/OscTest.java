package net.amarantha.gpiomofo;

import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.utils.osc.OscCommand;
import org.junit.runner.RunWith;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class OscTest extends TestBase {

    @Story
    public void test_osc_targets() {

        OscCommand on1 = new OscCommand("127.0.0.1", 8080, "theaddress");
        OscCommand off = new OscCommand("127.0.0.2", 8080, "anotheraddress");
        OscCommand on2 = new OscCommand("127.0.0.3", 8080, "nicething");

        Trigger trigger1 = given_trigger_on_pin_$1(0);
        Trigger trigger2 = given_trigger_on_pin_$1(1);
        Target target1 = given_osc_target(on1, off);
        Target target2 = given_osc_target(on2);

        then_target_$1_is_one_shot_$2(target2, true);

        given_link_between_$1_and_$2(trigger1, target1);
        given_link_between_$1_and_$2(trigger2, target2);

        then_last_osc_command_was_$1(null);

        when_set_pin_$1_to_$2(0, true);
        then_last_osc_command_was_$1(on1);
        when_set_pin_$1_to_$2(0, false);
        then_last_osc_command_was_$1(off);

        when_set_pin_$1_to_$2(1, true);
        then_last_osc_command_was_$1(on2);
        when_set_pin_$1_to_$2(1, false);
        then_last_osc_command_was_$1(on2);

    }

    @Story
    public void test_osc_triggers() {

        OscCommand command = new OscCommand("127.0.0.1", 55000, "myaddress");

        Trigger trigger = given_osc_trigger(55000, "myaddress");
        Target target = given_target_on_pin_$1(0);
        given_link_between_$1_and_$2(trigger, target);

        then_pin_$1_is_$2(0, false);

        when_fire_osc_command_$1(command);

        then_pin_$1_is_$2(0, true);


    }
}
