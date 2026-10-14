package model;

public class Line {
    private final Point p1;
    private final Point p2;
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
}
