package graphics.filler;

import graphics.Raster;
import model.Polygon;

public interface PatternFiller {
    public void fill(Polygon toFill, Raster pattern);
}
