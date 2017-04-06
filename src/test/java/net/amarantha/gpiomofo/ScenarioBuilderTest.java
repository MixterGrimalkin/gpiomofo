package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Expected;
import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.factory.TargetFactory;
import net.amarantha.gpiomofo.factory.TriggerFactory;
import net.amarantha.gpiomofo.scenario.Scenario;
import net.amarantha.gpiomofo.factory.ScenarioBuilder;
import net.amarantha.gpiomofo.service.audio.AudioFile;
import net.amarantha.gpiomofo.target.*;
import net.amarantha.gpiomofo.trigger.*;
import net.amarantha.gpiomofo.scenario.ExampleScenario;
import net.amarantha.gpiomofo.target.AudioTarget;
import net.amarantha.gpiomofo.target.ShellTarget;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.http.entity.Param;
import net.amarantha.utils.midi.entity.MidiCommand;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.properties.PropertiesService;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.sound.midi.ShortMessage;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static net.amarantha.utils.reflection.ReflectionUtils.reflectiveGet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class ScenarioBuilderTest extends TestBase {

    @Inject private ScenarioBuilder builder;
    @Inject private PropertiesService props;

    @Inject private TriggerFactory triggers;
    @Inject private TargetFactory targets;

    @Story
    public void testYamlOnly() {

        given_scenario_$1("YamlOnlyScenario");

        then_scenario_is_a_$1_called_$2(Scenario.class, "YamlOnlyScenario");

        then_trigger_$1_does_not_exist("DoesNotExist");

        // Triggers

        Trigger firstTrigger =
        then_trigger_$1_is_a_$2("FirstTrigger", GpioTrigger.class);
        then_gpio_trigger_$1_is_on_pin_$2_resistance_$3_trigger_state_$4(firstTrigger, 1, PULL_DOWN, true);
        then_trigger_$1_hold_time_is_$2(firstTrigger, 0);

        Trigger secondTrigger =
        then_trigger_$1_is_a_$2("SecondTrigger", GpioTrigger.class);
        then_trigger_$1_hold_time_is_$2(secondTrigger, 1540);

        then_trigger_$1_is_a_$2("ThirdTrigger", HttpTrigger.class);

        Trigger fourthTrigger =
        then_trigger_$1_is_a_$2("FourthTrigger", OscTrigger.class);
        then_osc_trigger_$1_on_port_$2_address_$3(fourthTrigger, 55, "testOsc");

        Trigger compTrigger =
        then_trigger_$1_is_a_$2("CompTrig", CompositeTrigger.class);

        Trigger compTriggerHold =
        then_trigger_$1_is_a_$2("CompTrigHold", CompositeTrigger.class);
        then_trigger_$1_hold_time_is_$2(compTriggerHold, 500);

        // Targets

        Target firstTarget =
        then_target_$1_is_a_$2("FirstTarget", GpioTarget.class);
        then_gpio_target_$1_is_on_pin_$2_activating_on_$3(firstTarget, 0, true);
        then_target_$1_is_one_shot_$2(firstTarget, false);
        then_target_$1_trigger_state_is_$2(firstTarget, true);
        then_target_$1_follows_trigger_$2(firstTarget, true);
        then_target_$1_clear_delay_is_$2(firstTarget, null);

        Target secondTarget =
        then_target_$1_is_a_$2("SecondTarget", GpioTarget.class);
        then_gpio_target_$1_is_on_pin_$2_activating_on_$3(secondTarget, 3, false);

        Target thirdTarget =
        then_target_$1_is_a_$2("ThirdTarget", AudioTarget.class);
        then_audio_target_$1_plays_file_$2_loop_$3(thirdTarget, "SnootyBobs-MisterBastard.mp3", true);

        Target fourthTarget =
        then_target_$1_is_a_$2("FourthTarget", HttpTarget.class);
        then_http_target_$1_has_on_$2_and_off_$3(fourthTarget,
                new HttpCommand("GET", "host.com", 9000, "resource", "", "", new Param("key", "value"), new Param("one", "two")),
                new HttpCommand("POST", "host.org", 5000, "path", "to", "nicebody", new Param("thing", "thong"))
        );
        then_target_$1_is_one_shot_$2(fourthTarget, true);
        then_target_$1_trigger_state_is_$2(fourthTarget, false);
        then_target_$1_follows_trigger_$2(fourthTarget, false);
        then_target_$1_clear_delay_is_$2(fourthTarget, 100L);

        Target fifthTarget =
        then_target_$1_is_a_$2("FifthTarget", MidiTarget.class);
        then_midi_target_$1_has_on_$2_and_off_$3(fifthTarget,
                new MidiCommand(ShortMessage.NOTE_ON, 5, 64, 100),
                new MidiCommand(ShortMessage.CONTROL_CHANGE, 16, 64, 0)
        );

        Target sixthTarget =
        then_target_$1_is_a_$2("SixthTarget", OscTarget.class);
        then_osc_target_$1_has_on_$2_and_off_$3(sixthTarget,
                new OscCommand("host.com", 1234, "path/to"),
                new OscCommand("host.org", 4321, "resource", "127", "0", "0")
        );

        Target seventhTarget =
        then_target_$1_is_a_$2("SeventhTarget", ShellTarget.class);
        then_shell_target_$1_runs_command_$2(seventhTarget, "./some-script.sh");

        // Links

        then_pin_$1_is_$2(0, false);
        then_pin_$1_is_$2(3, true);
        when_set_pin_$1_to_$2(1, true);
        then_pin_$1_is_$2(0, true);
        then_pin_$1_is_$2(3, false);

        // Composite Triggers

        then_pin_$1_is_$2(12, false);
        when_set_pin_$1_to_$2(10, true);
        when_set_pin_$1_to_$2(11, false);
        then_pin_$1_is_$2(12, false);
        when_set_pin_$1_to_$2(10, false);
        when_set_pin_$1_to_$2(11, true);
        then_pin_$1_is_$2(12, false);
        when_set_pin_$1_to_$2(10, true);
        then_pin_$1_is_$2(12, true);

    }


    @Story
    public void testYamlAndClass() {

        given_scenario_$1("ExampleScenario");
        ExampleScenario scenario = (ExampleScenario)then_scenario_is_a_$1_called_$2(ExampleScenario.class, "ExampleScenario");

        then_gpio_trigger_$1_is_on_pin_$2_resistance_$3_trigger_state_$4(scenario.testTrigger, 0, PULL_DOWN, true);
        then_gpio_target_$1_is_on_pin_$2_activating_on_$3(scenario.testTarget, 1, true);

        then_field_$2_has_value_$3(scenario, "colour", RGB.RED);
        then_field_$2_has_value_$3(scenario, "style", "Thong");

        then_pin_$1_is_$2(1, false);
        when_set_pin_$1_to_$2(0, true);

        then_pin_$1_is_$2(1, true);

    }

    void given_scenario_$1(String name) {
        props.setProperty("ScenariosDirectory", "test-scenarios");
        props.setProperty("Scenario", name);
        builder.loadScenario();
    }

    Scenario then_scenario_is_a_$1_called_$2(Class<? extends Scenario> clazz, String name) {
        Scenario scenario = builder.getScenario();
        assertNotNull(scenario);
        assertEquals(clazz, scenario.getClass());
        assertEquals(name, scenario.getName());
        return scenario;
    }

    @Expected(IllegalStateException.class)
    void then_trigger_$1_does_not_exist(String name) {
        triggers.get(name);
    }

    Trigger then_trigger_$1_is_a_$2(String name, Class<? extends Trigger> clazz) {
        Trigger trigger = triggers.get(name);
        Assert.assertEquals(clazz, trigger.getClass());
        return trigger;
    }

    void then_gpio_trigger_$1_is_on_pin_$2_resistance_$3_trigger_state_$4(Trigger trigger, int pin, PinPullResistance resistance, boolean state) {
        GpioTrigger gpioTrigger = (GpioTrigger)trigger;
        assertEquals(pin, ((Integer)reflectiveGet(gpioTrigger, "pinNumber")).intValue());
        assertEquals(resistance, reflectiveGet(gpioTrigger, "resistance"));
        assertEquals(state, reflectiveGet(gpioTrigger, "triggerState"));
    }

    Target then_target_$1_is_a_$2(String name, Class<? extends Target> clazz) {
        Target target = targets.get(name);
        Assert.assertEquals(clazz, target.getClass());
        return target;
    }

    void then_gpio_target_$1_is_on_pin_$2_activating_on_$3(Target target, int pin, Boolean state) {
        GpioTarget gpioTarget = (GpioTarget)target;
        assertEquals(pin, gpioTarget.getPinNumber());
        assertEquals(state, gpioTarget.getActiveState());
    }

    void then_trigger_$1_hold_time_is_$2(Trigger trigger, int holdTime) {
        Object ht = reflectiveGet(trigger, "holdTime");
        int value = ht==null ? 0 : (Integer) ht;
        assertEquals(holdTime, value);
    }

    void then_osc_trigger_$1_on_port_$2_address_$3(Trigger trigger, int port, String address) {
        OscTrigger oscTrigger = (OscTrigger)trigger;
        assertEquals(port, ((Integer)reflectiveGet(oscTrigger, "port")).intValue());
        assertEquals(address, reflectiveGet(oscTrigger, "address"));
    }

    void then_audio_target_$1_plays_file_$2_loop_$3(Target target, String filename, boolean loop) {
        AudioTarget audioTarget = (AudioTarget)target;
        assertEquals(filename, ((AudioFile)reflectiveGet(audioTarget, "audioFile")).getFilename());
        assertEquals(loop, reflectiveGet(audioTarget, "loop"));
    }

    void then_http_target_$1_has_on_$2_and_off_$3(Target target, HttpCommand on, HttpCommand off) {
        HttpTarget httpTarget = (HttpTarget)target;
        assertEquals(on, reflectiveGet(httpTarget, "onCommand"));
        assertEquals(off, reflectiveGet(httpTarget, "offCommand"));
    }

    void then_midi_target_$1_has_on_$2_and_off_$3(Target target, MidiCommand on, MidiCommand off) {
        MidiTarget midiTarget = (MidiTarget)target;
        assertEquals(on, reflectiveGet(midiTarget, "onCommand"));
        assertEquals(off, reflectiveGet(midiTarget, "offCommand"));
    }

    void then_osc_target_$1_has_on_$2_and_off_$3(Target target, OscCommand on, OscCommand off) {
        OscTarget oscTarget = (OscTarget)target;
        assertEquals(on, reflectiveGet(oscTarget, "onCommand"));
        assertEquals(off, reflectiveGet(oscTarget, "offCommand"));
    }

    void then_shell_target_$1_runs_command_$2(Target target, String filename) {
        ShellTarget shellTarget = (ShellTarget)target;
        assertEquals(filename, reflectiveGet(shellTarget, "command"));
    }

    void then_field_$2_has_value_$3(Object object, String fieldName, Object expected) {
        assertEquals(expected, reflectiveGet(object, fieldName));
    }

}
