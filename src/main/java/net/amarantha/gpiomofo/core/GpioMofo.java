package net.amarantha.gpiomofo.core;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import gnu.io.*;
import net.amarantha.gpiomofo.Dmx;
import net.amarantha.gpiomofo.factory.ScenarioBuilder;
import net.amarantha.gpiomofo.scenario.Scenario;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.TooManyListenersException;

import static net.amarantha.utils.shell.Utility.log;

@Singleton
public class GpioMofo {

    @Inject
    private ScenarioBuilder builder;

    private Scenario scenario;

    private boolean simulation = false;

    public void startSimulation() {
        simulation = true;
        start();
    }

    public void start() {

        listPorts();


//        String clientEndpoint = "a2q2k2ttlfw6v7.iot.us-west-2.amazonaws.com";
//        String clientId = "sdk-java";
//        String certificateFile = "/home/grimalkin/Downloads/aws/SnootBox.cert.pem";
//        String privateKeyFile = "/home/grimalkin/Downloads/aws/SnootBox.private.key";
//        String awsAccessKeyId = "";
//        String awsSecretAccessKey = "";
//        String sessionToken = "";
//
//        // AWS IAM credentials could be retrieved from AWS Cognito, STS, or other secure sources
//        AWSIotMqttClient client = new AWSIotMqttClient(
//                clientEndpoint,
//                clientId,
//                awsAccessKeyId,
//                awsSecretAccessKey,
//                sessionToken);
//
//        // optional parameters can be set before connect()
//        try {
//
//            client.connect();
//
//        } catch (AWSIotException e) {
//            e.printStackTrace();
//        }

        System.out.println("Hello");
//        try {
//            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");
//            CommPort comPort = portIdentifier.open(this.getClass().getName(),2000);
//            if(comPort instanceof SerialPort){
//                SerialPort port = (SerialPort) comPort;
//                port.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
//                System.out.println("!!!");
//            }
//        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
//            e.printStackTrace();
//        }
        Dmx dmx = new Dmx();
        if(dmx.open("/dev/ttyUSB0")){
//            dmx.getSerialNumber();
//            dmx.getParameters();
            long start = System.currentTimeMillis();
            int[] values = new int[]{ 255, 200, 150, 100, 50, 0, 50, 100, 150, 200 };
            int[] deltas = new int[]{ 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
            while ( System.currentTimeMillis() - start < 30000 ) {
                for ( int i=0; i<values.length; i++ ) {

                    dmx.setDMXChannel(i, values[i], true);

                    values[i] += deltas[i];
                    if ( values[i] > 255 ) {
                        values[i] = 255;
                        deltas[i] *= -1;
                    }
                    if ( values[i] < 0 ) {
                        values[i] = 0;
                        deltas[i] *= -1;
                    }
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        System.out.println("Goodbye");
        System.exit(0);


//        scenario = builder.loadScenario();
//        scenario.start();
//        if (!simulation) {
//            waitForEnter();
//        }
    }

    static void listPorts()
    {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() )
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        }
    }

    static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }

    private void waitForEnter() {
        log(true, " (Press ENTER to quit) ", true);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {
        }
        stop();
    }

    public void stop() {
        scenario.stop();
        System.exit(0);
    }

    public Scenario getScenario() {
        return scenario;
    }
}
