package pb;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageDarker {

    private WritableImage copyImage;


    private long alpha[] = new long[256];
    private long red[] = new long[256];
    private long green[] = new long[256];
    private long blue[] = new long[256];
    private double brightestRed = 0;
    private double brightestGreen = 0;
    private double brightestBlue = 0;
    private boolean success;

    ImageDarker(WritableImage src) {
        copyImage = src;
        success = false;
    }

    public Image makeDarker() {
        PixelReader pixelReader = copyImage.getPixelReader();

        PixelWriter pw = copyImage.getPixelWriter();
        for (int x = 0; x < copyImage.getWidth(); x++) {
            for (int y = 0; y < copyImage.getHeight(); y++) {

                java.awt.Color color = new java.awt.Color(pixelReader.getArgb(x, y));

                if (color.getRed() > brightestRed)
                    brightestRed = color.getRed();
                if (color.getBlue() > brightestBlue)
                    brightestBlue = color.getBlue();
                if (color.getGreen() > brightestGreen)
                    brightestGreen = color.getGreen();

            }
        }
        System.out.println("brightestREdd:" + brightestRed + "brightestGreen:" + brightestGreen);

        brightestRed = 255 / Math.pow(brightestRed, 2);
        brightestBlue = 255 / Math.pow(brightestBlue, 2);
        brightestGreen = 255 / Math.pow(brightestGreen, 2);


        for (int i = 0; i < 256; i++) {
            red[i] = (long) (brightestRed * Math.pow(i, 2));
            green[i] = (long) (brightestGreen * Math.pow(i, 2));
            blue[i] = (long) (brightestBlue * Math.pow(i, 2));
        }

        for (int x = 0; x < copyImage.getWidth(); x++) {
            for (int y = 0; y < copyImage.getHeight(); y++) {

                java.awt.Color color = new java.awt.Color(pixelReader.getArgb(x, y));

                int r = (int) red[color.getRed()];
                int g = (int) green[color.getGreen()];
                int b = (int) blue[color.getBlue()];

                System.out.println("red" + r + "green:" + g);

                Color newColor = Color.rgb(r, g, b);
                pw.setColor(x, y, newColor);

            }
        }

        return copyImage;
    }
}
