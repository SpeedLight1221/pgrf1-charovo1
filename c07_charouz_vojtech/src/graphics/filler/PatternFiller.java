package graphics.filler;

import graphics.Raster;
import model.Polygon;

public interface PatternFiller {
    void fill(Polygon toFill, Raster pattern);
}
