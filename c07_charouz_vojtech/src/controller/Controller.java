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


//region defs

    private Point startPoint;
    private Point currentPoint;
    private Point editSelected;
    List<Line> lineList;
    List<Line> freeLineList;

    private int lineColor = 0x00FF00;
    private static final int PREVIEW_COLOR = 0xFF0000;
    private int fillColor = 0xFF0000;


    public final Canvas canvas;
    private final Sidebar sidebar;
    private final Window window;
    public final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private Polygon cutPolygon;
    private Polygon lineCropPolygon;

    private Polygon selectedPolygon;
    private final List<Polygon> polygonList;
    private int polyIdCounter =1;

    public Mode currentMode;

    private Point seedFillPoint;
    private SeedFiller seedFiller;
    private PolygonFiller polygonFiller;

    private PolygonCropper polygonCropper;
    private Point selectedCropPoint;

    public static Controller mainControl;

//endregion



    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window,Canvas canvas, Sidebar sidebar) {

        mainControl = this;
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
        freeLineList = new ArrayList<>();

        sidebar.polygonPicker.addItem("Polygon 1");
        sidebar.polygonPicker.setSelectedIndex(1);
        lineList = selectedPolygon.Lines;
        currentMode = Mode.DRAW;
        seedFiller = new StackSeedFiller(canvas.getRaster());
        polygonFiller = new ScanlineFiller(canvas.getRaster());
        polygonCropper = new SHPolygonCropper();

        lineCropPolygon = new Polygon();
        initLineCropper();





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

               System.out.println(e.getX() + "//" + e.getY());


               if(currentMode == Mode.DRAW || currentMode == Mode.DRAWFREE){
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
               else if(currentMode == Mode.LINECROP){
                   lineCropMousePress(e);
               }

           }

           @Override
           public void mouseReleased(MouseEvent e) {
               super.mouseReleased(e);


               if(currentMode == Mode.DRAW || currentMode == Mode.DRAWFREE){
                   drawMouseRelease(e);
               }
               else if(currentMode == Mode.EDIT){
                    editMouseRelease(e);
               }
               else if(currentMode == Mode.SEEDFILL){
                   disableSeedFill();
               }
               else if(currentMode == Mode.LINECROP){
                   lineCropMouseRelease(e);
               }

           }

       });

       canvas.addMouseMotionListener(new MouseMotionAdapter() {
           @Override
           public void mouseDragged(MouseEvent e) {
               super.mouseDragged(e);
               if(currentMode == Mode.DRAW || currentMode == Mode.DRAWFREE ){
                   drawMouseDrag(e);
               }
               else if(currentMode == Mode.EDIT){
                    editMouseDrag(e);
               }
               else if(currentMode == Mode.LINECROP){
                   lineCropMouseDrag(e);
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

        sidebar.freeDrawRadioBtn.addActionListener(e->{
            currentMode = Mode.DRAWFREE;
            renderMain(false,false);
        });

        sidebar.lineColorPickBtn.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(sidebar, "Pick Color", new Color(lineColor));
            if(selected != null){
                sidebar.lineColorRect.setBackground(selected);
                lineColor = selected.getRGB();

            }



        });

        sidebar.applyToAllBtn.addActionListener(e -> {

            if(currentMode == Mode.DRAWFREE){
                for(Line l: freeLineList){
                    l.setColor(lineColor);
                }
            }
            else
            {
                for(Line l : lineList){
                    l.setColor(lineColor);
                }
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

        sidebar.lineCropBtn.addActionListener(e ->{
            if(currentMode == Mode.LINECROP){
                // crop
                switchMode();
                sidebar.lineCropBtn.setText("Line Crop");
            }
            else
            {
                currentMode = Mode.LINECROP;
                renderMain();
                sidebar.lineCropBtn.setText("Finish");
            }
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
            crop(1);
            renderMain();
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
            currentMode = Mode.DRAWFREE;
            renderMain(false,false);
            sidebar.freeDrawRadioBtn.setSelected(true);

        }else  if(currentMode == Mode.DRAWFREE || currentMode == Mode.LINECROP){
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
        if(lineList.size() ==1 && currentMode == Mode.DRAW ){
            startPoint = lineList.getLast().getP2();
        }
        else
        {
            startPoint = getPoint(e);
        }



        currentPoint = startPoint;

        renderMain(currentMode == Mode.DRAW,false);
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


        if(currentMode == Mode.DRAWFREE){
            freeLineList.add(new Line(startPoint,getPoint(e), lineColor));
        }
        else if(lineList.size() > 2){
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
        renderMain(currentMode == Mode.DRAW,false);
        drawModePreview();
    }

    //endregion

    //region editModeFunctions

    private void editMousePress(MouseEvent e){

        if(lineList.isEmpty() && freeLineList.isEmpty())
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

        if(lineList.isEmpty() && freeLineList.isEmpty())
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

            if(lineList.isEmpty() &&  freeLineList.isEmpty())
                return;

            Point toDel = getClosestPointInRange(getPoint(e),5);


            if(toDel == null) return;


            List<Line> toMerge = getLinesByCommonPoint(toDel);


            if(toMerge.size() == 1){
                int ind = lineList.indexOf(toMerge.getFirst());

                if(ind == -1){
                    ind = freeLineList.indexOf(toMerge.getFirst());
                    if(ind != -1) freeLineList.remove(ind);
                }
                else {
                    lineList.remove(ind);
                }


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

        public void crop(int index){
            if(polygonList.get(index) == cutPolygon) return;

            polygonList.set(index,polygonCropper.cropPolygon(polygonList.get(index),cutPolygon));
        }

        public void lineCropMousePress( MouseEvent e){

            selectedCropPoint = getClosestPointInRange(getPoint(e),5);


            if(selectedCropPoint == null) return;
            System.out.println(selectedCropPoint);
            renderMain();



        }

        public void lineCropMouseRelease(MouseEvent e){
            selectedCropPoint = null;
            renderMain();
        }

        public void lineCropMouseDrag(MouseEvent e){
            if(selectedCropPoint == null) return;


            if(e.getX() < 0 || e.getX() > canvas.getWidth() || e.getY() < 0 || e.getY() > canvas.getHeight() )
            {
                renderMain(false,true);
                return;
            }



            int x = e.getX();
            int y =e.getY();



            List<Line> com = getLinesByCommonPoint(selectedCropPoint);
            if(com.size() != 2) return;
            Point sharedX = null;
            Point sharedY = null;
            for(Line l : com){




                if(l.getP1() == selectedCropPoint){
                    if(l.getP2().getY() == selectedCropPoint.getY()){
                        sharedY = l.getP2();
                    }
                    else if(l.getP2().getX() == selectedCropPoint.getX()){
                        sharedX = l.getP2();
                    }
                }
                else
                {
                    if(l.getP1().getY() == selectedCropPoint.getY()){
                        sharedY = l.getP1();
                    }
                    else if(l.getP1().getX() == selectedCropPoint.getX()){
                        sharedX = l.getP1();
                    }
                }
            }

            if(sharedX == null || sharedY == null) return;

            if(getLenght(selectedCropPoint, new Point(x,sharedX.getY())) < 10) return;
            if(getLenght(selectedCropPoint, new Point(sharedY.getX(),y)) < 10) return;


            selectedCropPoint.setX(x);
            sharedX.setX(x);
            sharedY.setY(y);
            selectedCropPoint.setY(y);
            System.out.println(selectedCropPoint);


            renderMain(false,false);



        }

    //endregion


    //region utils

    private void disableSeedFill(){
        currentMode = Mode.DRAW;
        sidebar.drawRadioBtn.setSelected(true);
        sidebar.seedFillBtn.setText("Seed Fill");
    }

    public double getLenght(Point p1, Point p2){
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();

        return Math.sqrt(dx*dx+dy*dy);
    }


    private List<Line> getLinesByCommonPoint(Point com){

        List<Line> allLines = new ArrayList<>();
        if(currentMode == Mode.LINECROP){
            allLines = lineCropPolygon.getLines();
        }
        else {
            allLines.addAll(lineList);
            allLines.addAll(freeLineList);
        }

        return allLines.stream().filter(x -> x.getP1().pointsEqual(com) || x.getP2().pointsEqual(com)).toList();
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

        List<Line> allLines = new ArrayList<>();
        if(currentMode == Mode.LINECROP){
            allLines = lineCropPolygon.getLines();
        }
        else {
            allLines.addAll(lineList);
            allLines.addAll(freeLineList);
        }


        for (Line l : allLines){
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

    private void initLineCropper(){
        int e = canvas.getWidth()-100;
        Point p1 = new Point(100,100);
        Point p2 = new Point(e,100);
        Point p3 = new Point(100,500);
        Point p4 =new Point(e,500);

        lineCropPolygon.Lines.add(new Line(p1,p2,0xFF0000));
        lineCropPolygon.Lines.add(new Line(p3,p1,0xFF0000));
        lineCropPolygon.Lines.add(new Line(p2,p4,0xFF0000));
        lineCropPolygon.Lines.add(new Line(p4,p3,0xFF0000));

    }

    //endregion

    //region render

    public void renderMain(){
        if(currentMode == Mode.EDIT || currentMode == Mode.DELETE){
            renderMain(false,true);
        }
        else
        {
            renderMain(false,false);
        }
    }


    public void renderMain(boolean mids, boolean points){
        canvas.clear();


        if(currentMode == Mode.LINECROP){
            renderCropWindow();
        }
        else
        {
            renderGrid();
        }


        renderPolygons();
        renderLines();


        if(mids) renderMids();
        if(points) renderPoints();

        canvas.repaint();

    }

    private void renderCropWindow(){
        polygonRasterizer.rasterize(lineCropPolygon);
        for(Line l: lineCropPolygon.getLines()){
            Point p = l.getP1();
            drawPoint(p.getX(),p.getY(),0x0000FF);
        }
    }

    private void renderLines(){
        for(Line l: freeLineList){
            lineRasterizer.rasterize(l);
        }
    }



    private void renderGrid(){
        List<Line> gl = new ArrayList<>();

        final int gcolor = 0x474747;
        for(int i = 1; i <8; i++){
            gl.add(new Line(new Point(i*100,0),new Point(i*100,600),gcolor));
        }

        for(int j = 1; j < 6; j++){
            gl.add(new Line(new Point(0,j*100),new Point(800,j*100),gcolor));
        }

        for(Line l : gl){
            lineRasterizer.rasterize(l);
        }


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


        if(lineList.isEmpty() || currentMode == Mode.DRAWFREE){
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

    private void drawPoint(int x, int y, int clr){
        for(int i = -2; i < 3; i++){
            for(int j = -2; j < 3; j++){

                int xmx =x+i;
                int ymy = y+j;

                if(xmx < 0 || xmx > canvas.getWidth() || ymy<0 || y > canvas.getHeight()){
                    continue;
                }


                canvas.getRaster().setPixel(xmx, ymy,0x0000FF );
            }
        }
    }


    private void renderMids(){
        for(Line l:lineList){


            Point mid = l.getMid();
            drawPoint(mid.getX(), mid.getY(),0x0000FF  );

        }

        for(Line l: freeLineList){
            Point mid = l.getMid();
            drawPoint(mid.getX(), mid.getY(),0x0000FF  );
        }
    }



    private void renderPoints(){

        float b = 0.1f;
        for(Line l:lineList){


            Color c =  new Color(0,0,b);
            drawPoint(l.getP1().getX(),l.getP1().getY(),c.getRGB());
            b += 0.2f;

            if(b > 1) b =1f;


        }
        if(lineList.size() <3 && !lineList.isEmpty()){
            Point p = lineList.getLast().getP2();
            drawPoint(p.getX(),p.getY(),0x0000FF  );
        }

        for(Line l: freeLineList){
            drawPoint(l.getP1().getX(),l.getP1().getY(),0x0000FF  );
            drawPoint(l.getP2().getX(),l.getP2().getY(),0x0000FF  );
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


