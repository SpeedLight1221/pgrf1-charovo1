package view;

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

    private final BufferedImage image;

    private static final int STROKE_COLOR = Color.WHITE.getRGB();
    private static final Color BACKGROUND_COLOR = new Color(0x2F2F2F);

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Rendering -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void draw() {
        clear();
        image.setRGB(10, 10, STROKE_COLOR);
    }

    public void clear() {
        Graphics graphics = image.getGraphics();

        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        graphics.dispose();
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
        graphics.drawImage(image, 0, 0, null);
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Getters -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public BufferedImage getRaster() {
        return image;
    }

}
