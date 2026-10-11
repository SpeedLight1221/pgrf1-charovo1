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


        int dx =(x2-x1);
        int dy =(y2-y1);



        float k = dy/(float)dx;
        float q = y1-(k * x1);

        int itStart = x1;
        int itEnd = x2;
        if(Math.abs(dy) > Math.abs(dx))
        {
            itStart = y1;
            itEnd = y2;
        }

        if(itStart > itEnd){
            int t = itStart;
            itStart = itEnd;
            itEnd = t;
        }





        for(int i = itStart; i<= itEnd; i++)
        {

            if(Math.abs(dy) > Math.abs(dx))
            {
                float x = (i-q)/k;
                if(dx == 0){
                    x = x1;
                }
                raster.setPixel(Math.round(x),i,rgb);
            }
            else
            {
                float y = k * i + q;
                raster.setPixel(i,Math.round(y),rgb);
            }
        }
    }
}
