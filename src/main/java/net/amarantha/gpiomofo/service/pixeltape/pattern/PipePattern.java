package net.amarantha.gpiomofo.service.pixeltape.pattern;

import com.google.inject.Inject;
import net.amarantha.gpiomofo.service.pixeltape.PixelTapeTarget;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.time.TimeGuard;

public class PipePattern extends PixelTapeTarget {

    @Inject private TimeGuard guard;

    long patternLead = 0;
    boolean on = true;
    int count = 0;
    private int onSizeDelta = 0;
    private boolean finish;

    int onSize = 10;
    int offSize = 30;
    int maxOffSize = 30;
    int offSizeDelta;


    long moveInterval = 100;
    int moveBy = 1;

    long sizeInterval = 503;

    RGB onColour = new RGB(255,0,0);
    RGB offColour = new RGB(0,0,0);
    RGB offColour1 = new RGB(0,0,0);
    RGB offColour2 = new RGB(255,255,255);

    @Override
    public PixelTapeTarget init(int startPixel, int pixelCount) {
        return super.init(startPixel, pixelCount);
    }

    @Override
    public void start() {
        super.start();
        onSize = 10;
        offSize = 30;
        patternLead = 0;
        offColour = offColour1;
        offSizeDelta = -2;
        onSizeDelta = 0;
        finish = false;
        climax = false;
        currentPattern = new RGB[currentPattern.length];
    }

    boolean climax;

    @Override
    protected void update() {

        if ( climax ) {


        } else {
            shiftPattern();
            setPixel(0, getNextPixel());

            guard.every(moveInterval, "move", () -> {
                patternLead += moveBy;
            });

            guard.every(sizeInterval, "size", () -> {
                onSize += onSizeDelta;
                if (onSize <= 0) {
                    onSizeDelta = 0;
                    offSizeDelta = 0;
                    onSize = 0;
                    climax = true;
//                    finish = true;
                }
                if (offSize > 1) {
                    offSize += offSizeDelta;
                    if (offSize >= maxOffSize) {
                        onSizeDelta = -1;
                    }
                } else {
                    offColour = offColour2;
                    offSizeDelta = 2;
                    offSize = 2;
                }
            });
        }

    }

    private void shiftPattern() {
        RGB[] newPattern = new RGB[currentPattern.length];
        for ( int i=1; i<getPixelCount()-1; i++ ) {
            newPattern[i] = currentPattern[i-1];
        }
        currentPattern = newPattern;
    }

    private RGB getNextPixel() {
        if ( finish ) {
            return new RGB(0,0,0);
        }
        if ( on ) {
            if ( count >= onSize ) {
                count = 0;
                on = false;
            }
            count++;
            return onColour;
        } else {
            if ( count >= offSize ) {
                count = 0;
                on = true;
            }
            count++;
            return offColour;
        }


    }

    public PipePattern setOnColour(RGB onColour) {
        this.onColour = onColour;
        return this;
    }
}
