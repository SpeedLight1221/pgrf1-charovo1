package controller;

import graphics.rasterizer.LineRasterizer;
import graphics.rasterizer.PolygonOutlineRasterizer;
import graphics.rasterizer.PolygonRasterizer;
import graphics.rasterizer.TrivialLineRasterizer;
import model.Line;
import model.Point;
import model.Polygon;
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
    private final LineRasterizer rasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private final Polygon poly;



    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Canvas canvas) {

        this.canvas = canvas;
        rasterizer = new TrivialLineRasterizer(canvas.getRaster());
        polygonRasterizer = new PolygonOutlineRasterizer(rasterizer);
        poly = new Polygon();// todo pridavani do polygonu

        lineList.add(new Line(new Point(10,10),new Point(100,100),LINE_COLOR));
        lineList.add(new Line(new Point(100,100),new Point(200,50),LINE_COLOR));
        lineList.add(new Line(new Point(200,50),new Point(300,300),LINE_COLOR));
        lineList.add(new Line(new Point(300,300),new Point(50,250),LINE_COLOR));

    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.clear();
        polygonRasterizer.rasterize(new Polygon(lineList));

       canvas.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               super.mousePressed(e);

               //TODO : vzít aktuální pozici, při stisknutí LMB přidat nový bod do polygonu

               startPoint = getPoint(e);
               currentPoint = startPoint;





           }

           @Override
           public void mouseReleased(MouseEvent e) {
               super.mouseReleased(e);
               //TODO : při puštění zmizí preview čára,

               lineList.add(new Line(startPoint,getPoint(e),LINE_COLOR));
               startPoint = null ;
               currentPoint  = null;
               render();

           }

           //TODO : Drag mouse, aktualizovat preview
       });

       canvas.addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
               super.keyPressed(e);

               if(e.getKeyCode() == KeyEvent.VK_U){

               }



           }


       });

        canvas.repaint();
    }

    private void Render(){
        canvas.clear();
        polygonRasterizer.rasterize(poly);
        canvas.repaint();
    }

    private Point getPoint(MouseEvent e){
        return  new Point(e.getX(),e.getY());
    }

    private void startLine(){
        //todo co se stane pri zacatku kresleni, preview line
    }

    private void finishPolygon(){
        //todo zavolat render
    }

    //todo final, kreslime polygon, v moment co mame 2 cary se dokonci, dalsi kliknuti pak prida novy bod do polygonu

    private void render(){
        canvas.clear();
        if(lineList.size() > 1)
        {
            polygonRasterizer.rasterize(new Polygon(lineList));
        }
        else{
            for(Line e : lineList){
                rasterizer.rasterize(e);
            }
        }
        canvas.repaint();
    }
}