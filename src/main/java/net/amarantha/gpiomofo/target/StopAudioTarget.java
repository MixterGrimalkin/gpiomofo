package net.amarantha.gpiomofo.target;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.factory.TargetFactory;

public class StopAudioTarget extends Target {

    @Inject private TargetFactory targets;

    @Parameter("target") private String audioTarget;

    @Override
    protected void onActivate() {
        ((AudioTarget)targets.get(audioTarget)).getAudioFile().stop();
    }

    @Override
    protected void onDeactivate() {

    }
}
