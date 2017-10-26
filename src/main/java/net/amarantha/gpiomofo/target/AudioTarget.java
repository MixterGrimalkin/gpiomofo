package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.service.audio.AudioFile;

public class AudioTarget extends Target {

    @Parameter("filename") private String filename;
    @Parameter("loop") private boolean loop;
    @Parameter("stopOnDeactivate") private boolean stopOnDeactivate;

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
        if ( stopOnDeactivate ) {
            audioFile.onPlaybackFinished(null);
            audioFile.stop();
        }
    }

    public AudioFile getAudioFile() {
        return audioFile;
    }
}
