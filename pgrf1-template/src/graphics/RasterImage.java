package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterImage implements Raster{
    private final BufferedImage image;


    public RasterImage(int width, int height) {

        image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
    }

    @Override
    public void present(Graphics graphics) {
        graphics.drawImage(image,0,0,null);
    }

    @Override
    public void setPixel(int x, int y, int rgb) {
        if(x >= 0 && x<image.getWidth() && y >= 0 && y < image.getHeight())
        {
            image.setRGB(x,y,rgb);
        }
    }

    @Override
    public void clear(int color) {
        Graphics graphics = image.getGraphics();

        graphics.setColor(new Color(color));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        graphics.dispose();
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }
}
