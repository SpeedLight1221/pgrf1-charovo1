package model;

import java.util.ArrayList;
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

    public List<Line> GetLines(){
        return Lines;
    }

}
