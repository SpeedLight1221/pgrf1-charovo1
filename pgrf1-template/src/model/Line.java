package model;

public class Line {
    private final Point p1;
    private final Point p2;
    private final int Color;

    public Line(Point p1, Point p2, int color) {
        this.p1 = p1;
        this.p2 = p2;
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
}
