package graphics.cropper;

import model.Line;
import model.Polygon;

import java.util.List;

public interface LineCropper {
    List<Line> cropLines(List<Line> toCrop, Polygon cropBy);
}
