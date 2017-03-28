package net.amarantha.gpiomofo;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Expected;
import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.gpio.GpioServiceMock;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.Map.Entry;

import static org.junit.Assert.assertEquals;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class GpioTest {

    @Inject private GpioService gpio;

    @Before
    public void given_gpio_system() {
        ((GpioServiceMock)gpio).reset();
        listenerMessage = null;
    }

    @Story
    public void test_shutdown() {
        given_digital_output_pin_$1(0);
        given_digital_output_pin_$1(1);
        given_digital_output_pin_$1(2);
        given_digital_output_pin_$1(3);
        when_set_output_$1_to_$2(0, true);
        when_set_output_$1_to_$2(1, true);
        when_set_output_$1_to_$2(2, true);
        when_set_output_$1_to_$2(3, true);
        then_all_outputs_are_$1(true);
        when_shutdown();
        then_all_outputs_are_$1(false);

    }

    @Story
    public void pin_setup_test() {
        then_cannot_create_input_$1(-1);
        then_cannot_create_output_$1(-1);
        given_digital_input_pin_$1(0);
        then_cannot_create_input_$1(0);
        then_cannot_create_output_$1(0);
        given_digital_output_pin_$1(1);
        then_cannot_create_input_$1(1);
        then_cannot_create_output_$1(1);
        then_cannot_listen_on_$1(1);
    }

    @Story
    public void basic_Io_test() {
        given_digital_output_pin_$1(0);
        then_output_$1_is_$2(0, false);
        when_set_output_$1_to_$2(0, true);
        then_output_$1_is_$2(0, true);
        when_set_output_$1_to_$2(0, false);
        then_output_$1_is_$2(0, false);

        given_digital_input_pin_$1(1);
        then_cannot_write_pin_$1(1);
        when_input_$1_is_set_to_$2(1, false);
        then_input_$1_is_$2(1, false);
        when_input_$1_is_set_to_$2(1, true);
        then_input_$1_is_$2(1, true);
    }

    @Story
    public void listener_test() {
        // State Change
        given_digital_input_pin_$1(0);
        given_on_high_listener_on_input_$1(0, TEST_1);
        given_on_low_listener_on_input_$1(0, TEST_2);
        then_listener_message_is_correct(null);
        when_input_$1_is_set_to_$2(0, true);
        then_listener_message_is_correct(TEST_1);
        when_input_$1_is_set_to_$2(0, false);
        then_listener_message_is_correct(TEST_2);
        // Continuous
        given_digital_input_pin_$1(1);
        given_while_low_listener_on_input_$1(1, TEST_3);
        given_while_high_listener_on_input_$1(1, TEST_4);
        when_input_$1_is_set_to_$2(1, false);
        then_listener_message_is_correct(TEST_3);
        when_input_$1_is_set_to_$2(1, true);
        then_listener_message_is_correct(TEST_4);
    }

    @Story
    public void multi_listener_test() {
        given_digital_input_pin_$1(0);
        given_digital_output_pin_$1(1);
        given_digital_output_pin_$1(2);
        given_change_listener_from_input_$1_to_output_$2(0, 1);
        given_change_listener_from_input_$1_to_output_$2(0, 2);
        when_input_$1_is_set_to_$2(0, true);
        then_output_$1_is_$2(1, true);
        then_output_$1_is_$2(2, true);
        when_input_$1_is_set_to_$2(0, false);
        then_output_$1_is_$2(1, false);
        then_output_$1_is_$2(2, false);
    }

    private String listenerMessage = null;

    private static final String TEST_1 = "Bill Hicks";
    private static final String TEST_2 = "Noam Chomsky";
    private static final String TEST_3 = "George Carlin";
    private static final String TEST_4 = "Amy Goodman";

    ///////////
    // Given //
    ///////////

    void given_digital_input_pin_$1(int pin) {
        gpio.setupDigitalInput(pin);
    }

    void given_digital_output_pin_$1(int pin) {
        gpio.setupDigitalOutput(pin);
    }

    void given_change_listener_from_input_$1_to_output_$2(int inputPin, int outputPin) {
        gpio.onInputChange(inputPin, (s)->gpio.write(outputPin, s));
    }

    void given_on_high_listener_on_input_$1(int pin, String message) {
        gpio.onInputHigh(pin, ()->listenerMessage=message);
    }

    void given_on_low_listener_on_input_$1(int pin, String message) {
        gpio.onInputLow(pin, ()->listenerMessage=message);
    }

    void given_while_high_listener_on_input_$1(int pin, String message) {
        gpio.whileInputHigh(pin, ()->listenerMessage=message);
    }

    void given_while_low_listener_on_input_$1(int pin, String message) {
        gpio.whileInputLow(pin, ()->listenerMessage=message);
    }

    //////////
    // When //
    //////////

    void when_input_$1_is_set_to_$2(int pin, boolean state) {
        ((GpioServiceMock)gpio).setInput(pin, state);
        ((GpioServiceMock)gpio).scanPins();
    }

    void when_set_output_$1_to_$2(int pin, boolean state) {
        gpio.write(pin, state);
    }

    void when_shutdown() {
        gpio.stop();
    }

    //////////
    // Then //
    //////////

    @Expected(IllegalStateException.class)
    void then_cannot_create_input_$1(int pin) {
        gpio.setupDigitalInput(pin);
    }

    @Expected(IllegalStateException.class)
    void then_cannot_create_output_$1(int pin) {
        gpio.setupDigitalOutput(pin);
    }

    @Expected(IllegalStateException.class)
    void then_cannot_listen_on_$1(int pin) {
        gpio.onInputHigh(pin, null);
    }

    @Expected(IllegalStateException.class)
    void then_cannot_write_pin_$1(int pin) {
        gpio.write(pin, true);
    }

    void then_input_$1_is_$2(int pin, boolean state) {
        assertEquals(state, gpio.read(pin));
    }

    void then_output_$1_is_$2(int pin, boolean state) {
        assertEquals(state, ((GpioServiceMock)gpio).getOutput(pin));
    }

    void then_listener_message_is_correct(String message) {
        assertEquals(message, listenerMessage);
    }

    void then_all_outputs_are_$1(boolean state) {
        for ( Entry<Integer, Boolean> entry : ((GpioServiceMock)gpio).getOutputStates().entrySet() ) {
            assertEquals(state, entry.getValue());
        }
    }

}
