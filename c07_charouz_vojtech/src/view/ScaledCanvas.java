package view;

import graphics.RasterImage;

import java.awt.*;

public class ScaledCanvas extends Canvas {

    int scale;

    public ScaledCanvas(int width, int height, int scale) {
        super(width * scale, height * scale);
        this.scale = scale;
        image = new RasterImage(width, height);
        clear();


    }

    public void setScaledPixel(int x, int y, int clr) {
        int sx = x / scale;
        int sy = y / scale;
        getRaster().setPixel(sx, sy, clr);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.scale(scale, scale);
        getRaster().present(graphics);
    }
}
