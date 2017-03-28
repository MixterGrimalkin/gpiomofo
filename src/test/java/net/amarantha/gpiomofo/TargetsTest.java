package net.amarantha.gpiomofo;

import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.core.target.QueuedTarget;
import net.amarantha.gpiomofo.core.target.Target;
import net.amarantha.gpiomofo.core.trigger.Trigger;
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

        then_target_$1_is_active_$2(target1, false);
        then_target_$1_is_active_$2(target2, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_$2(target1, true);
        then_target_$1_is_active_$2(target2, true);
        then_pin_$1_is_$2(1, true);
        then_pin_$1_is_$2(2, true);

        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_$2(target1, false);
        then_target_$1_is_active_$2(target2, false);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);

    }

    @Story
    public void test_non_following_targets() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_non_following_target_on_pin_$1(1);

        given_link_between_$1_and_$2(trigger, target);

        then_target_$1_is_active_$2(target, false);
        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_$2(target, true);
        then_pin_$1_is_$2(1, true);

        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_$2(target, true);
        then_pin_$1_is_$2(1, true);

    }

    @Story
    public void test_one_shot_targets() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_one_shot_target_on_pin_$1(1);
        given_link_between_$1_and_$2(trigger, target);

        then_target_$1_is_active_$2(target, false);
        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_$2(target, false);
        then_pin_$1_is_$2(1, true);

        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_$2(target, false);
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

        Trigger trigger1 = given_trigger_on_pin_$1(0);
        Trigger trigger2 = given_trigger_on_pin_$1(4);
        Target target1 = given_target_on_pin_$1(1);
        Target target2 = given_target_on_pin_$1(2);
        Target target3 = given_target_on_pin_$1(3);

        QueuedTarget queuedTarget = given_a_queued_target(target1, target2, target3);
        Target target4 = given_queue_reset_target(queuedTarget);
        given_link_between_$1_and_$2(trigger1, queuedTarget);
        given_link_between_$1_and_$2(trigger2, target4);

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

        when_set_pin_$1_to_$2(4, true);
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

    @Story
    public void test_clear_delay() {

        when_time_is_$1("12:00:00");

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_target_on_pin_$1_with_clear_delay_$1(1, 3000L);
        given_link_between_$1_and_$2(trigger, target);

        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, true);
        when_time_is_$1("12:00:02");
        then_pin_$1_is_$2(1, true);
        when_time_is_$1("12:00:04");
        then_pin_$1_is_$2(1, false);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);

        when_time_is_$1("12:00:00");
        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, true);
        when_time_is_$1("12:00:02");
        then_pin_$1_is_$2(1, true);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);

    }

    @Story
    public void test_clear_delay_no_follow() {

        when_time_is_$1("12:00:00");

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_non_following_target_on_pin_$1_with_clear_delay_$1(1, 3000L);
        given_link_between_$1_and_$2(trigger, target);

        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, true);
        when_time_is_$1("12:00:02");
        then_pin_$1_is_$2(1, true);
        when_time_is_$1("12:00:04");
        then_pin_$1_is_$2(1, false);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);

        when_time_is_$1("12:00:00");
        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(1, true);
        when_time_is_$1("12:00:02");
        then_pin_$1_is_$2(1, true);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, true);
        when_time_is_$1("12:00:04");
        then_pin_$1_is_$2(1, false);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(1, false);

    }

    @Story
    public void test_cancellation_targets() {

        Trigger trigger1 = given_trigger_on_pin_$1(0);
        Trigger trigger2 = given_trigger_on_pin_$1(1);
        Target target1 = given_target_on_pin_$1(2);
        Target target2 = given_cancellation_target_for(target1);
        given_link_between_$1_and_$2(trigger1, target1);
        given_link_between_$1_and_$2(trigger2, target2);

        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(2, true);
        when_set_pin_$1_to_$2(1, true);
        then_pin_$1_is_$2(2, false);

    }

    @Story
    public void test_target_lockout() {

        Trigger trigger1 = given_trigger_on_pin_$1(0);
        Trigger trigger2 = given_trigger_on_pin_$1(1);
        Target target1 = given_target_on_pin_$1(2);
        Target target2 = given_target_on_pin_$1(3);
        given_lock_of_$1_on_targets(10000, target1, target2);
        given_link_between_$1_and_$2(trigger1, target1);
        given_link_between_$1_and_$2(trigger2, target2);

        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_time_is_$1("12:00:00");
        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(2, true);
        then_pin_$1_is_$2(3, false);
        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_time_is_$1("12:00:02");
        when_set_pin_$1_to_$2(1, true);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);
        when_set_pin_$1_to_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_time_is_$1("12:00:15");
        when_set_pin_$1_to_$2(1, true);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, true);
        when_set_pin_$1_to_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

    }

}
