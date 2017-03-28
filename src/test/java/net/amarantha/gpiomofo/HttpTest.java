package net.amarantha.gpiomofo;

import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.core.target.Target;
import net.amarantha.gpiomofo.core.trigger.Trigger;
import org.junit.runner.RunWith;

import static net.amarantha.gpiomofo.service.http.HttpCommand.GET;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class HttpTest extends TestBase {

    @Story
    public void test_http_targets() {

        HttpCommand get = new HttpCommand(GET, "127.0.0.1", 8001, "base", "path", "");
        HttpCommand post1 = get.withMethod(POST).withPort(8002).withBasePath("base/thing");
        HttpCommand post2 = get.withMethod(POST).withPath("otherthing").withPayload("more data");

        Trigger trigger1 = given_trigger_on_pin_$1(0);
        Trigger trigger2 = given_trigger_on_pin_$1(1);

        Target http1 = given_http_target(get, post1);
        Target http2 = given_http_target(post2);
        then_target_$1_is_one_shot_$2(http1, false);
        then_target_$1_is_one_shot_$2(http2, true);

        given_link_between_$1_and_$2(trigger1, http1);
        given_link_between_$1_and_$2(trigger2, http2);
        then_last_http_command_was_$1(null);

        when_set_pin_$1_to_$2(0, true);
        then_last_http_command_was_$1(get);
        when_set_pin_$1_to_$2(0, false);
        then_last_http_command_was_$1(post1);

        when_set_pin_$1_to_$2(1, true);
        then_last_http_command_was_$1(post2);
        when_set_pin_$1_to_$2(1, false);
        then_last_http_command_was_$1(post2);

    }

    @Story
    public void test_http_triggers() {

        Trigger trigger = given_http_trigger_$1("web-trigger");
        Target target = given_target_on_pin_$1(0);
        given_link_between_$1_and_$2(trigger, target);

        then_pin_$1_is_$2(0, false);

        when_fire_web_service_with_path_param_$1("web-trigger");

        then_pin_$1_is_$2(0, true);

    }


}
