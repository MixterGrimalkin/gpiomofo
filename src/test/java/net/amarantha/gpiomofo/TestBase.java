package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Expected;
import com.illposed.osc.OSCMessage;
import net.amarantha.gpiomofo.factory.LinkFactory;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceMock;
import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.http.HttpService;
import net.amarantha.gpiomofo.service.http.HttpServiceMock;
import net.amarantha.gpiomofo.service.midi.MidiCommand;
import net.amarantha.gpiomofo.service.midi.MidiService;
import net.amarantha.gpiomofo.service.midi.MidiServiceMock;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.service.osc.OscService;
import net.amarantha.gpiomofo.service.osc.OscServiceMock;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.target.*;
import net.amarantha.gpiomofo.trigger.GpioTrigger;
import net.amarantha.gpiomofo.trigger.RangeTrigger;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.utility.Now;
import net.amarantha.gpiomofo.webservice.TriggerResource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import static com.pi4j.io.gpio.PinPullResistance.OFF;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.testng.Assert.assertEquals;

public class TestBase {

    @Inject protected TriggerResource triggerResource;

    @Inject protected GpioService gpio;
    @Inject protected MidiService midi;
    @Inject protected HttpService http;
    @Inject protected OscService osc;

    @Inject protected Now now;
    @Inject protected TaskService tasks;

    @Inject protected TriggerFactory triggers;
    @Inject protected TargetFactory targets;
    @Inject protected LinkFactory links;

    @Before
    public void given_system() {
        ((GpioServiceMock)gpio).reset();
        ((MidiServiceMock)midi).clearLastCommand();
        ((HttpServiceMock)http).clearLastCommand();
        triggers.clearAll();
        targets.clearAll();
        midi.openDevice();
        now.setOffset(0L);
    }

    @After
    public void shutdown() {
        midi.closeDevice();
        gpio.shutdown();
    }

    ///////////
    // Given //
    ///////////

    Trigger given_trigger_on_pin_$1(int pin) {
        GpioTrigger trigger = triggers.gpio(pin, OFF, true);
        assertEquals(true, trigger.getTriggerState());
        return trigger;
    }

    Trigger given_inverted_trigger_on_pin_$1(int pin) {
        GpioTrigger trigger = triggers.gpio(pin, OFF, false);
        assertEquals(false, trigger.getTriggerState());
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
        trigger.fire(value);
    }

    Target given_target_on_pin_$1(int pin) {
        GpioTarget target = targets.gpio(pin, true);
        assertEquals(true, target.isFollowTrigger());
        assertEquals(false, target.isOneShot());
        assertEquals(true, target.getTriggerState());
        assertEquals(true, target.getOutputState().booleanValue());
        assertEquals(pin, target.getOutputPin());
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
        assertNull(target.getOutputState());
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

    //////////
    // When //
    //////////

    void when_write_output_$1_to_$1(int pin, boolean state) {
        gpio.write(pin, state);
    }

    void when_set_pin_$1_to_$2(int pin, boolean state) {
        ((GpioServiceMock)gpio).setInput(pin, state);
    }

    void when_fire_web_service_with_path_param_$1(String path) {
        triggerResource.fireTrigger(path);
    }

    void when_fire_osc_command_$1(OscCommand command) {
        ((OscServiceMock)osc).receive(command.getAddress(), new OSCMessage("/"+command.getAddress()));
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

    void then_target_$1_is_active_£2(Target target, boolean active) {
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
        Assert.assertEquals(oneShot, target.isOneShot());
    }


}
