package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private  final List<Line> Lines;

    public Polygon(List<Line> lines){
        Lines = lines;
    }

    public Polygon(){
        this(List.of());
    }

    public List<Line> GetLines(){
        return Lines;
    }

}
