package net.amarantha.gpiomofo.trigger;

import com.google.inject.Inject;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.service.gpio.GpioService;
import net.amarantha.gpiomofo.service.task.TaskService;
import net.amarantha.gpiomofo.trigger.RangeTrigger;
import net.amarantha.gpiomofo.utility.Now;

import java.time.LocalDateTime;

public class UltrasonicSensor extends RangeTrigger {

    static {
        System.loadLibrary("hc-sr04");
    }

    @Inject private GpioService gpio;
    @Inject private Now now;
    @Inject private TaskService tasks;

    private final static int TRIG = 0;
    private final static int ECHO = 1;

    private final static int SAMPLES = 5;

    private final static int SENSITIVITY = 3000;

    public void start() {
        gpio.setupDigitalOutput(TRIG);
        gpio.setupDigitalInput(ECHO, PinPullResistance.PULL_DOWN);
        init();
        tasks.addRepeatingTask(this, 100, ()->{
            double total = 0;
            for ( int i=-0; i<SAMPLES; i++ ) {
                total += measure();
            }
            double avg = total/SAMPLES;
            double norm = 1 - (avg/SENSITIVITY);
            if ( norm < 0 ) {
                norm = 0;
            }
            if ( norm > 1 ) {
                norm = 1;
            }
            fire(norm);
        });
    }


    public native void init();
    public native long measure();

    public double measureDistance() {

        try {

            // pulse TRIG
            gpio.write(TRIG, true);
            Thread.sleep(0,100);
            gpio.write(TRIG, false);

            long startScan = LocalDateTime.now().getNano();

            double timeout = 10000000.0;

            long start = LocalDateTime.now().getNano();
            while ( !gpio.read(ECHO) && (start-startScan)<=timeout ) {
                start = LocalDateTime.now().getNano();
            }

            long end = LocalDateTime.now().getNano();
            while ( gpio.read(ECHO) && (end-start)<=timeout) {
                end = LocalDateTime.now().getNano();
            }

            double duration = end-start;

            if ( duration < 0 ) {
                duration = 0;
            }
            if ( duration > timeout ) {
                duration = timeout;
            }

            double result = 1 - (duration/timeout);

            return result;

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return 0.0;

    }



}
