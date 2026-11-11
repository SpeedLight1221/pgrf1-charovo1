package model;

import java.awt.*;
import java.util.Random;

public class Point {


    private int x;
    private int y;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;

        Random rnd = new Random(x+y);
        float r = rnd.nextFloat();
        float g = rnd.nextFloat();
        float b = rnd.nextFloat();
        debugColor = (new Color(r,g,b)).getRGB();


    }

    public double pointDistance(Point p){

        int dX = (int)Math.pow(x-p.getX(),2);
        int dY = (int)Math.pow(y-p.getY(),2);

        return Math.sqrt(dX+dY);
    }

    public String printC(){
        return getX() + " | " +getY();
    }


    @Override
    public String toString() {
        return  x + " | " + y + "(" + Integer.toHexString(debugColor) + ")";
    }

    public boolean pointsEqual(Point tc){
        if(tc.getX() == this.x && tc.getY() == y) return  true;

        return false;
    }

    public int debugColor;

    public static float crossProduct(Point A, Point B, Point M){
        return Math.signum((B.getX()- A.getX())*(M.getY()- A.getY())-(B.getY()- A.getY())*(M.getX()- A.getX()));
    }
}
