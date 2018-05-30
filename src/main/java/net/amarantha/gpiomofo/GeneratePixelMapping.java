package net.amarantha.gpiomofo;

import net.amarantha.utils.math.MathUtils;

import static java.lang.Math.PI;
import static net.amarantha.utils.math.MathUtils.round;

public class GeneratePixelMapping {

    private static int radials = 8;
    private static int centreSpace = 5;
    private static int pixelsSquare = 40;
    private static int pixelSpacing = 10;
    private static int deadPixelsOuter = 2;
    private static int deadPixelsInner = 1;
    private static int radialPixels = 10;

    private static double radialAngle;
    private static int patternLength;
    private static int realRadius;

    private static int pixelCount;

    public static void main(String[] args) {
        radialAngle = (2 * PI) / radials;
        patternLength = (2 * radialPixels) + deadPixelsInner + deadPixelsOuter;
        pixelCount = radials * patternLength;
        realRadius = centreSpace + ((radialPixels-1) * pixelSpacing);
        System.out.println(pixelCount);
        for ( int i=0; i<pixelCount; i++ ) {
            int[] xy = calculateXY(i);
            if ( xy!=null ) {
//                System.out.println(xy[0] + "," + xy[1] + ",6");
            }
        }
    }


    private static int[] calculateXY(int pixelNumber) {

        int p = pixelNumber % patternLength;
        int n = pixelNumber / patternLength;

        Double d = null;
        Double t = null;

        if ( p < radialPixels ) {
            // inward radial
            d = (double)(centreSpace + ((radialPixels - p - 1) * pixelSpacing));
            t = 2 * radialAngle * n;
        } else {
            int c = p-radialPixels-deadPixelsInner;
            if ( c >= 0 && c < radialPixels ) {
                // outward radial
                d = (double)(centreSpace + (c * pixelSpacing));
                t = 2 * radialAngle * (n + 1);
            }
        }
        if ( d != null && t != null ) {
            double realX = realRadius + (d * Math.cos(t));
            double realY = realRadius + (d * Math.sin(t));

//            System.out.println(d + " , " + t + " = " + realX + " , " + realY);
//            System.out.println((2*PI)/t+" "+Math.cos(t));
            double f = (2*realRadius)/pixelsSquare;
//            System.out.println(f);
            int x = round((2*realRadius)/f);
            int y = round((2*realRadius)/f);
            return new int[] { x, y };
        } else {
            return new int[] { 0, 0 };
        }

    }

}
