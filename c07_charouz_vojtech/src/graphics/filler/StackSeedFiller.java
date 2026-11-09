package graphics.filler;

import graphics.Raster;

import java.util.ArrayDeque;
import java.util.Deque;

public class StackSeedFiller implements SeedFiller{
    private Raster raster;

    public StackSeedFiller(Raster raster){
        this.raster = raster;
    }

    @Override
    public boolean fill(int x, int y, int backgroundColor, int fillColor) {
        if(backgroundColor == fillColor || raster.getPixel(x, y) == null || raster.getPixel(x,y) != backgroundColor){
            return false;
        }

        int width = raster.getWidth();
        Deque<Integer> pixel = new ArrayDeque<>();
        enqueue(x,y,width,backgroundColor,fillColor,pixel);

        while (!pixel.isEmpty()){
            int index = pixel.removeLast();
            int lx = index%width;
            int ly = index/width;

            enqueue(lx-1,ly,width,backgroundColor,fillColor,pixel);
            enqueue(lx+1,ly,width,backgroundColor,fillColor,pixel);
            enqueue(lx,ly-1,width,backgroundColor,fillColor,pixel);
            enqueue(lx,ly+1,width,backgroundColor,fillColor,pixel);

        }

        return true;
    }

    private void  enqueue(int x,int y, int width,int backgroundColor, int fillColor, Deque<Integer> pixel){
        Integer color = raster.getPixel(x,y);

        if(color != null && color == backgroundColor){
            raster.setPixel(x,y,fillColor);
            pixel.addLast(y*width+x);
        }
    }
}
