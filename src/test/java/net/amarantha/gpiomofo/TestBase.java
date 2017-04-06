package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Expected;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.target.*;
import net.amarantha.gpiomofo.trigger.GpioTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceMock;
import net.amarantha.gpiomofo.service.gpio.ultrasonic.RangeTrigger;
import net.amarantha.utils.task.TaskService;
import net.amarantha.gpiomofo.webservice.TriggerResource;
import net.amarantha.utils.http.HttpService;
import net.amarantha.utils.http.HttpServiceMock;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.midi.MidiService;
import net.amarantha.utils.midi.MidiServiceMock;
import net.amarantha.utils.midi.entity.MidiCommand;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.OscServiceMock;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.service.ServiceFactory;
import net.amarantha.utils.time.Now;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;

import static com.pi4j.io.gpio.PinPullResistance.OFF;
import static net.amarantha.utils.reflection.ReflectionUtils.reflectiveGet;
import static org.junit.Assert.*;

public class TestBase {

    @Inject protected TriggerResource triggerResource;

    @Inject protected PropertiesService props;

    @Inject protected ServiceFactory services;
    @Service protected GpioService gpio;
    @Service protected MidiService midi;
    @Service protected HttpService http;
    @Service protected OscService osc;

    @Inject protected Now now;
    @Inject protected TaskService tasks;

    @Inject protected TriggerFactory triggers;
    @Inject protected TargetFactory targets;
    @Inject protected LinkFactory links;

    @Before
    public void given_system() {
        services.injectServices(this);
        ((GpioServiceMock)gpio).reset();
        ((MidiServiceMock)midi).clearLastCommand();
        ((HttpServiceMock)http).clearLastCommand();
        ((OscServiceMock)osc).clearLastCommand();
        triggers.clearAll();
        targets.clearAll();
        now.setOffset(0L);
        tasks.reset();
        services.startAll();
        props.setProperty("HttpAsyncTargets", "false");
    }

    @After
    public void shutdown() {
        services.stopAll();
    }

    ///////////
    // Given //
    ///////////

    Trigger given_trigger_on_pin_$1(int pin) {
        GpioTrigger trigger = triggers.gpio(pin, OFF, true);
        assertEquals(true, reflectiveGet(trigger, "triggerState"));
        return trigger;
    }

    Trigger given_trigger_on_pin_$1_with_hold_time_$2(int pin, int holdTime) {
        GpioTrigger trigger = triggers.gpio(pin, OFF, true);
        trigger.setHoldTime(holdTime);
        return trigger;
    }

    Trigger given_inverted_trigger_on_pin_$1(int pin) {
        GpioTrigger trigger = triggers.gpio(pin, OFF, false);
        assertEquals(false, reflectiveGet(trigger, "triggerState"));
        return trigger;
    }

    Trigger given_composed_trigger(Trigger... ts) {
        return triggers.composite(ts);
    }

    RangeTrigger given_range_trigger() {
        return triggers.range();
    }

    RangeTrigger on_target_$4_between_$2_and_$3(RangeTrigger trigger, double min, double max, Target target) {
        return trigger.addRange(min, max, target);
    }

    void when_range_trigger_$1_fires_value_$2(RangeTrigger trigger, double value) {
        trigger.fireTriggers(value);
    }

    Target given_target_on_pin_$1(int pin) {
        GpioTarget target = targets.gpio(pin, true);
        assertEquals(true, target.isFollowTrigger());
        assertEquals(false, target.isOneShot());
        assertEquals(true, target.getTriggerState());
        assertEquals(true, target.getActiveState().booleanValue());
        assertEquals(pin, target.getPinNumber());
        return target;
    }

    Target given_cancellation_target_for(Target t) {
        CancellationTarget target = targets.cancel(t);
        return target;
    }

    Target given_chained_target(Target... ts) {
        return targets.chain().add(null, ts).build();
    }

    Target given_non_following_target_on_pin_$1(int pin) {
        Target target = targets.gpio(pin, true).followTrigger(false);
        assertEquals(false, target.isFollowTrigger());
        return target;
    }

    Target given_one_shot_target_on_pin_$1(int pin) {
        Target target = targets.gpio(pin, true).oneShot(true);
        assertEquals(true, target.isOneShot());
        return target;
    }

    Target given_inverted_target_on_pin_$1(int pin) {
        Target target = targets.gpio(pin, true).triggerState(false);
        assertEquals(false, target.getTriggerState());
        return target;
    }

    Target given_target_on_pin_$1_with_clear_delay_$1(int pin, long clearDelay) {
        Target target = targets.gpio(pin, true).clearDelay(clearDelay);
        assertEquals(clearDelay, target.getClearDelay().longValue());
        return target;
    }

