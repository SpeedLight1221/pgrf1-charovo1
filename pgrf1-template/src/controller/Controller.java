package controller;

import graphics.rasterizer.LineRasterizer;
import graphics.rasterizer.TrivialLineRasterizer;
import model.Line;
import model.Point;
import view.Canvas;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Handles user input and controls the application flow related to the {@link Canvas}.
 * The controller coordinates input events, canvas operations, and rendering updates.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Controller {

    private final Canvas canvas;
    private final LineRasterizer raster;

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Canvas canvas) {

        this.canvas = canvas;
        raster = new TrivialLineRasterizer(canvas.getRaster());

    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.clear();
        raster.rasterize(new Line(new Point(50,50),new Point(300,150),0xFF00FF));
        //todo : dominant y axis fix, also fix perfectly vertical lines
       canvas.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               super.mousePressed(e);


           }
       });

       canvas.addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
               super.keyPressed(e);


           }


       });

        canvas.repaint();
    }

}