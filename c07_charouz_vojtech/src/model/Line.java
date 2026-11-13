package model;

import java.util.Collections;
import java.util.List;

public class Line {
    private Point p1;
    private Point p2;
    private int Color;

    public Line(Point p1, Point p2, int color) {
        this.p1 = p1;
        this.p2 = p2;
        Color = color;
    }

    public void setColor(int color){
        Color = color;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public int getColor() {
        return Color;
    }

    public Point getMid(){
        int mX = (p1.getX()+p2.getX())/2;
        int mY = (p1.getY()+p2.getY())/2;

        return  new Point(mX,mY);
    }

    public void swapPoints()
    {
        Point p = p1;
        p1 = p2;
        p2 = p;
    }

    public static int YInterpolation(int Y, Line toCheck){
        if(toCheck.p2.getY() > Y && toCheck.p1.getY() > 1) return -1;
        if(toCheck.p2.getY() < Y && toCheck.p1.getY() < 1) return -1;

        Y += 0.5;

        double x = toCheck.p1.getX() + (Y- toCheck.p1.getY()) * (toCheck.p2.getX() - toCheck.p1.getX()) / (toCheck.p2.getY()-toCheck.p1.getY());

        int rsx = (int)Math.ceil(x-0.5);

        return rsx;

    }

    public Point getDirectionVector(){
        return  new Point(p2.getX()-p1.getX(), p2.getY()-p1.getY());
    }


}
