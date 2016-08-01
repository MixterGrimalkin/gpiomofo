package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Expected;
import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.gpio.GpioService;
import net.amarantha.gpiomofo.gpio.GpioServiceMock;
import net.amarantha.gpiomofo.link.LinkFactory;
import net.amarantha.gpiomofo.target.GpioTarget;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.target.TargetFactory;
import net.amarantha.gpiomofo.trigger.GpioTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.trigger.TriggerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import static com.pi4j.io.gpio.PinPullResistance.OFF;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.testng.Assert.assertEquals;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class LinkTest {

    @Inject private GpioService gpio;
    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;
    @Inject private LinkFactory links;

    @Before
    public void given_system() {
        ((GpioServiceMock)gpio).reset();
        triggers.clearAll();
        targets.clearAll();
    }

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
        given_link_between_$1_and_$2(trigger, target);

        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(1, false);

        when_set_pin_$1_to_$2(0, true);
        then_target_$1_is_active_£2(target, true);
        then_pin_$1_is_$2(1, true);

        when_set_pin_$1_to_$2(0, false);
        then_target_$1_is_active_£2(target, false);
        then_pin_$1_is_$2(1, false);

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
        then_pin_$1_is_$2(1, false);

    }

    @Story
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



    ///////////
    // Given //
    ///////////

    Trigger given_trigger_on_pin_$1(int pin) {
        GpioTrigger trigger = triggers.gpio("Trigger"+pin, pin, OFF, true);
        assertEquals(true, trigger.getTriggerState());
        return trigger;
    }

    Trigger given_inverted_trigger_on_pin_$1(int pin) {
        GpioTrigger trigger = triggers.gpio("InvertedTrigger"+pin, pin, OFF, false);
        assertEquals(false, trigger.getTriggerState());
        return trigger;
    }

    Target given_target_on_pin_$1(int pin) {
        GpioTarget target = targets.gpio("Target"+pin, pin, true);
        assertEquals(true, target.isFollowTrigger());
        assertEquals(false, target.isOneShot());
        assertEquals(true, target.getTriggerState());
        assertEquals(true, target.getOutputState().booleanValue());
        assertEquals(pin, target.getOutputPin());
        return target;
    }

    Target given_non_following_target_on_pin_$1(int pin) {
        Target target = targets.gpio("NonFollowingTarget"+pin, pin, true).followTrigger(false);
        assertEquals(false, target.isFollowTrigger());
        return target;
    }

    Target given_one_shot_target_on_pin_$1(int pin) {
        Target target = targets.gpio("OneShotTarget"+pin, pin, true).oneShot(true);
        assertEquals(true, target.isOneShot());
        return target;
    }

    Target given_inverted_target_on_pin_$1(int pin) {
        Target target = targets.gpio("InvertedTarget"+pin, pin, true).triggerState(false);
        assertEquals(false, target.getTriggerState());
        return target;
    }

    Target given_toggle_target_on_pin_$1(int pin) {
        GpioTarget target = targets.gpio("InvertedTarget"+pin, pin, null);
        assertNull(target.getOutputState());
        return target;
    }

    void given_link_between_$1_and_$2(Trigger trigger, Target target) {
        links.link(trigger, target);
    }

    //////////
    // When //
    //////////

    void when_write_output_$1_to_$1(int pin, boolean state) {
        gpio.write(pin, state);
    }

    void when_set_pin_$1_to_$2(int pin, boolean state) {
        ((GpioServiceMock)gpio).setInput(pin, state);
    }

    void when_scan_pins() {
        ((GpioServiceMock)gpio).scanPins();
    }

    //////////
    // Then //
    //////////

    void then_pin_$1_is_$2(int pin, boolean state) {
        assertEquals(state, gpio.read(pin));
    }

    void then_target_$1_is_active_£2(Target target, boolean active) {
        assertEquals(active, target.isActive());
    }

    void then_there_are_$1_triggers(int count) {
        assertEquals(count, triggers.getAllTriggers().size());
    }

    void then_there_are_$1_targets(int count) {
        assertEquals(count, targets.getAllTargets().size());
    }

    @Expected(IllegalStateException.class)
    void then_cannot_create_trigger_on_$1_called_$1(int pin, String name) {
        triggers.gpio(name, pin, OFF, true);
    }

    @Expected(IllegalStateException.class)
    void then_cannot_create_target_on_$1_with_name_$2(int pin, String name) {
        targets.gpio(name, pin, true);
    }

    void then_trigger_$1_is_registered(String name) {
        assertNotNull(triggers.getTrigger(name));
    }

    void then_target_$1_is_registered(String name) {
        assertNotNull(targets.getTarget(name));
    }

}
