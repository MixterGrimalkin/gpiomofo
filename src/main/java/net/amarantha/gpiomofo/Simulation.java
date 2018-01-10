package net.amarantha.gpiomofo;

import javafx.application.Application;
import javafx.stage.Stage;
import net.amarantha.gpiomofo.core.GpioMofo;
import net.amarantha.gpiomofo.core.SimulationModule;

public class Simulation extends Application {

    public static void main(String[] args) {
//        SerialPort serialPort = SerialPort.getCommPorts()[0];
//        serialPort.openPort();
//
//        serialPort.setBaudRate(250000);
//        serialPort.setNumStopBits(2);
//        serialPort.setParity(0);
//
//        System.out.println(serialPort.getDescriptivePortName());
//
//        serialPort.setNumDataBits(8);
//        serialPort.writeBytes(new byte[]{(byte) 0x00}, 1);
//        sleep(0, 100);
//        serialPort.writeBytes(new byte[]{(byte) 0xFF}, 1);
//        sleep(0, 10);
//        serialPort.writeBytes(new byte[]{(byte) 0x00}, 1);
//        sleep(0, 10);
//
//        serialPort.setNumDataBits(8);
//        for ( int i=0; i<512; i++) {
//            serialPort.writeBytes(new byte[]{(byte) 255}, 1);
//        }
//
//        serialPort.setNumDataBits(2);
//        serialPort.writeBytes(new byte[]{(byte) 0x1}, 1);
//        sleep(0, 10);
//        serialPort.closePort();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GpioMofo.build(new SimulationModule(primaryStage)).start();
    }

    private static void sleep(int ms, int micros) {
        try {
            Thread.sleep(ms, micros * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
