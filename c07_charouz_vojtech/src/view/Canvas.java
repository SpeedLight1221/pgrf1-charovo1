package view;

import graphics.RasterImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a raster drawing surface based on a Swing {@link JPanel}.
 * The canvas stores its image data in a {@link BufferedImage} and allows
 * individual pixels to be manipulated and rendered on the screen.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Canvas extends JPanel {

    protected RasterImage image;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public Color getBackgroundColor() {
        return BACKGROUND_COLOR;
    }

    protected int width;
    protected int height;


    private static final Color BACKGROUND_COLOR = new Color(0x2F2F2F);

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        this.width = width;
        this.height = height;
        image = new RasterImage(width, height);
        clear();
    }


    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Rendering -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */


    public void clear() {
        image.clear(BACKGROUND_COLOR.getRGB());
    }

    /**
     * Paints the current raster image onto this canvas. Swing calls this method automatically whenever the component
     * needs to be repainted.
     *
     * @param graphics graphics context used for painting the component
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        image.present(graphics);
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Getters -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public RasterImage getRaster() {
        return image;
    }

}
