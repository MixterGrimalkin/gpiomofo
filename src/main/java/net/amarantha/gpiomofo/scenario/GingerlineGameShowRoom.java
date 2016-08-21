package net.amarantha.gpiomofo.scenario;

import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static net.amarantha.gpiomofo.scenario.GingerlineSetup.*;

public class GingerlineGameShowRoom extends Scenario {

    private Trigger panicButton;
    private Trigger podiumButton1;
    private Trigger podiumButton2;
    private Trigger podiumButton3;
    private Trigger podiumButton4;
    private Trigger effectButton01;
    private Trigger effectButton02;
    private Trigger effectButton03;
    private Trigger effectButton04;
    private Trigger effectButton05;
    private Trigger effectButton06;
    private Trigger effectButton07;
    private Trigger effectButton08;
    private Trigger effectButton09;
    private Trigger effectButton10;
    private Trigger effectButton11;
    private Trigger effectButton12;
    private Trigger effectButton13;
    private Trigger effectButton14;
    private Trigger effectButton15;
    private Trigger effectButton16;

    private Target panicTarget;
    private Target podiumTarget1;
    private Target podiumTarget2;
    private Target podiumTarget3;
    private Target podiumTarget4;
    private Target effectTarget01;
    private Target effectTarget02;
    private Target effectTarget03;
    private Target effectTarget04;
    private Target effectTarget05;
    private Target effectTarget06;
    private Target effectTarget07;
    private Target effectTarget08;
    private Target effectTarget09;
    private Target effectTarget10;
    private Target effectTarget11;
    private Target effectTarget12;
    private Target effectTarget13;
    private Target effectTarget14;
    private Target effectTarget15;
    private Target effectTarget16;

    @Override
    public void setupTriggers() {

        panicButton =       triggers.gpio("Panic", 0, PULL_DOWN, true);

        podiumButton1 =     triggers.gpio(1, PULL_DOWN, true);
        podiumButton2 =     triggers.gpio(2, PULL_DOWN, true);
        podiumButton3 =     triggers.gpio(3, PULL_DOWN, true);
        podiumButton4 =     triggers.gpio(4, PULL_DOWN, true);

        effectButton01 =    triggers.gpio(5, PULL_DOWN, true);
        effectButton02 =    triggers.gpio(6, PULL_DOWN, true);
        effectButton03 =    triggers.gpio(7, PULL_DOWN, true);
        effectButton04 =    triggers.gpio(8, PULL_DOWN, true);
        effectButton05 =    triggers.gpio(9, PULL_DOWN, true);
        effectButton06 =    triggers.gpio(10, PULL_DOWN, true);
        effectButton07 =    triggers.gpio(11, PULL_DOWN, true);
        effectButton08 =    triggers.gpio(12, PULL_DOWN, true);
        effectButton09 =    triggers.gpio(13, PULL_DOWN, true);
        effectButton10 =    triggers.gpio(14, PULL_DOWN, true);
        effectButton11 =    triggers.gpio(15, PULL_DOWN, true);
        effectButton12 =    triggers.gpio(16, PULL_DOWN, true);
        effectButton13 =    triggers.gpio(17, PULL_DOWN, true);
        effectButton14 =    triggers.gpio(18, PULL_DOWN, true);
        effectButton15 =    triggers.gpio(19, PULL_DOWN, true);
        effectButton16 =    triggers.gpio(20, PULL_DOWN, true);

    }

    @Override
    public void setupTargets() {

        panicTarget =       targets.osc("Panic", new OscCommand(PANIC_IP, PANIC_OSC_PORT, PANIC_GAME_SHOW));

        podiumTarget1 =     targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_podium1"));
        podiumTarget2 =     targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_podium2"));
        podiumTarget3 =     targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_podium3"));
        podiumTarget4 =     targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_podium4"));

        effectTarget01 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect01"));
        effectTarget02 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect02"));
        effectTarget03 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect03"));
        effectTarget04 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect04"));
        effectTarget05 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect05"));
        effectTarget06 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect06"));
        effectTarget07 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect07"));
        effectTarget08 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect08"));
        effectTarget09 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect09"));
        effectTarget10 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect10"));
        effectTarget11 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect11"));
        effectTarget12 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect12"));
        effectTarget13 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect13"));
        effectTarget14 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect14"));
        effectTarget15 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect15"));
        effectTarget16 =    targets.osc(new OscCommand(MEDIA_SERVER_IP, MEDIA_SERVER_OSC_PORT, "game_show_effect16"));

    }

    @Override
    public void setupLinks() {

        links
            .link(panicButton,      panicTarget)

            .link(podiumButton1,    podiumTarget1)
            .link(podiumButton2,    podiumTarget2)
            .link(podiumButton3,    podiumTarget3)
            .link(podiumButton4,    podiumTarget4)
            .lock(5000, podiumTarget1, podiumTarget2, podiumTarget3, podiumTarget4)

            .link(effectButton01,   effectTarget01)
            .link(effectButton02,   effectTarget02)
            .link(effectButton03,   effectTarget03)
            .link(effectButton04,   effectTarget04)
            .link(effectButton05,   effectTarget05)
            .link(effectButton06,   effectTarget06)
            .link(effectButton07,   effectTarget07)
            .link(effectButton08,   effectTarget08)
            .link(effectButton09,   effectTarget09)
            .link(effectButton10,   effectTarget10)
            .link(effectButton11,   effectTarget11)
            .link(effectButton12,   effectTarget12)
            .link(effectButton13,   effectTarget13)
            .link(effectButton14,   effectTarget14)
            .link(effectButton15,   effectTarget15)
            .link(effectButton16,   effectTarget16)
        ;

    }

}
