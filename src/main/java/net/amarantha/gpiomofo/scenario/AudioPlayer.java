package net.amarantha.gpiomofo.scenario;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.audio.AudioFile;
import net.amarantha.gpiomofo.webservice.WebService;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.entity.OscListener;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.properties.entity.PropertyNotFoundException;
import net.amarantha.utils.service.Service;
import net.amarantha.utils.shell.Utility;

import java.util.Date;
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
            filenames.forEach((filename)->{
                log("Audio File '"+filename+"' available");
                audioFiles.put(filename, new AudioFile("audio/"+filename+".mp3"));
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
