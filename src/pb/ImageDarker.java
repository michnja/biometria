package pb;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.awt.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ImageDarker {

    private WritableImage copyImage;


    private long alpha[] = new long[256];
    private long red[] = new long[256];
    private long green[] = new long[256];
    private long blue[] = new long[256];
    private double brightestRed=0;
    private double brightestGreen=0;
    private double brightestBlue=0;
    private boolean success;

    ImageDarker(WritableImage src)
    {
        copyImage = src;
        success=false;
    }

    public Image makeDarker(){
        PixelReader pixelReader = copyImage.getPixelReader();

        PixelWriter pw = copyImage.getPixelWriter();

        for (int y = 0; y < copyImage.getHeight(); y++) {
            for (int x = 0; x < copyImage.getWidth(); x++) {

                Color color =  copyImage.getPixelReader().getColor(x,y);

                if(color.getRed()>brightestRed)
                    brightestRed=color.getRed();
                if(color.getBlue()>brightestBlue)
                    brightestBlue=color.getBlue();
                if(color.getGreen()>brightestGreen)
                    brightestGreen=color.getGreen();

            }
        }

        brightestRed = 255 / Math.pow(brightestRed,2);
        brightestBlue = 255 / Math.pow(brightestBlue,2);
        brightestGreen = 255 / Math.pow(brightestGreen,2);

        for(int i=0; i<256; i++)
        {
            red[i] = (long) (brightestRed * Math.pow(brightestRed,2));
            green[i] = (long) (brightestGreen * Math.pow(brightestGreen,2));
            blue[i] = (long) (brightestBlue * Math.pow(brightestBlue,2));
        }

        for (int y = 0; y < copyImage.getHeight(); y++) {
            for (int x = 0; x < copyImage.getWidth(); x++) {

                Color color =  copyImage.getPixelReader().getColor(x,y);
                int b = (int) color.getBlue();
                int r = (int) color.getRed();
                int g = (int) color.getGreen();

                Color newColor = Color.rgb(r,g,b);
                pw.setColor(x,y,newColor);

            }
        }

        return copyImage;
    }
}
