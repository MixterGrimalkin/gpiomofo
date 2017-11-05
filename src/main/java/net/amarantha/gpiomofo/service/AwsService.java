package net.amarantha.gpiomofo.service;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import net.amarantha.utils.service.AbstractService;
import net.amarantha.utils.shell.Utility;

import java.util.function.Consumer;

import static com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.getKeyStorePasswordPair;
import static net.amarantha.utils.shell.Utility.log;

public class AwsService extends AbstractService {

    public AwsService() {
        super("AWS IoT Service");
    }

    private AWSIotMqttClient client;

    @Override
    protected void onStart() {
        String clientEndpoint = "a2q2k2ttlfw6v7.iot.us-west-2.amazonaws.com";
        String clientId = "sdk-java";
        String certificateFile = "/home/grimalkin/Downloads/aws/SnootBox.cert.pem";
        String privateKeyFile = "/home/grimalkin/Downloads/aws/SnootBox.private.key";

        KeyStorePasswordPair pair = getKeyStorePasswordPair(certificateFile, privateKeyFile);
        client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

        try {
            client.connect();
        } catch (AWSIotException e) {
            log("Error connecting to AWS IoT: " + e.getMessage());
        }
    }

    public void publish(String topic, String message) {
        try {
            client.publish(topic, AWSIotQos.QOS0, "{\"message\": \""+ message + "\"}");
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
