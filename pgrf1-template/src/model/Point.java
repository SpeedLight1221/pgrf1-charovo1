package model;

public class Point {
    private final int x;
    private final int y;

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
}
