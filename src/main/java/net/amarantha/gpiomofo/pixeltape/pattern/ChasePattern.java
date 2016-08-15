package net.amarantha.gpiomofo.pixeltape.pattern;


import static java.lang.Math.round;

public class ChasePattern extends PixelTapePattern {

    private int currentPixel = 0;
    private int dir = 1;

    @Override
    protected void update() {

        clear();

        int g = (int) round(greenMin + ((greenMax-greenMin)*getIntensity()));
        int r = (int) round(redMin + ((redMax-redMin)*getIntensity()));
        int b = (int) round(blueMin + ((blueMax-blueMin)*getIntensity()));

        for ( int i=0; i<width; i++ ) {
            if ( currentPixel+i>=0 && currentPixel+i < getPixelCount() ) {
                setPixel(currentPixel+i, r, g, b);
            }
        }

        currentPixel += dir * movement;
        if ( currentPixel+width < 0 ) {
            currentPixel = -width;
            dir = 1;
        } else if ( (currentPixel+movement) >= getPixelCount() ) {
            if ( bounce ) {
                currentPixel = getPixelCount() - (2*movement);
                dir = -1;
            } else {
                currentPixel = -width;
            }
        }

//        delay(minDelay + (int) round((1-getSpeed()) * (maxDelay-minDelay)));
    }

    @Override
    public void start() {
        super.start();
        currentPixel = -width;
    }

    private int width = 10;
    private int movement = 1;
    private boolean bounce;

    private int redMin;
    private int greenMin;
    private int blueMin;

    private int redMax;
    private int greenMax;
    private int blueMax;

    private int minDelay = 30;
    private int maxDelay = 500;

    public ChasePattern setBlockWidth(int width) {
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

}
