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
import java.util.List;


/**
 * Handles user input and controls the application flow related to the {@link Canvas}.
 * The controller coordinates input events, canvas operations, and rendering updates.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Controller {




    private Point startPoint;
    private Point currentPoint;
    private Point editSelected;
    List<Line> lineList;

    private int lineColor = 0x00FF00;
    private static final int PREVIEW_COLOR = 0xFF0000;


    private final Canvas canvas;
    private final Sidebar sidebar;
    private final Window window;
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private Polygon selectedPolygon;
    private final List<Polygon> polygonList;

    private int polyIdCounter =0;

    public Mode currentMode;





    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window,Canvas canvas, Sidebar sidebar) {

        this.canvas = canvas;
        this.sidebar = sidebar;
        this.window = window;
        lineRasterizer = new TrivialLineRasterizer(canvas.getRaster());
        polygonRasterizer = new PolygonOutlineRasterizer(lineRasterizer);
        polygonList  = new ArrayList<Polygon>();
        polygonList.add(new Polygon());
        selectedPolygon = polygonList.getFirst();
        selectedPolygon.polygonId =0;
        sidebar.polygonPicker.addItem("Polygon 0");
        lineList = selectedPolygon.Lines;
        currentMode = Mode.DRAW;



    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.clear();

        sidebar.lineColorRect.setBackground(new Color(lineColor));

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
               else if(currentMode == Mode.DELETE){
                   deleteMousePress(e);
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

        sidebar.drawRadioBtn.addActionListener(e -> {
            currentMode = Mode.DRAW;
            renderMain(false,false);
        });

        sidebar.editRadioBtn.addActionListener(e -> {
            currentMode = Mode.EDIT;
            renderMain(false,true);

        });

        sidebar.deleteRadioBtn.addActionListener(e -> {
            currentMode = Mode.DELETE;
            renderMain(false,true);

        });

        sidebar.colorPickBtn.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(sidebar, "Pick Color", new Color(lineColor));
            if(selected != null){
                sidebar.lineColorRect.setBackground(selected);
                lineColor = selected.getRGB();

            }



        });

        sidebar.applyToAllBtn.addActionListener(e -> {
            for(Line l : lineList){
                l.setColor(lineColor);
            }
            renderMain();
        });

        sidebar.polygonPicker.addActionListener(e -> {
            int ind = sidebar.polygonPicker.getSelectedIndex();

            if(ind > polygonList.size()-1){
                ind =polygonList.size()-1;
            }


            selectedPolygon = polygonList.get(ind);
            lineList = selectedPolygon.GetLines();
            renderMain();






        });

        sidebar.addPolygonBtn.addActionListener(e -> {

            if(polygonList.size() >= 10) {
                return;
            }

            polygonList.add(new Polygon());

            int ID = ++polyIdCounter;
            polygonList.getLast().polygonId = ID;
            sidebar.polygonPicker.addItem("Polygon " + ID);


        });

        sidebar.removePolygonBtn.addActionListener(e -> {
            if(polygonList.size() == 1){
                return;
            }

            int ind = sidebar.polygonPicker.getSelectedIndex();
            polygonList.remove(ind);
            sidebar.polygonPicker.removeItemAt(ind);

           if(ind > polygonList.size()-1){
                selectedPolygon = polygonList.getLast();
                sidebar.polygonPicker.setSelectedIndex(polygonList.size()-1);
            }
            else {
                selectedPolygon = polygonList.get(ind);
                sidebar.polygonPicker.setSelectedIndex(ind);
            }

            lineList = selectedPolygon.GetLines();
            renderMain();


        });

        //endregion


        canvas.repaint();
    }

    //region eventHandlers

    private final Action keyPressedC = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          clearCanvas();
        }
    };

    private final Action keyPressedE = new AbstractAction() {
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
            renderMain(false,true);

            sidebar.editRadioBtn.setSelected(true);

        }
        else if(currentMode == Mode.EDIT){
            currentMode = Mode.DELETE;
            renderMain(false,true);
            sidebar.deleteRadioBtn.setSelected(true);

        }
        else if(currentMode == Mode.DELETE){
            currentMode = Mode.DRAW;
            renderMain(false,false);
            sidebar.drawRadioBtn.setSelected(true);

        }
    }

    private void clearCanvas(){
        for(Polygon p : polygonList){
            p.Lines.clear();
        }
        canvas.clear();
        canvas.repaint();
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

        renderMain(true,false);
        drawModePreview();
    }


    private void drawMouseRelease(MouseEvent e){
        if(e.getX() < 0 || e.getX() > canvas.getWidth() || e.getY() < 0 || e.getY() > canvas.getHeight() )
        {
            startPoint = null;
            currentPoint = null;
            renderMain();
            return;
        }


        if(lineList.size() > 2){
            Line closest = getClosestLine(getPoint(e));
            lineList.add(new Line(closest.getP1(),currentPoint, lineColor));
            lineList.add(new Line(currentPoint,closest.getP2(), lineColor));
            lineList.remove(closest);
        }
        else
        {
            lineList.add(new Line(startPoint,getPoint(e), lineColor));
        }




        startPoint = null ;
        currentPoint  = null;

        renderMain(false,false);
    }

    private void drawMouseDrag(MouseEvent e){

        currentPoint = getPoint(e);
        renderMain(true,false);
        drawModePreview();
    }

    //endregion

    //region editModeFunctions

    private void editMousePress(MouseEvent e){

        if(lineList.isEmpty())
            return;

        editSelected = getClosestPointInRange(getPoint(e),5);

        if(editSelected == null) return;

        renderMain(false,true);
        editModePreview();



    }

    private void editMouseRelease(MouseEvent e) {
        editSelected = null;
        renderMain(false,true);

    }

    private void editMouseDrag(MouseEvent e){

        if(lineList.isEmpty())
            return;
        if(editSelected == null) return;

        if(e.getX() < 0 || e.getX() > canvas.getWidth() || e.getY() < 0 || e.getY() > canvas.getHeight() )
        {
            renderMain(false,true);
            editModePreview();
            return;
        }


        editSelected.setX(e.getX());
        editSelected.setY(e.getY());

        renderMain(false,true);
        editModePreview();
    }
    //endregion

    //region deleteModeFunctions

        private void deleteMousePress(MouseEvent e)
        {
            for(Line l : lineList){

            }
            if(lineList.isEmpty())
                return;



            Point toDel = getClosestPointInRange(getPoint(e),5);


            if(toDel == null) return;


            List<Line> toMerge = getLinesByCommonPoint(toDel);


            if(toMerge.size() == 1){
                lineList.remove(toMerge.getFirst());
            }
            else if(toMerge.size() == 2){
                List<Point> edgs = getEdgePoints(toMerge,toDel);

                if(lineList.size()!=3){
                    if (edgs != null) {
                        lineList.add(new Line(edgs.getFirst(), edgs.getLast(), lineColor));
                    }
                    else {
                        return;
                    }
                }

                lineList.remove(toMerge.getFirst());
                lineList.remove(toMerge.getLast());
            }

            renderMain(false,true);


        }
    //endregion

    //region utils


    private List<Line> getLinesByCommonPoint(Point com){

        return lineList.stream().filter(x -> x.getP1() == com || x.getP2() == com).toList();
    }

    private List<Point> getEdgePoints(List<Line> lins, Point mid){
        if(lins.size() != 2){
            return  null;
        }

        List<Point> p = new ArrayList<>();


        for(Line l : lins){

            if(l.getP1() == mid){
                p.add(l.getP2());
            }
            else if(l.getP2() == mid){
                p.add(l.getP1());
            }
        }

        return p;

    }



    private Point getPoint(MouseEvent e){
        return  new Point(e.getX(),e.getY());
    }

    private Line getClosestLine(Point curr){

        Line closest = null;
        double dist = -1;


        for(Line l : selectedPolygon.GetLines())
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

    private Point getClosestPointInRange(Point at, int range){

        double dist = -1;
        Point closest = null;

        for (Line l : selectedPolygon.GetLines()){
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

    private void renderMain(){
        if(currentMode == Mode.EDIT || currentMode == Mode.DELETE){
            renderMain(false,true);
        }
        else
        {
            renderMain(false,false);
        }
    }

    private void renderMain(boolean mids, boolean points){
        canvas.clear();

        if(mids) renderMids();
        if(points) renderPoints();

        renderPolygons();



        canvas.repaint();



    }

    private void editModePreview(){
        List<Point> p = new ArrayList<>();
        List<Line> cl =  getLinesByCommonPoint(editSelected);

        for(Line l : cl){

            if(l.getP1() == editSelected){
                p.add(l.getP2());
            }
            else if(l.getP2() == editSelected){
                p.add(l.getP1());
            }
        }


        if(p.size() == 1){
            previewRender(p.getFirst(),editSelected);
        }
        else {
            previewRender(p.getFirst(),p.getLast(),editSelected);
        }
    }

    private void drawModePreview(){


        if(lineList.isEmpty()){
            previewRender(startPoint,currentPoint);

        }
        else if (lineList.size() == 1){
            Line l = lineList.getFirst();
            previewRender(l.getP1(),l.getP2(),currentPoint);
        }
        else
        {
            Line l = getClosestLine(currentPoint);
            previewRender(l.getP1(),l.getP2(),currentPoint);
        }

    }

    private void previewRender(Point p1, Point p2){



        Line prwLine = new Line(p1,p2,PREVIEW_COLOR);
        lineRasterizer.rasterize(prwLine);
        canvas.repaint();



    }

    private void previewRender(Point p1, Point p2, Point midP){
        Line prwL1 = new Line(p1, midP, PREVIEW_COLOR);
        Line prwL2 = new Line(p2, midP, PREVIEW_COLOR);
        lineRasterizer.rasterize(prwL1);
        lineRasterizer.rasterize(prwL2);
        canvas.repaint();

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

    private void renderPoints(){
        for(Line l:lineList){



            for(int i = -2; i < 3; i++){
                for(int j = -2; j < 3; j++){

                    int x1 =l.getP1().getX()+i;
                    int y1 = l.getP1().getY()+j;

                    canvas.getRaster().setPixel(x1, y1,0x0000FF );


                    int x2 =l.getP2().getX()+i;
                    int y2 = l.getP2().getY()+j;

                    canvas.getRaster().setPixel(x2, y2,0x0000FF );

                }
            }


        }
    }



    private void renderPolygons(){

        for (Polygon p : polygonList){
            polygonRasterizer.rasterize(p);
        }



    }

    //endregion
}


