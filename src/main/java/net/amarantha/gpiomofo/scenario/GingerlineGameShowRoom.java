package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.http.HttpCommand;
import net.amarantha.gpiomofo.service.osc.OscCommand;
import net.amarantha.gpiomofo.target.Target;
import net.amarantha.gpiomofo.trigger.Trigger;
import net.amarantha.gpiomofo.utility.PropertyManager;

import static com.pi4j.io.gpio.PinPullResistance.PULL_DOWN;
import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static net.amarantha.gpiomofo.service.http.HttpCommand.POST;

public class GingerlineGameShowRoom extends Scenario {

    @Inject private PropertyManager props;

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

        podiumButton1 =     triggers.gpio("Podium-1", 2, PULL_UP, true);
        podiumButton2 =     triggers.gpio("Podium-2", 3, PULL_UP, true);
        podiumButton3 =     triggers.gpio("Podium-3", 4, PULL_UP, true);
        podiumButton4 =     triggers.gpio("Podium-4", 5, PULL_UP, true);

        effectButton01 =    triggers.gpio("FX1", 6, PULL_DOWN, true);
        effectButton02 =    triggers.gpio("FX2", 7, PULL_DOWN, true);
        effectButton03 =    triggers.gpio("FX3", 8, PULL_DOWN, true);
        effectButton04 =    triggers.gpio("FX4", 9, PULL_DOWN, true);
        effectButton05 =    triggers.gpio("FX5", 10, PULL_DOWN, true);
        effectButton06 =    triggers.gpio("FX6", 11, PULL_DOWN, true);
        effectButton07 =    triggers.gpio("FX7", 12, PULL_DOWN, true);
        effectButton08 =    triggers.gpio("FX8", 13, PULL_DOWN, true);
        effectButton09 =    triggers.gpio("FX9", 14, PULL_DOWN, true);
        effectButton10 =    triggers.gpio("FX10", 15, PULL_DOWN, true);
        effectButton11 =    triggers.gpio("FX11", 16, PULL_DOWN, true);
        effectButton12 =    triggers.gpio("FX12", 17, PULL_DOWN, true);
        effectButton13 =    triggers.gpio("FX13", 18, PULL_DOWN, true);
        effectButton14 =    triggers.gpio("FX14", 19, PULL_DOWN, true);
        effectButton15 =    triggers.gpio("FX15", 20, PULL_DOWN, true);
        effectButton16 =    triggers.gpio("FX16", 21, PULL_DOWN, true);

    }

    @Override
    public void setupTargets() {

        String ip = props.getString("mediaServerIP", "192.168.42.99");
        int port = props.getInt("mediaServerOscPort", 53000);

        panicTarget = targets.http(
            new HttpCommand(POST, "192.168.42.105", 8001, "gpiomofo/trigger/panic-gameshow/fire", "", "")
        );

        podiumTarget1 =     targets.osc(new OscCommand(ip, port, "cue/1101/start", 255));
        podiumTarget2 =     targets.osc(new OscCommand(ip, port, "cue/1102/start", 255));
        podiumTarget3 =     targets.osc(new OscCommand(ip, port, "cue/1103/start", 255));
        podiumTarget4 =     targets.osc(new OscCommand(ip, port, "cue/1104/start", 255));

        effectTarget01 =    targets.osc(new OscCommand(ip, port, "cue/1105/start", 255));
        effectTarget02 =    targets.osc(new OscCommand(ip, port, "cue/1106/start", 255));
        effectTarget03 =    targets.osc(new OscCommand(ip, port, "cue/1107/start", 255));
        effectTarget04 =    targets.osc(new OscCommand(ip, port, "cue/1108/start", 255));
        effectTarget05 =    targets.osc(new OscCommand(ip, port, "cue/1109/start", 255));
        effectTarget06 =    targets.osc(new OscCommand(ip, port, "cue/1110/start", 255));
        effectTarget07 =    targets.osc(new OscCommand(ip, port, "cue/1111/start", 255));
        effectTarget08 =    targets.osc(new OscCommand(ip, port, "cue/1112/start", 255));
        effectTarget09 =    targets.osc(new OscCommand(ip, port, "cue/1113/start", 255));
        effectTarget10 =    targets.osc(new OscCommand(ip, port, "cue/1114/start", 255));
        effectTarget11 =    targets.osc(new OscCommand(ip, port, "cue/1115/start", 255));
        effectTarget12 =    targets.osc(new OscCommand(ip, port, "cue/1116/start", 255));
        effectTarget13 =    targets.osc(new OscCommand(ip, port, "cue/1117/start", 255));
        effectTarget14 =    targets.osc(new OscCommand(ip, port, "cue/1118/start", 255));
        effectTarget15 =    targets.osc(new OscCommand(ip, port, "cue/1119/start", 255));
        effectTarget16 =    targets.osc(new OscCommand(ip, port, "cue/1120/start", 255));

    }

    @Override
    public void setupLinks() {

        links
            .link(panicButton,      panicTarget)

            .link(podiumButton1,    podiumTarget1)
            .link(podiumButton2,    podiumTarget2)
            .link(podiumButton3,    podiumTarget3)
            .link(podiumButton4,    podiumTarget4)
            .lock(2000, podiumTarget1, podiumTarget2, podiumTarget3, podiumTarget4)

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
