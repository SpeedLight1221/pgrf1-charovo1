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
import java.util.ArrayList;


/**
 * Handles user input and controls the application flow related to the {@link Canvas}.
 * The controller coordinates input events, canvas operations, and rendering updates.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Controller {

    private Point startPoint;
    private Point endPoint;
    private Point currentPoint;

    ArrayList<Line> lineList = new ArrayList<Line>();

    private static final int LINE_COLOR = 0x00FF00;
    private static final int PREVIEW_COLOR = 0x00AA00;


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
        //raster.rasterize(new Line(new Point(50,50),new Point(300,150),0xFF00FF));
        //todo : dominant y axis fix, also fix perfectly vertical lines
       canvas.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               super.mousePressed(e);
               startPoint = getPoint(e);
               currentPoint = startPoint;


           }

           @Override
           public void mouseReleased(MouseEvent e) {
               super.mouseReleased(e);
               lineList.add(new Line(startPoint,getPoint(e),LINE_COLOR));
               startPoint = null ;
               currentPoint  = null;
               render();
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

    private Point getPoint(MouseEvent e){
        return  new Point(e.getX(),e.getY());
    }

    private void render(){
        canvas.clear();
        for(Line e : lineList){
            raster.rasterize(e);
        }
        canvas.repaint();
    }
}