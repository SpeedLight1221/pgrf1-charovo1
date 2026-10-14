package graphics.rasterizer;

import graphics.Raster;
import model.Line;

/**
* Třída pro vykreslování úsečky do {@link graphics.Raster} pomocí Triviálního Algoritmu
*
 */


public class TrivialLineRasterizer implements LineRasterizer {



    private final Raster raster;

    /**
    * Vytvoří instanci třídy {@link graphics.rasterizer.TrivialLineRasterizer}
    * @param raster {@link graphics.Raster} do kterého se má úsečka vykreslovat
     */
    public TrivialLineRasterizer(Raster raster) {
        this.raster = raster;
    }



    /**
     * Vykreslí čáru podle objektu typu {@link model.Line}
     * @param line Objekt typu {@link model.Line} pro vykreslení
     */
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



    /**
    * Vykreslí čáru podle souřadnice prvního a konečného bodu ve vybrané barvě
    * @param x1 Souřadnice x prvního bodu
    * @param y1 Souřadnice y prvního bodu
    * @param x2 Souřadnice x konečného bodu
    * @param y2 Souřadnice y konečného bodu
    * @param rgb barva úsečky
     */
    public void rasterize(int x1,int y1,int x2, int y2,int rgb){



        int dx =(x2-x1);
        int dy =(y2-y1);



        float k = dy/(float)dx;
        float q = y1-(k * x1);

        int itStart = x1;
        int itEnd = x2;
        if(Math.abs(dy) > Math.abs(dx))
        {
            //Pokud je osa Y dominatní, bude algoritmus postupovat podle ní pro zamezení degenerované čáry
            itStart = y1;
            itEnd = y2;
        }

        if(itStart > itEnd){
            //Pokud se první bod nachází dále od počátku než bod konečný, body se prohodí
            int t = itStart;
            itStart = itEnd;
            itEnd = t;
        }





        for(int i = itStart; i<= itEnd; i++)
        {

            if(Math.abs(dy) > Math.abs(dx))// vykreslování podle osy Y
            {
                float x = (i-q)/k;
                if(dx == 0){// Ošetření vertikální čáry
                    x = x1;
                }
                raster.setPixel(Math.round(x),i,rgb);
            }
            else // vykreslovaní podle osy X
            {
                float y = k * i + q;
                raster.setPixel(i,Math.round(y),rgb);
            }
        }
    }
}
