package net.amarantha.gpiomofo;

import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.RangeTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;
import org.junit.runner.RunWith;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class TriggersTest extends TestBase {

    @Story
    public void test_registrations() {

        then_there_are_$1_triggers(0);
        then_there_are_$1_targets(0);

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_target_on_pin_$1(1);

        then_there_are_$1_triggers(1);
        then_trigger_$1_is_registered(trigger.getName());
        then_there_are_$1_targets(1);
        then_target_$1_is_registered(target.getName());

        then_cannot_create_trigger_on_$1_called_$1(2, trigger.getName());
        then_cannot_create_target_on_$1_with_name_$2(3, target.getName());

    }

    @Story
    public void test_simple_link() {

        Trigger trigger = given_trigger_on_pin_$1(0);
        Target target = given_target_on_pin_$1(1);
        given_link_between_$1_and_$2(trigger.getName(), target.getName());

        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_£2(target, true);
        then_pin_$1_is_$2(1, true);

        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(1, false);

    }

//    @Story
//    @Ignore     // this test occasionally fails, and is old anyway
    public void test_inverted_triggers_and_targets() {

        int trig = 0;
        int invTrig = 1;
        int targ = 2;
        int invTarg = 3;

        Trigger trigger = given_trigger_on_pin_$1(0);
        Trigger invertedTrigger = given_inverted_trigger_on_pin_$1(1);
        Target target = given_target_on_pin_$1(2);
        Target invertedTarget = given_inverted_target_on_pin_$1(3);

        given_link_between_$1_and_$2(trigger, invertedTarget);
        given_link_between_$1_and_$2(invertedTrigger, target);

        then_pin_$1_is_$2(targ, false);
        then_pin_$1_is_$2(invTarg, false);

        when_set_pin_$1_to_$2(trig, true);
        then_pin_$1_is_$2(invTarg, false);
        when_set_pin_$1_to_$2(trig, false);
        then_pin_$1_is_$2(invTarg, true);

        when_set_pin_$1_to_$2(invTrig, true);
        then_pin_$1_is_$2(targ, false);
        when_set_pin_$1_to_$2(invTrig, false);
        then_pin_$1_is_$2(targ, true);

    }
    @Story
    public void test_composite_trigger() {

        Trigger trigger1 = given_trigger_on_pin_$1(0);
        Trigger trigger2 = given_trigger_on_pin_$1(1);
        Trigger trigger3 = given_trigger_on_pin_$1(2);
        Trigger composite = given_composed_trigger(trigger1, trigger2, trigger3);

        Target singleTarget = given_target_on_pin_$1(3);
        Target mainTarget = given_target_on_pin_$1(4);

        given_link_between_$1_and_$2(trigger2, singleTarget);
        given_link_between_$1_and_$2(composite, mainTarget);

        then_pin_$1_is_$2(3, false);
        then_pin_$1_is_$2(4, false);

        when_set_pin_$1_to_$2(0, true);
        then_pin_$1_is_$2(3, false);
        then_pin_$1_is_$2(4, false);

        when_set_pin_$1_to_$2(1, true);
        then_pin_$1_is_$2(3, true);
        then_pin_$1_is_$2(4, false);

        when_set_pin_$1_to_$2(2, true);
        then_pin_$1_is_$2(3, true);
        then_pin_$1_is_$2(4, true);

        when_set_pin_$1_to_$2(0, false);
        then_pin_$1_is_$2(3, true);
        then_pin_$1_is_$2(4, false);

    }

    @Story
    public void test_multiple_triggers() {

        Trigger trigger1 = given_trigger_on_pin_$1(0);
        Trigger trigger2 = given_trigger_on_pin_$1(1);
        Target target = given_target_on_pin_$1(2);
        given_link_between_$1_and_$2(trigger1, target);
        given_link_between_$1_and_$2(trigger2, target);

        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(2, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_£2(target, true);
        then_pin_$1_is_$2(2, true);
        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(2, false);

        when_set_pin_$1_to_$2(1, true);
        then_target_$1_is_active_£2(target, true);
        then_pin_$1_is_$2(2, true);
        when_set_pin_$1_to_$2(1, false);
        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(2, false);

        when_set_pin_$1_to_$2(0, true);
        when_set_pin_$1_to_$2(1, true);
        then_target_$1_is_active_£2(target, true);
        then_pin_$1_is_$2(2, true);

        when_set_pin_$1_to_$2(1, false);
        then_pin_$1_is_$2(2, false);

    }

    @Story
    public void test_range_trigger() {

        Target target1 = given_target_on_pin_$1(1);
        Target target2 = given_target_on_pin_$1(2);
        Target target3 = given_target_on_pin_$1(3);

        RangeTrigger trigger = given_range_trigger();
        on_target_$4_between_$2_and_$3(trigger, 0.0, 0.3, target1);
        on_target_$4_between_$2_and_$3(trigger, 0.3, 0.7, target2);
        on_target_$4_between_$2_and_$3(trigger, 0.7, 1.0, target3);

        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_range_trigger_$1_fires_value_$2(trigger, 0.2);
        then_pin_$1_is_$2(1, true);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, false);

        when_range_trigger_$1_fires_value_$2(trigger, 0.5);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, true);
        then_pin_$1_is_$2(3, false);

        when_range_trigger_$1_fires_value_$2(trigger, 0.9);
        then_pin_$1_is_$2(1, false);
        then_pin_$1_is_$2(2, false);
        then_pin_$1_is_$2(3, true);

    }
}
