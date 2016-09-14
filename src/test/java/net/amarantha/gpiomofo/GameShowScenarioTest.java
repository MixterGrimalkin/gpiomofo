package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.scenario.GingerlineGameShowRoom;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import org.junit.runner.RunWith;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class GameShowScenarioTest extends TestBase {

    @Inject private GingerlineGameShowRoom scenario;

    @Story
    public void test_podium_buttons() {

        given_properties();

        given_scenario(scenario);

        when_set_pin_$1_to_$2(0, true);
        then_last_osc_command_was_$1(null);
        when_set_pin_$1_to_$2(0, false);
        then_last_osc_command_was_$1(new OscCommand(lightingIp, lightingPort, "alarm/c1", 255));

    }

    private String lightingIp = "127.0.0.1";
    private int lightingPort = 7700;

    private void given_properties() {
        given_property_$1_equals_$2("ButtonHoldTime", "100");
        given_property_$1_equals_$2("LightingServerIP", lightingIp);
        given_property_$1_equals_$2("LightingServerOscPort", lightingPort+"");
        given_property_$1_equals_$2("MediaServerIP", "127.0.0.1");
        given_property_$1_equals_$2("MediaServerOscPort", "1000");
    }
}
