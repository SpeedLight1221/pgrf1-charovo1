package graphics.rasterizer;

import graphics.Raster;
import model.Line;

public class TrivialLineRasterizer implements LineRasterizer {

    private final Raster raster;

    public TrivialLineRasterizer(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void rasterize(Line line) {
        rasterize(
                line.getP1().getX(),
                line.getP1().getY(),
                line.getP2().getX(),
                line.getP2().getY(),
                line.getColor()
        );
    }

    public void rasterize(int x1,int y1,int x2, int y2,int rgb){
        float k = (y2-y1)/(float)(x1-x2);
        float q = y1-k * x1;

        for(int x = x1; x<= x2; x++)
        {
            float y = k * x + q;
            raster.setPixel(x,Math.round(y),rgb);
        }
    }
}
