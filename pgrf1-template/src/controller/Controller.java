package controller;

import graphics.rasterizer.LineRasterizer;
import graphics.rasterizer.PolygonOutlineRasterizer;
import graphics.rasterizer.PolygonRasterizer;
import graphics.rasterizer.TrivialLineRasterizer;
import model.Line;
import model.Point;
import model.Polygon;
import model.Tuple;
import view.Canvas;

import java.awt.event.*;
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
    private static final int PREVIEW_COLOR = 0xFF0000;


    private final Canvas canvas;
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private Polygon poly;



    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Canvas canvas) {

        this.canvas = canvas;
        lineRasterizer = new TrivialLineRasterizer(canvas.getRaster());
        polygonRasterizer = new PolygonOutlineRasterizer(lineRasterizer);
        poly = new Polygon();// todo pridavani do polygonu



    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.clear();


       canvas.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               super.mousePressed(e);


                if(!lineList.isEmpty()){
                    startPoint = lineList.getLast().getP2();
                }
                else
                {
                    startPoint = getPoint(e);
                }


               currentPoint = startPoint;

               previewRender();





           }

           @Override
           public void mouseReleased(MouseEvent e) {
               super.mouseReleased(e);

               //TODO : při puštění zmizí preview čára,



               if(lineList.size() > 2){
                   Line closest = getClosestLine(getPoint(e));
                   lineList.add(new Line(closest.getP1(),currentPoint,LINE_COLOR));
                   lineList.add(new Line(currentPoint,closest.getP2(),LINE_COLOR));
                   lineList.remove(closest);
               }
               else
               {
                   lineList.add(new Line(startPoint,getPoint(e),LINE_COLOR));
               }


               startPoint = null ;
               currentPoint  = null;

               canvas.clear();
               render();

           }




           //TODO : Drag mouse, aktualizovat preview
       });

       canvas.addMouseMotionListener(new MouseMotionAdapter() {
           @Override
           public void mouseDragged(MouseEvent e) {

               System.out.println("Drag");
               super.mouseDragged(e);

               currentPoint = getPoint(e);
               previewRender();
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

    private void previewRender(){

        canvas.clear();
        if(lineList.size() < 1){
            Line prwLine = new Line(startPoint,currentPoint,PREVIEW_COLOR);
            lineRasterizer.rasterize(prwLine);

        }
        else if(lineList.size() ==1)
        {
            Line prwL1 = new Line(lineList.get(0).getP1(), currentPoint, PREVIEW_COLOR);
            Line prwL2 = new Line(lineList.get(0).getP2(), currentPoint, PREVIEW_COLOR);
            lineRasterizer.rasterize(prwL1);
            lineRasterizer.rasterize(prwL2);
        }
        else
        {
            Line closest = getClosestLine(currentPoint);
            Line prwL1 = new Line(closest.getP1(), currentPoint, PREVIEW_COLOR);
            Line prwL2 = new Line(closest.getP2(), currentPoint, PREVIEW_COLOR);
            lineRasterizer.rasterize(prwL1);
            lineRasterizer.rasterize(prwL2);
        }
        renderMids();
        render();
    }

    private Line getClosestLine(Point curr){

        Line closest = null;
        double dist = -1;


        for(Line l : poly.GetLines())
        {
            double cdist = l.getMid().pointDistance(curr);
            if(dist > cdist || dist == -1)
            {
                closest = l;
                dist= cdist;
            }

        }

        return closest;
    }

    private void renderMids(){
        for(Line l:lineList){


            Point mid = l.getMid();
            for(int i = -2; i < 3; i++){
                for(int j = -2; j < 3; j++){

                    int xmx =mid.getX()+i;
                    int ymy = mid.getY()+j;


                    canvas.getRaster().setPixel(xmx, ymy,0x0000FF );
                }
            }


        }
    }



    private void render(){



        poly = new Polygon(lineList);
        polygonRasterizer.rasterize(poly);




        canvas.repaint();
    }
}