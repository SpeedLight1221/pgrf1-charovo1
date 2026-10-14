package model;

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
    }

    public double pointDistance(Point p){

        int dX = (int)Math.pow(x-p.getX(),2);
        int dY = (int)Math.pow(y-p.getY(),2);

        return Math.sqrt(dX+dY);
    }

    public String printC(){
        return getX() + " | " +getY();
    }



}
