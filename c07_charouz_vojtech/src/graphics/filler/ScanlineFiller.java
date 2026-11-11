package graphics.filler;

import graphics.Raster;
import model.Line;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class ScanlineFiller implements PolygonFiller{

    private final Raster raster;
    public ScanlineFiller(Raster raster){
        this.raster = raster;
    }

    @Override
    public void fill(Polygon polygon, int fillColor) {
        List<Line> edges = new ArrayList<>(polygon.getLines());
        if(edges.size() < 2) {return;}

        Line last = edges.getLast();
        Line first = edges.getFirst();


        //
        int maxY = 0;
        int minY = raster.getHeight();
        for(int i =0; i < edges.size(); i++){// orient all eges to go from top to bottom
            Line l = edges.get(i);
            Point p1 = l.getP1();
            Point p2 = l.getP2();
            if(p1.getY() > p2.getY()){
                Point p = p1;
                p1 = p2;
                p2 = p;
                edges.set(i,new Line(p1,p2,l.getColor()));
            }

            minY = Math.min(minY,p1.getY());// find the smallest and biggest Y
            maxY = Math.max(maxY,p2.getY());

        }


        List<Double> intersections = new ArrayList<>(); // List of X coords where intersections happen, per Y level

        //for loop, using math max and min to ensure not going outside raster bounds
        for(int y = Math.max(0,minY); y < Math.min(raster.getHeight(),maxY);y++){
            intersections.clear();
            double scanY = y+0.5;

            for(Line edge : edges){
                Point p1 = edge.getP1();
                Point p2 = edge.getP2();
                if(y < p1.getY() || y >= p2.getY()){// skip if edge doesn't intersect current Y( because p1 is always top and p2 bottom)
                    continue;
                }

                double x = p1.getX() + (scanY- p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY()-p1.getY());// Hledání průsečíku linearní interpolací
                intersections.add(x);
            }


            intersections.sort(null);
            for(int i = 0; i + 1 < intersections.size(); i+=2){ // Always an even number of intersections, because even in corners it adds one for both lines
                // zaokrouhlime x souradnici odkud mame jit
                int from = Math.max(0, (int) Math.ceil(intersections.get(i)-0.5));
                // zaokrouhlime x kam mame jit
                int to = Math.min(raster.getWidth(), (int) Math.ceil(intersections.get(i+1)-0.5));
                //projdeme od from do to a obarvime

                for(int x = from; x < to; x++){
                    raster.setPixel(x,y,fillColor);
                }

            }
        }

    }
}
