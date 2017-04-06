package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.service.audio.AudioFile;
import net.amarantha.gpiomofo.target.Target;

public class AudioTarget extends Target {

    @Parameter("filename") private String filename;
    @Parameter("loop") private boolean loop;

    private AudioFile audioFile;

    @Override
    public void enable() {
        audioFile = new AudioFile(filename);
    }

    @Override
    protected void onActivate() {
        audioFile.play();
        if (loop) {
            audioFile.onPlaybackFinished(this::activate);
        }
    }

    @Override
    protected void onDeactivate() {
        audioFile.onPlaybackFinished(null);
        audioFile.stop();
    }

}
