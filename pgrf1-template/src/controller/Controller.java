package controller;

import graphics.rasterizer.LineRasterizer;
import graphics.rasterizer.PolygonOutlineRasterizer;
import graphics.rasterizer.PolygonRasterizer;
import graphics.rasterizer.TrivialLineRasterizer;
import model.*;
import model.Point;
import model.Polygon;
import view.Canvas;
import view.Sidebar;
import view.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Handles user input and controls the application flow related to the {@link Canvas}.
 * The controller coordinates input events, canvas operations, and rendering updates.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Controller {

    private static Controller controller;

    public static Controller get(){
        return controller;
    }

    private Point startPoint;
    private Point endPoint;
    private Point currentPoint;
    private Point editSelected;
    ArrayList<Line> lineList = new ArrayList<Line>();

    private static final int LINE_COLOR = 0x00FF00;
    private static final int PREVIEW_COLOR = 0xFF0000;


    private final Canvas canvas;
    private final Sidebar sidebar;
    private final Window window;
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private Polygon poly;

    public Mode currentMode;





    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window,Canvas canvas, Sidebar sidebar) {

        this.canvas = canvas;
        this.sidebar = sidebar;
        this.window = window;
        controller = this;
        lineRasterizer = new TrivialLineRasterizer(canvas.getRaster());
        polygonRasterizer = new PolygonOutlineRasterizer(lineRasterizer);
        poly = new Polygon();// todo pridavani do polygonu
        currentMode = Mode.DRAW;



    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.clear();

        //region Keyevents

        InputMap IM = window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap AM = window.getRootPane().getActionMap();

        IM.put(KeyStroke.getKeyStroke("C"),"pressedC");
        AM.put("pressedC",keyPressedC);

        IM.put(KeyStroke.getKeyStroke("E"),"pressedE");
        AM.put("pressedE",keyPressedE);



        //endregion



        //region canvEvent
       canvas.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               super.mousePressed(e);

               if(currentMode == Mode.DRAW){
                   drawMousePress(e);
               }
               else if(currentMode == Mode.EDIT){
                   editMousePress(e);
               }

           }

           @Override
           public void mouseReleased(MouseEvent e) {
               super.mouseReleased(e);

               if(currentMode == Mode.DRAW){
                   drawMouseRelease(e);
               }
               else if(currentMode == Mode.EDIT){
                    editMouseRelease(e);
               }

           }

       });

       canvas.addMouseMotionListener(new MouseMotionAdapter() {
           @Override
           public void mouseDragged(MouseEvent e) {
               super.mouseDragged(e);
               if(currentMode == Mode.DRAW){
                   drawMouseDrag(e);
               }
               else if(currentMode == Mode.EDIT){
                    editMouseDrag(e);
               }


           }
       });


       //endregion


        //region sidebarEvents

        sidebar.drawRadioBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentMode = Mode.DRAW;
            }
        });

        sidebar.editRadioBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentMode = Mode.EDIT;
            }
        });

        //endregion


        canvas.repaint();
    }

    //region eventHandlers




    private Action keyPressedC = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          clearCanvas();
        }
    };

    private Action keyPressedE = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            switchMode();
        }
    };




    //endregion

    //region globalFunctions

    private void switchMode(){

        if(currentMode == Mode.DRAW){
            currentMode = Mode.EDIT;
            sidebar.editRadioBtn.setSelected(true);
        }
        else if(currentMode == Mode.EDIT){
            currentMode = Mode.DRAW;
            sidebar.drawRadioBtn.setSelected(true);
        }
    }

    private void clearCanvas(){
        lineList.clear();
        canvas.clear();
        render();
    }
    //endregion

    //region drawModeFunctions
    private void drawMousePress(MouseEvent e){
        if(lineList.size() ==1){
            startPoint = lineList.getLast().getP2();
        }
        else
        {
            startPoint = getPoint(e);
        }

        currentPoint = startPoint;

        previewRender();
    }

    private void drawMouseRelease(MouseEvent e){
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

    private void drawMouseDrag(MouseEvent e){

        currentPoint = getPoint(e);
        previewRender();
    }

    //endregion

    //region editModeFunctions

    private void editMousePress(MouseEvent e){
        editSelected = getClosestInRange(getPoint(e),5);
    }

    private void editMouseRelease(MouseEvent e){
        editSelected = null;
    }

    private void editMouseDrag(MouseEvent e){
        if(editSelected != null){
            editSelected.setX(e.getX());
            editSelected.setY(e.getY());
        }
        //todo Currently just draws ad infinitum. add edit preview and correct drawing
    }
    //endregion

    //region utils



    private Point getPoint(MouseEvent e){
        return  new Point(e.getX(),e.getY());
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

    private Point getClosestInRange(Point at, int range){

        double dist = -1;
        Point closest = null;

        for (Line l : poly.GetLines()){
            double p1D = l.getP1().pointDistance(at);
            double p2D = l.getP2().pointDistance(at);

            if(p1D > dist && p2D > dist && dist != -1){
                continue;
            }

            if((p1D < dist || dist == -1) && p1D <= range){
                dist = p1D;
                closest = l.getP1();
            }

            if((p2D < dist || dist == -1) && p2D <= range){
                dist = p2D;
                closest = l.getP2();
            }


        }
        return closest;
    }

    //endregion

    //region render
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

    //endregion
}


