package controller;

import graphics.cropper.PolygonCropper;
import graphics.cropper.SHPolygonCropper;
import graphics.filler.*;
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
    private int fillColor = 0xFF0000;


    private final Canvas canvas;
    private final Sidebar sidebar;
    private final Window window;
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private Polygon cutPolygon;

    private Polygon selectedPolygon;
    private final List<Polygon> polygonList;

    private int polyIdCounter =1;

    public Mode currentMode;

    private Point seedFillPoint;
    private SeedFiller seedFiller;

    private PolygonFiller polygonFiller;

    private PolygonCropper polygonCropper;





    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window,Canvas canvas, Sidebar sidebar) {

        this.canvas = canvas;
        this.sidebar = sidebar;
        this.window = window;
        lineRasterizer = new TrivialLineRasterizer(canvas.getRaster());
        polygonRasterizer = new PolygonOutlineRasterizer(lineRasterizer);
        polygonList  = new ArrayList<Polygon>();
        cutPolygon = new Polygon();
        cutPolygon.lineOverrideColor = 0xFF0000;
        cutPolygon.polygonId = 0;
        polygonList.add(cutPolygon);
        polygonList.add(new Polygon());
        selectedPolygon = polygonList.get(1);
        selectedPolygon.polygonId =1;
        sidebar.polygonPicker.addItem("Cut Polygon");

        sidebar.polygonPicker.addItem("Polygon 1");
        sidebar.polygonPicker.setSelectedIndex(1);
        lineList = selectedPolygon.Lines;
        currentMode = Mode.DRAW;
        seedFiller = new StackSeedFiller(canvas.getRaster());
        polygonFiller = new ScanlineFiller(canvas.getRaster());
        polygonCropper = new SHPolygonCropper();



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

        IM.put(KeyStroke.getKeyStroke("T"),"pressedT");
        AM.put("pressedT",keyPressedT);

        IM.put(KeyStroke.getKeyStroke("O"),"pressedO");
        AM.put("pressedO",keyPressedO);



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
               else if(currentMode == Mode.SEEDFILL){
                   seedFillAt(e);
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
               else if(currentMode == Mode.SEEDFILL){
                   disableSeedFill();
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

        sidebar.lineColorPickBtn.addActionListener(e -> {
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
            lineList = selectedPolygon.getLines();
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
            if(polygonList.size() == 2){
                return;
            }



            int ind = sidebar.polygonPicker.getSelectedIndex();

            if(ind == 0) return;

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

            lineList = selectedPolygon.getLines();
            renderMain();


        });


        sidebar.seedFillBtn.addActionListener( e -> {
            if(selectedPolygon.polygonId == 0) return;

            if(currentMode == Mode.SEEDFILL){
                currentMode = Mode.DRAW;
                sidebar.drawRadioBtn.setSelected(true);
                sidebar.seedFillBtn.setText("Seed Fill");
            }
            else {
                currentMode = Mode.SEEDFILL;
                sidebar.seedFillBtn.setText("Cancel");


            }
        });

        sidebar.fillColorPickBtn.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(sidebar, "Pick Color", new Color(fillColor));
            if(selected != null){
                sidebar.fillColorRect.setBackground(selected);
                fillColor = selected.getRGB();

            }
        });

        sidebar.scanLineFillBtn.addActionListener(e -> {

            if(selectedPolygon.Lines.size() > 2 && selectedPolygon.polygonId != 0){

                selectedPolygon.fillColor = fillColor;
                selectedPolygon.fillSeed = null;
                selectedPolygon.scanLineFill = true;

                renderMain();
            }
        });

        sidebar.removeFillBtn.addActionListener(e ->{
            selectedPolygon.scanLineFill = false;
            selectedPolygon.fillSeed = null;
            selectedPolygon.fillColor = fillColor;
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

    private final Action keyPressedT = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            for(Line l : polygonList.get(1).getLines()){
                System.out.println(l.getP1() + " ----> " + l.getP2());
            }
            System.out.println(cutPolygon.isClockWise());
            System.out.println("------------");


            renderMain(false,true);
        }
    };


    private final Action keyPressedO = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            cropCurrent();
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
                Line n;
                if(lineList.size()!=3){
                    if (edgs == null) return;

                    n = new Line(edgs.getFirst(), edgs.getLast(), lineColor);
                    lineList.remove(toMerge.getFirst());
                    lineList.remove(toMerge.getLast());
                    if(lineList.stream().anyMatch(x -> n.getP1().pointsEqual(x.getP1()) || n.getP2().pointsEqual(x.getP2()))){
                        n.swapPoints();
                    }
                    lineList.add(n);

                }
                else
                {
                    lineList.remove(toMerge.getFirst());
                    lineList.remove(toMerge.getLast());
                }



            }

            renderMain(false,true);


        }
    //endregion

    //region fillFunctions

    private void seedFillAt(MouseEvent event) {


        if(selectedPolygon.Lines.size() < 3 || selectedPolygon.polygonId == 0){
            disableSeedFill();
            return;
        }


        Point seed = getPoint(event);
        Integer seedColor = canvas.getRaster().getPixel(seed.getX(), seed.getY());
        if (selectedPolygon.fillSeed != null && seedColor != null && seedColor == fillColor) {
            selectedPolygon.fillSeed= null;
            renderMain();


            return;
        }


        boolean filled = seedFiller.fill(seed.getX(), seed.getY(),canvas.getBackgroundColor().getRGB(),fillColor);
        if (filled) {
            selectedPolygon.fillSeed = seed;
            selectedPolygon.fillColor = fillColor;
            selectedPolygon.scanLineFill = false;


            renderMain();

        }
    }


    //endregion


    //region cropFunctions
        public void cropCurrent(){
            if(selectedPolygon == cutPolygon) return;

            int in = polygonList.indexOf(selectedPolygon);

            polygonList.set(in,polygonCropper.cropPolygon(polygonList.get(in),cutPolygon));
            renderMain();
        }

    //endregion


    //region utils

    private void disableSeedFill(){
        currentMode = Mode.DRAW;
        sidebar.drawRadioBtn.setSelected(true);
        sidebar.seedFillBtn.setText("Seed Fill");
    }


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


        for(Line l : selectedPolygon.getLines())
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

        for (Line l : selectedPolygon.getLines()){
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


        renderPolygons();


        if(mids) renderMids();
        if(points) renderPoints();






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


        int clr = PREVIEW_COLOR;
        if(selectedPolygon == cutPolygon){
            clr = 0x00FF00;
        }


        Line prwLine = new Line(p1,p2,clr);
        lineRasterizer.rasterize(prwLine);
        canvas.repaint();



    }

    private void previewRender(Point p1, Point p2, Point midP){

        int clr = PREVIEW_COLOR;
        if(selectedPolygon == cutPolygon){
            clr = 0x00FF00;
        }

        Line prwL1 = new Line(p1, midP, clr);
        Line prwL2 = new Line(p2, midP, clr);
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

        float b = 0.1f;
        for(Line l:lineList){

//TODO : currently uses progressivly brighter to indicate orientation, change back to static



            for(int i = -2; i < 3; i++){
                for(int j = -2; j < 3; j++){

                    int x1 =l.getP1().getX()+i;
                    int y1 = l.getP1().getY()+j;
                    Color c =  new Color(0,0,b);

                    canvas.getRaster().setPixel(x1, y1, c.getRGB() );



                    if(lineList.size() == 1){
                        int x2 =l.getP2().getX()+i;
                        int y2 = l.getP2().getY()+j;

                        canvas.getRaster().setPixel(x2, y2,0x0000FF );
                    }


                }
            }
            b += 0.2f;

            if(b > 1) b =1f;



        }
    }



    private void renderPolygons(){

        for (Polygon p : polygonList){

            if(p.polygonId == 0) continue;


            if(p.scanLineFill == true && p.Lines.size() > 2){
                p.fillColor = (p.fillColor == 0) ? fillColor : p.fillColor;
                polygonFiller.fill(p,p.fillColor);
                polygonRasterizer.rasterize(p);
                continue;
            }

            polygonRasterizer.rasterize(p);
            if(p.fillSeed != null && p.Lines.size() > 2){
                p.fillColor = (p.fillColor == 0) ? fillColor : p.fillColor;
                seedFiller.fill(p.fillSeed.getX(), p.fillSeed.getY(), canvas.getBackgroundColor().getRGB(), fillColor);

            }
            else
            {
                p.fillSeed = null;
            }
        }

        polygonRasterizer.rasterize(cutPolygon);





    }

    //endregion
}


