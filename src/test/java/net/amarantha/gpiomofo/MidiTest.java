package net.amarantha.gpiomofo;

import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.core.target.Target;
import net.amarantha.gpiomofo.core.trigger.Trigger;
import net.amarantha.utils.midi.MidiCommand;
import org.junit.runner.RunWith;

import static javax.sound.midi.ShortMessage.*;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class MidiTest extends TestBase {

    @Story
    public void test_midi_target() {

        MidiCommand on1 = new MidiCommand(NOTE_ON, 1, 64, 127);
        MidiCommand off1 = new MidiCommand(NOTE_OFF, 1, 64, 127);
        MidiCommand on2 = new MidiCommand(CONTROL_CHANGE, 1, 64, 127);

        Trigger trigger1 = given_trigger_on_pin_$1(0);
        Trigger trigger2 = given_trigger_on_pin_$1(1);
        Target target1 = given_midi_target(on1, off1);
        Target target2 = given_midi_target(on2);
        given_link_between_$1_and_$2(trigger1, target1);
        given_link_between_$1_and_$2(trigger2, target2);

        then_last_midi_command_was_$1(null);

        when_set_pin_$1_to_$2(0, true);
        then_last_midi_command_was_$1(on1);

        when_set_pin_$1_to_$2(0, false);
        then_last_midi_command_was_$1(off1);

        when_set_pin_$1_to_$2(1, true);
        then_last_midi_command_was_$1(on2);

        when_set_pin_$1_to_$2(1, false);
        then_last_midi_command_was_$1(on2);

    }

}
