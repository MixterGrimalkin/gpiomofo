package net.amarantha.gpiomofo.target;

import net.amarantha.gpiomofo.audio.AudioFile;

public class AudioTarget extends Target {

    private AudioFile audioFile;

    @Override
    protected void onActivate() {
        audioFile.play();
    }

    @Override
    protected void onDeactivate() {
        audioFile.stop();
    }

    public AudioTarget setAudioFile(String filename) {
        audioFile = new AudioFile(filename);
        return this;
    }

}
