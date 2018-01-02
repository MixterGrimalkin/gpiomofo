package net.amarantha.gpiomofo.service;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.service.AbstractService;
import net.amarantha.utils.string.StringMap;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Map;
import java.util.function.Consumer;

import static com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.getKeyStorePasswordPair;
import static net.amarantha.utils.shell.Utility.log;

@PropertyGroup("Aws")
public class AwsService extends AbstractService {

    public AwsService() {
        super("AWS IoT Service");
    }

    private AWSIotMqttClient client;

    @Property("ClientEndpoint") private String clientEndpoint = "a2q2k2ttlfw6v7.iot.us-west-2.amazonaws.com";
    @Property("ClientId") private String clientId = "sdk-java";
    @Property("CertificateFile") private String certificateFile = "/home/grimalkin/Downloads/aws/SnootBox.cert.pem";
    @Property("PrivateKeyFile") private String privateKeyFile = "/home/grimalkin/Downloads/aws/SnootBox.private.key";

    @Override
    protected void onStart() {
        KeyStorePasswordPair pair = getKeyStorePasswordPair(certificateFile, privateKeyFile);
        client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        System.out.println(clientId + "   " + pair.keyStore + "   " + pair.keyPassword);

        try {
            client.connect();
        } catch (AWSIotException e) {
            log("Error connecting to AWS IoT: " + e.getMessage());
        }
    }


    public void publish(String topic, String message) {
        publish(topic, new StringMap().add("message", message).get());
    }

    public void publish(String topic, Map<String, String> params) {
        try {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }
            client.publish(topic, AWSIotQos.QOS0, json.toString());
        } catch (JSONException e) {
            log("Error building JSON: " + e.getMessage());
        } catch (AWSIotException e) {
            log("Error publishing to AWS topic '" + topic + "': " + e.getMessage());
        }
    }

    public void subscribe(String topicName, Consumer<AWSIotMessage> callback) {
        try {
            client.subscribe(new AWSIotTopic(topicName, AWSIotQos.QOS0){
                @Override
                public void onMessage(AWSIotMessage message) {
                    callback.accept(message);
                }
            });
        } catch (AWSIotException e) {
            log("Error subscribing to AWS topic '" + topicName + "': " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {

    }
}
