package graphics.filler;

import graphics.Raster;

public class RecursiveSeedFiller implements SeedFiller{

    private final Raster raster;

    public RecursiveSeedFiller(Raster raster){
        this.raster = raster;
    }


    @Override
    public boolean fill(int x, int y, int backgroundColor, int fillColor) {

        if(backgroundColor == fillColor || raster.getPixel(x, y) == null || raster.getPixel(x,y) != backgroundColor){
            return false;
        }

        fillRegion(x,y,backgroundColor,fillColor);

        return true;
    }

    private void fillRegion(int x, int y, int backgroundColor, int fillColor){


        Integer color = raster.getPixel(x,y);


        if(color == null) return;

        if(color != backgroundColor) return;


        raster.setPixel(x,y,fillColor);

        fillRegion(x-1,y,backgroundColor,fillColor);
        fillRegion(x+1,y,backgroundColor,fillColor);
        fillRegion(x,y-1,backgroundColor,fillColor);
        fillRegion(x,y+1,backgroundColor,fillColor);



    }

}
