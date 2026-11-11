package graphics.cropper;

import model.Line;
import model.Point;
import model.Polygon;

public interface PolygonCropper {
    public Polygon cropPolygon(Polygon toCrop, Polygon cropBy);
}
