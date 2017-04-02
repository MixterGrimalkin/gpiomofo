package net.amarantha.gpiomofo.service.gpio.touch;

import com.google.inject.Singleton;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.math.BigInteger;

@Singleton
public class TouchSensorMPR121 extends TouchSensor {

    private I2CDevice device;

    public TouchSensorMPR121() {
        super("MPR-121");
    }

    @Override
    protected void init() {

        try {

            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
            device = bus.getDevice(MPR121_I2CADDR_DEFAULT);

            // Soft reset device
            write(MPR121_SOFTRESET, 0x63);

            // Set electrode configuration to default values
            write(MPR121_ECR, 0x00);

//            // Check CDT, SFI, ESI configuration is at default values
//            int c = device.read(MPR121_CONFIG2);
//            if ( c != 0x24 ) {
//                System.out.println(c); // What is this?
//            }

            // Set threshold for touch and release to default values
            int touch = 12;
            int release = 6;
            for ( int i=0; i<12; i++ ) {
                write(MPR121_TOUCHTH_0 + 2*i, touch);
                write(MPR121_RELEASETH_0 + 2*i, release);
            }

            // Configure baseline filtering control registers
            write(MPR121_MHDR, 0x01);
            write(MPR121_NHDR, 0x01);
            write(MPR121_NCLR, 0x0E);
            write(MPR121_FDLR, 0x00);
            write(MPR121_MHDF, 0x01);
            write(MPR121_NHDF, 0x05);
            write(MPR121_NCLF, 0x01);
            write(MPR121_FDLF, 0x00);
            write(MPR121_NHDT, 0x00);
            write(MPR121_NCLT, 0x00);
            write(MPR121_FDLT, 0x00);

            // Set other configuration registers
            write(MPR121_DEBOUNCE, 0);
            write(MPR121_CONFIG1, 0x10);    // default, 16uA charge current
            write(MPR121_CONFIG2, 0x20);     // 0.5uS encoding, 1ms period
            // Enable all electrodes
            write(MPR121_ECR, 0x8F);        // start with first 5 bits of baseline tracking

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(int address, int data) throws IOException {
        device.write(address, (byte) data);
    }

    protected void scanPins() {
        try {
            int lsb = device.read(MPR121_TOUCHSTATUS_L);
            byte[] buffer = new byte[2];
            device.read(MPR121_TOUCHSTATUS_L, buffer, 0, 2);
            int msb = buffer[1];
            for (int i = 0; i < 8; i++) {
                checkPinState(i, BigInteger.valueOf(lsb).testBit(i));
            }
            for (int i = 0; i < 4; i++) {
                checkPinState(i+8, BigInteger.valueOf(msb).testBit(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final int MPR121_I2CADDR_DEFAULT = 0x5A;
    private static final int MPR121_TOUCHSTATUS_L   = 0x00;
    private static final int MPR121_TOUCHSTATUS_H   = 0x01;
    private static final int MPR121_FILTDATA_0L     = 0x04;
    private static final int MPR121_FILTDATA_0H     = 0x05;
    private static final int MPR121_BASELINE_0      = 0x1E;
    private static final int MPR121_MHDR            = 0x2B;
    private static final int MPR121_NHDR            = 0x2C;
    private static final int MPR121_NCLR            = 0x2D;
    private static final int MPR121_FDLR            = 0x2E;
    private static final int MPR121_MHDF            = 0x2F;
    private static final int MPR121_NHDF            = 0x30;
    private static final int MPR121_NCLF            = 0x31;
    private static final int MPR121_FDLF            = 0x32;
    private static final int MPR121_NHDT            = 0x33;
    private static final int MPR121_NCLT            = 0x34;
    private static final int MPR121_FDLT            = 0x35;
    private static final int MPR121_TOUCHTH_0       = 0x41;
    private static final int MPR121_RELEASETH_0     = 0x42;
    private static final int MPR121_DEBOUNCE        = 0x5B;
    private static final int MPR121_CONFIG1         = 0x5C;
    private static final int MPR121_CONFIG2         = 0x5D;
    private static final int MPR121_CHARGECURR_0    = 0x5F;
    private static final int MPR121_CHARGETIME_1    = 0x6C;
    private static final int MPR121_ECR             = 0x5E;
    private static final int MPR121_AUTOCONFIG0     = 0x7B;
    private static final int MPR121_AUTOCONFIG1     = 0x7C;
    private static final int MPR121_UPLIMIT         = 0x7D;
    private static final int MPR121_LOWLIMIT        = 0x7E;
    private static final int MPR121_TARGETLIMIT     = 0x7F;
    private static final int MPR121_GPIODIR         = 0x76;
    private static final int MPR121_GPIOEN          = 0x77;
    private static final int MPR121_GPIOSET         = 0x78;
    private static final int MPR121_GPIOCLR         = 0x79;
    private static final int MPR121_GPIOTOGGLE      = 0x7A;
    private static final int MPR121_SOFTRESET       = 0x80;

}
