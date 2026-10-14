package graphics.rasterizer;

import graphics.Raster;
import model.Line;
import model.Polygon;

import java.util.List;

public class PolygonOutlineRasterizer implements PolygonRasterizer {

    private final LineRasterizer lRast;

    public PolygonOutlineRasterizer(LineRasterizer lr){
        lRast = lr;
    }

    @Override
    public void rasterize(Polygon polygon) {

        List<Line> ls = polygon.GetLines();

        if(ls.size() == 2)
        {
            Line l1 = ls.getFirst();
            Line l2 =ls.getLast();

            ls.add(new Line(l1.getP1(),l2.getP2(),l2.getColor()));
        }


        for(Line l : ls)
        {
            lRast.rasterize(l);
        }


    }
}
