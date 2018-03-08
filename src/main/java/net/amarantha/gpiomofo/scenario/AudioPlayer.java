package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.audio.AudioFile;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.properties.entity.PropertyNotFoundException;
import net.amarantha.utils.service.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.amarantha.utils.shell.Utility.log;

@PropertyGroup("AudioPlayer")
public class AudioPlayer extends Scenario {

    @Inject private PropertiesService props;
    @Inject private WebService http;

    @Service private OscService osc;

    @Property("Port") private int port;

    private Map<String, AudioFile> audioFiles = new HashMap<>();

    @Override
    public void setup() {
        try {
            props.injectProperties(this);
            List<String> filenames = props.getStringList("AudioPlayer","Files");
            filenames.forEach((fn)->{
                String[] split = fn.split(",");
                String filename = split[0];
                AudioFile audioFile = new AudioFile("audio/"+filename+".mp3");
                if ( split.length>1 ) {
                    audioFile.setPolyphony(Integer.parseInt(split[1]));
                }
                log("Audio File '"+filename+"' available (polyphony "+audioFile.getPolyphony()+")");
                audioFiles.put(filename, audioFile);
                osc.onReceive(port, filename+"/play", (date, args) -> {
                    log("Play '"+filename+"'");
                    AudioFile af = audioFiles.get(filename);
                    af.onPlaybackFinished(null);
                    af.play();
                });
                osc.onReceive(port, filename+"/loop", (date, args) -> {
                    log("Loop '"+filename+"'");
                    AudioFile af = audioFiles.get(filename);
                    af.onPlaybackFinished(af::play);
                    af.play();
                });
                osc.onReceive(port, filename+"/stop", (date, args) -> {
                    log("Stop '"+filename+"'");
                    AudioFile af = audioFiles.get(filename);
                    af.onPlaybackFinished(null);
                    af.stop();
                });
            });
        } catch (PropertyNotFoundException e) {
            e.printStackTrace();
        }
    }

}
