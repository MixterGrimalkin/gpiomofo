package net.amarantha.gpiomofo.pixeltape;


import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.task.TaskService;

import static java.lang.Math.round;

public class ChasePattern extends PixelTapePattern {

    private int currentPixel = 0;
    private int dir = 1;

    private boolean bounce;

    private int width = 10;
    private int movement = 1;

    private int redMin;
    private int greenMin;
    private int blueMin;
    private int redMax;
    private int greenMax;
    private int blueMax;

    private int minDelay = 30;
    private int maxDelay = 500;

    @Inject
    public ChasePattern(TaskService tasks) {
        super(tasks);
    }

    public ChasePattern setWidth(int width) {
        this.width = width;
        this.movement = width;
        return this;
    }

    public ChasePattern setMovement(int movement) {
        this.movement = movement;
        return this;
    }

    public ChasePattern setBounce(boolean bounce) {
        this.bounce = bounce;
        return this;
    }

    public ChasePattern setMinColour(int red, int green, int blue) {
        this.redMin = red;
        this.greenMin = green;
        this.blueMin = blue;
        return this;
    }

    public ChasePattern setMaxColour(int red, int green, int blue) {
        this.redMax = red;
        this.greenMax = green;
        this.blueMax = blue;
        return this;
    }

    public ChasePattern setDelayRange(int minDelay, int maxDelay) {
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        return this;
    }

    @Override
    protected void update() {

        int g = (int) round(greenMin + ((greenMax-greenMin)*getIntensity()));
        int r = (int) round(redMin + ((redMax-redMin)*getIntensity()));
        int b = (int) round(blueMin + ((blueMax-blueMin)*getIntensity()));

//        System.out.println(getIntensity() + " : " + r + ", " + g + ", " + b);

        pixelTape.allOff();
        for ( int i=0; i<width; i++ ) {
            if ( currentPixel +i < getPixelCount() ) {
                pixelTape.setPixelColourRGB(currentPixel+i, g, r, b);
            }
        }
        pixelTape.render();

        currentPixel += dir * movement;
        if ( currentPixel <=0 ) {
            currentPixel = 0;
            dir = 1;
        } else if ( (currentPixel+movement) >= getPixelCount() ) {
            if ( bounce ) {
                currentPixel = getPixelCount() - (2*movement);
                dir = -1;
            } else {
                currentPixel = 0;
            }
        }

        delay(minDelay + (int) round((1-getSpeed()) * (maxDelay-minDelay)));
    }

}
