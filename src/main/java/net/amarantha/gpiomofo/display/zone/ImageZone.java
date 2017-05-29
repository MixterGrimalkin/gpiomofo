package net.amarantha.gpiomofo.display.zone;

import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.math.MathUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static net.amarantha.utils.math.MathUtils.round;

public class ImageZone extends AbstractZone {

    private Pattern imagePattern;
    private String filename;

    @Override
    public Pattern getNextPattern() {
        return imagePattern;
    }

    @Override
    public void init() {
        super.init();
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filename));
            convertImage(Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, getWidth(), getHeight()));
        } catch (Exception e) {
            System.out.println("WARNING: " + getId() + " could not read image '"+filename+"'\n"+e.getMessage());
        }
    }

    public ImageZone setImage(String filename) {
        this.filename = filename;
        return this;
    }

    private void convertImage(BufferedImage image) {
        RGB[][] convertedImage = new RGB[image.getHeight()][image.getWidth()];
        for ( int row=0; row<image.getHeight(); row++ ) {
            for ( int col=0; col<image.getWidth(); col++ ) {
                int[] pixel = (image.getRaster().getPixel(col,row,new int[3]));
                double red = pixel[0]/255.0;
                double green = pixel[1]/255.0;
                double blue = pixel[2]/255.0;
                if ( row==0 || row==image.getHeight()-1 || col==0 || col==image.getWidth()-1 ) {
                    // put blank space around image
                    convertedImage[row][col] = RGB.BLACK;
                } else {
                    convertedImage[row][col] = new RGB(round(red), round(green), round(blue));
                }
            }
        }
        imagePattern = new Pattern(convertedImage);
    }

}
