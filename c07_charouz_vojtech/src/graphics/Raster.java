package graphics;

import java.awt.*;

public interface Raster {
    void present(Graphics graphics);
    void setPixel(int x, int y, int rgb);
    Integer getPixel(int x,int y);
    void clear(int rgb);

    int getWidth();
    int getHeight();
}
