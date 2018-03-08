package net.amarantha.gpiomofo.service.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.net.URL;

public class AudioFile extends PlaybackListener implements Runnable {

    private String filename;
    private AdvancedPlayer player;
    private Thread playerThread;

    private boolean playing = false;

    private int polyphony = 10;
    private int voicesOn = 0;

    public AudioFile setPolyphony(int polyphony) {
        this.polyphony = polyphony;
        return this;
    }

    public int getPolyphony() {
        return polyphony;
    }

    public AudioFile(String filename) {
        this.filename = filename;
    }

    public void play() {
        if ( voicesOn < polyphony ) {
            try {
                System.out.println("Playing " + filename);
                String urlAsString = "file:///" + new java.io.File(".").getCanonicalPath() + "/" + this.filename;

                player = new AdvancedPlayer(new URL(urlAsString).openStream(), FactoryRegistry.systemRegistry().createAudioDevice());
                player.setPlayBackListener(this);

                playerThread = new Thread(this);
                playerThread.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stop() {
        if ( playing ) {
            player.stop();
            playerThread = null;
        }
    }

    public void playbackStarted(PlaybackEvent playbackEvent) {
        playing = true;
    }

    public void playbackFinished(PlaybackEvent playbackEvent) {
        playing = false;
    }

    private PlaybackFinishedCallback callback;

    public void onPlaybackFinished(PlaybackFinishedCallback callback) {
        this.callback = callback;
    }

    public void run() {
        try {
            voicesOn++;
            this.player.play();
            voicesOn--;
            if ( callback!=null ) {
                callback.onPlaybackFinished();
            }
        } catch (JavaLayerException ex) {
            ex.printStackTrace();
        }
    }

    public interface PlaybackFinishedCallback {
        void onPlaybackFinished();
    }

    public String getFilename() {
        return filename;
    }
}

