package net.amarantha.gpiomofo.service.audio;

import net.amarantha.gpiomofo.target.Target;

public class AudioTarget extends Target {

    private AudioFile audioFile;

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

    public AudioTarget setAudioFile(String filename) {
        audioFile = new AudioFile(filename);
        return this;
    }

    private boolean loop;

    public AudioTarget loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public AudioFile getAudioFile() {
        return audioFile;
    }

    public boolean isLoop() {
        return loop;
    }
}
