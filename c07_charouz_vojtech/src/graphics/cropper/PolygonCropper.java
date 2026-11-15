package graphics.cropper;

import model.Polygon;

public interface PolygonCropper {
    Polygon cropPolygon(Polygon toCrop, Polygon cropBy);
}
