package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Polygon {

    public List<Line> Lines;

    public int polygonId;

    public Polygon(List<Line> lines){
        Lines = lines;
    }

    public Polygon(){
        this(new ArrayList<Line>());
    }

    public List<Line> getLines(){
        return Lines;
    }

    public void orderSelf(){

        if(Lines.size() < 3) return;

        Point next = Lines.getFirst().getP2();

        for(int i =1; i < Lines.size()-1; i++){
            final Point t = next;

            int toSwap = Lines.indexOf(Lines.stream().filter(x-> x.getP1() == t).toList().getFirst());
            Collections.swap(Lines,toSwap,i);
            next = Lines.get(i).getP2();
        }

    }

    public boolean isClockWise(){
        orderSelf();

        List<Point> points = new ArrayList<>();

        for(Line l: Lines){
            points.add(l.getP1());
        }

        int min = points.stream().mapToInt(Point::getY).min().orElse(-1);
        if(min == -1) throw new RuntimeException("polygon.isClockWise min point error");

        Point A = points.stream().filter(x->x.getY() == min).max(Comparator.comparingInt(Point::getX)).orElse(null);
        if(A == null) throw new RuntimeException("polygon.isClockWise point A error");

        Line AB = Lines.stream().filter(x-> x.getP2().pointsEqual(A)).findFirst().orElse(null);
        Line AC = Lines.stream().filter(x-> x.getP1().pointsEqual(A)).findFirst().orElse(null);
        if(AB == null || AC == null) throw new RuntimeException("polygon.isClockWise AB/AC Line error");

        Point vAB = new Point(AB.getP2().getX()-AB.getP1().getX(),AB.getP2().getY()-AB.getP1().getY());
        Point vAC = new Point(AC.getP2().getX()-AC.getP1().getX(),AC.getP2().getY()-AC.getP1().getY());

        float productSign = Math.signum((vAB.getX()*vAC.getY()) - (vAB.getY()*vAC.getX()));

        return productSign > 0;


    }

    public void addMissing(){

        List<Line> toAdd = new ArrayList<>();
        for(int i =0; i < Lines.size(); i++){
            int n = (i+1 >= Lines.size()) ? 0 : i+1;
            if(!Lines.get(i).getP2().pointsEqual(Lines.get(n).getP1())){
                toAdd.add(new Line(Lines.get(i).getP2(),Lines.get(n).getP1(),Lines.get(n).getColor()));
            }

        }

        Lines.addAll(toAdd);
        orderSelf();
    }

    public int fillColor;

    public int lineOverrideColor = -1;

    public Point fillSeed;

    public boolean scanLineFill;

}