    Target given_non_following_target_on_pin_$1_with_clear_delay_$1(int pin, long clearDelay) {
        Target target = targets.gpio(pin, true).clearDelay(clearDelay).followTrigger(false);
        assertEquals(clearDelay, target.getClearDelay().longValue());
        assertEquals(false, target.isFollowTrigger());
        return target;
    }

    Target given_toggle_target_on_pin_$1(int pin) {
        GpioTarget target = targets.gpio(pin, null);
        assertNull(target.getActiveState());
        return target;
    }

    QueuedTarget given_a_queued_target(Target... ts) {
        QueuedTarget target = targets.queue(ts);
        assertEquals(ts.length, target.getComponentTargets().size());
        return target;
    }

    Target given_queue_reset_target(QueuedTarget queuedTarget) {
        QueueResetTarget target = targets.queueReset(queuedTarget);
        assertEquals(true, target.isOneShot());
        return target;
    }

    void given_lock_of_$1_on_targets(long lockFor, Target... ts) {
        links.lock(lockFor, ts);
    }

    Target given_midi_target(MidiCommand on) {
        return targets.midi(on);
    }

    Target given_midi_target(MidiCommand on, MidiCommand off) {
        return targets.midi(on, off);
    }

    Trigger given_http_trigger_$1(String path) {
        return triggers.http(path);
    }

    Target given_http_target(HttpCommand on) {
        return targets.http(on);
    }

    Target given_http_target(HttpCommand on, HttpCommand off) {
        return targets.http(on, off);
    }

    Trigger given_osc_trigger(int port, String address) {
        return triggers.osc(port, address);
    }

    Target given_osc_target(OscCommand on) {
        return targets.osc(on);
    }

    Target given_osc_target(OscCommand on, OscCommand off) {
        return targets.osc(on, off);
    }

    void given_link_between_$1_and_$2(Trigger trigger, Target target) {
        links.link(trigger, target);
    }

    void given_link_between_$1_and_$2(String trigger, String target) {
        links.link(trigger, target);
    }

    void given_property_$1_equals_$2(String key, String value) {
        props.setProperty(key, value);
    }

    //////////
    // When //
    //////////

    void when_write_output_$1_to_$1(int pin, boolean state) {
        gpio.write(pin, state);
    }

    void when_set_pin_$1_to_$2(int pin, boolean state) {
        ((GpioServiceMock)gpio).setInput(pin, state);
        Assert.assertEquals(state, gpio.read(pin));
        ((GpioServiceMock) gpio).scanPins();
    }

    void when_fire_web_service_with_path_param_$1(String path) {
        triggerResource.fireTrigger(path);
    }

    void when_fire_osc_command_$1(OscCommand command) {
        ((OscServiceMock)osc).receive(command.getAddress(), new ArrayList<>() );
    }

    void when_time_is_$1(String time) {
        now.setTime(time);
        tasks.scanTasks();
    }

    //////////
    // Then //
    //////////

    void then_pin_$1_is_$2(int pin, boolean state) {
        assertEquals(state, gpio.read(pin));
    }

    void then_target_$1_is_active_$2(Target target, boolean active) {
        assertEquals(active, target.isActive());
    }

    void then_there_are_$1_triggers(int count) {
        assertEquals(count, triggers.getAll().size());
    }

    void then_there_are_$1_targets(int count) {
        assertEquals(count, targets.getAll().size());
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
        assertNotNull(triggers.get(name));
    }

    void then_target_$1_is_registered(String name) {
        assertNotNull(targets.get(name));
    }

    void then_last_midi_command_was_$1(MidiCommand command) {
        assertEquals(command, ((MidiServiceMock)midi).getLastCommand());
    }

    void then_last_http_command_was_$1(HttpCommand command) {
        assertEquals(command, ((HttpServiceMock)http).getLastCommand());
    }

    void then_last_osc_command_was_$1(OscCommand command) {
        assertEquals(command, ((OscServiceMock)osc).getLastCommand());
    }

    void then_target_$1_is_one_shot_$2(Target target, boolean oneShot) {
        assertEquals(oneShot, target.isOneShot());
    }

    void then_target_$1_trigger_state_is_$2(Target target, boolean triggerState) {
        assertEquals(triggerState, target.getTriggerState());
    }

    void then_target_$1_follows_trigger_$2(Target target, boolean followTrigger) {
        assertEquals(followTrigger, target.isFollowTrigger());
    }

    void then_target_$1_clear_delay_is_$2(Target target, Long clearDelay) {
        assertEquals(clearDelay, target.getClearDelay());
    }

}
