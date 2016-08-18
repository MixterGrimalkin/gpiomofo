package net.amarantha.gpiomofo.pixeltape;

public class RGBW extends RGB {

    private final int white;

    public RGBW(int red, int green, int blue, int white) {
        super(red, green, blue);
        this.white = white;
    }

    public RGBW withBrightness(double brightness) {
        return new RGBW(
                (int)Math.round(getRed()*brightness),
                (int)Math.round(getGreen()*brightness)
                ,
                (int)Math.round(getBlue()*brightness)
                ,
                (int)Math.round(white*brightness))
                ;
    }

    public int getWhite() {
        return white;
    }

    public static RGB[] convertToRGB(RGBW[] input) {
        RGB[] output = new RGB[((input.length/3)*4)];
        int j=0;
        for ( int i=0; i<input.length; i+=3 ) {
            RGBW c1 = input[i];
            RGBW c2 = i+1 < input.length ? input[i+1] : new RGBW(0,0,0,0);
            RGBW c3 = i+2 < input.length ? input[i+2] : new RGBW(0,0,0,0);
            if ( c1!=null && j<output.length ) output[j++] =
                    new RGB(c1.getGreen(),      c1.getRed(),  c1.getBlue());
            if ( c1!=null && c2!=null && j<output.length ) output[j++] =
                    new RGB(c1.getWhite(),    c2.getGreen(),    c2.getRed());
            if ( c2!=null && c3!=null && j<output.length ) output[j++] =
                    new RGB(c2.getBlue(),     c2.getWhite(),  c3.getGreen());
            if ( c3!=null && j<output.length ) output[j++] =
                    new RGB(c3.getRed(),    c3.getBlue(),   c3.getWhite());
        }
        for ( int k=j; k<output.length; k++ ) {
            output[k] = new RGB(0,0,0);
        }
        return output;
    }

    public static void main(String[] args) {

        RGBW[] input = new RGBW[]{
                new RGBW(1, 2, 3, 4),
                new RGBW(11, 12, 13, 14),
                new RGBW(21, 22, 23, 24),
                new RGBW(31, 32, 33, 34),
        };

        RGB[] output = convertToRGB(input);

        print(input);
        print(output);



    }

    private static void print(RGB[] colours) {
        for ( int i=0; i<colours.length; i++ ) {
            System.out.println(colours[i].toString());
        }
    }

    // Little = 24
    // Big = 60

    @Override
    public String toString() {
        return "RGBW{"+getRed()+","+getGreen()+","+getBlue()+","+white+"}";
    }


}
