package graphics.cropper;

import model.Line;
import model.Polygon;

import java.util.List;

public interface LineCropper {
    public List<Line> cropLines(List<Line> toCrop, Polygon cropBy);
}
