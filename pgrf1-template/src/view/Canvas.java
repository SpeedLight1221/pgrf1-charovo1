package view;

import graphics.RasterImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Represents a raster drawing surface based on a Swing {@link JPanel}.
 * The canvas stores its image data in a {@link BufferedImage} and allows
 * individual pixels to be manipulated and rendered on the screen.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Canvas extends JPanel {

    private final RasterImage image;

    private static final Color BACKGROUND_COLOR = new Color(0x2F2F2F);

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        image = new RasterImage(width,height);
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
