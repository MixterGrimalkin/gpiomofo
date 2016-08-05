package net.amarantha.gpiomofo;

import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import org.junit.runner.RunWith;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class TargetsTest extends TestBase {

    @Story
    public void test_multiple_targets() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target1 = given_target_on_pin_$1(1);
        Target target2 = given_target_on_pin_$1(2);
        given_link_between_$1_and_$2(trigger, target1);
        given_link_between_$1_and_$2(trigger, target2);

        then_target_$1_is_active_£2(target1, false);
        then_target_$1_is_active_£2(target2, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_£2(target1, true);
        then_target_$1_is_active_£2(target2, true);
        then_pin_$1_is_$2(1, true);
        then_pin_$1_is_$2(2, true);

        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_£2(target1, false);
        then_target_$1_is_active_£2(target2, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);

    }

    @Story
    public void test_non_following_targets() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_non_following_target_on_pin_$1(1);

        given_link_between_$1_and_$2(trigger, target);

        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_£2(target, true);
        then_pin_$1_is_$2(1, true);

        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_£2(target, true);
        then_pin_$1_is_$2(1, true);

    }

    @Story
    public void test_one_shot_targets() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_one_shot_target_on_pin_$1(1);
        given_link_between_$1_and_$2(trigger, target);

        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(1, true);

        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(1, true);

    }

    @Story
    public void test_chained_targets() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target1 = given_target_on_pin_$1(1);
        Target target2 = given_target_on_pin_$1(2);
        Target target3 = given_target_on_pin_$1(3);

        Target chainedTarget = given_chained_target(target1, target2, target3);

        given_link_between_$1_and_$2(trigger, chainedTarget);

        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, true);
        then_pin_$1_is_$2(2, true);
        then_pin_$1_is_$2(3, true);

        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

    }

    @Story
    public void test_queued_targets() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target1 = given_target_on_pin_$1(1);
        Target target2 = given_target_on_pin_$1(2);
        Target target3 = given_target_on_pin_$1(3);

        Target queuedTarget = given_a_queued_target(target1, target2, target3);
        given_link_between_$1_and_$2(trigger, queuedTarget);

        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, true);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, true);
        then_pin_$1_is_$2(3, false);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, true);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, true);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

    }

    @Story
    public void test_gpio_toggle() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_toggle_target_on_pin_$1(1);
        given_link_between_$1_and_$2(trigger, target);

        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, true);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);

        when_write_output_$1_to_$1(1, true);
        then_pin_$1_is_$2(1, true);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, false);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, true);

    }
}
