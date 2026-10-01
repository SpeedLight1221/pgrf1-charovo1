package graphics;

import java.awt.*;

public interface Raster {
    void present(Graphics graphics);
    void setPixel(int x, int y, int rgb);
    void clear(int rgb);

    int getWidth();
    int getHeight();
}
