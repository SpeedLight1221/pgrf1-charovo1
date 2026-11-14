package graphics.cropper;

import model.Line;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class CSLineCropper implements LineCropper{
    @Override

    /*
        1
    8 | 0 | 4
        2
     */

    public List<Line> cropLines(List<Line> toCrop, Polygon cropBy) {

        if(cropBy.getLines().size() != 4) return null;
        List<Line> cropLines = cropBy.getLines();



        System.out.println("cl");
        int xmin = cropLines.stream().mapToInt(x->x.getP1().getX()).min().orElse(-1);
        int xmax  = cropLines.stream().mapToInt(x->x.getP1().getX()).max().orElse(-1);
        int ymin  = cropLines.stream().mapToInt(x->x.getP1().getY()).min().orElse(-1);
        int ymax =  cropLines.stream().mapToInt(x->x.getP1().getY()).max().orElse(-1);

        List<Line> res = new ArrayList<>();

        for(Line l : toCrop){


            System.out.println("cls");
            int sp1 = getPointScore(l.getP1(),xmin,xmax,ymin,ymax);
            int sp2 = getPointScore(l.getP2(),xmin,xmax,ymin,ymax);

            if((sp1 | sp2) == 0){
                res.add(l);
                continue;
            }

            if((sp1 & sp2) != 0){
                continue;
            }




            if((sp1 & (1)) == 1) {
                int nx = Line.YInterpolation(ymin,l);
                l.getP1().setY(ymin);
                l.getP1().setX(nx);
                sp1 = getPointScore(l.getP1(),xmin,xmax,ymin,ymax);
                //above ymin;
            }
            else if((sp1 & (1 << 1)) == 2){
                int nx = Line.YInterpolation(ymax,l);
                l.getP1().setY(ymax);
                l.getP1().setX(nx);
                sp1 = getPointScore(l.getP1(),xmin,xmax,ymin,ymax);
                //below ymax;
            }

            if(((sp1 & (1 << 2)) == 4)){
                // beyond xmax
                int ny = Line.XInterpolation(xmax,l);
                l.getP1().setX(xmax);
                l.getP1().setY(ny);
            }
            else if (((sp1 & (1<<3)) == 8)){
                // before xmin
                int ny = Line.XInterpolation(xmin,l);
                l.getP1().setX(xmin);
                l.getP1().setY(ny);
            }
            
            // Point 2

            if((sp2 & (1)) == 1) {
                int nx = Line.YInterpolation(ymin,l);
                l.getP2().setY(ymin);
                l.getP2().setX(nx);
                sp2 = getPointScore(l.getP2(),xmin,xmax,ymin,ymax);
                //above ymin;
            }
            else if((sp2 & (1 << 1)) == 2){
                int nx = Line.YInterpolation(ymax,l);
                l.getP2().setY(ymax);
                l.getP2().setX(nx);
                sp2 = getPointScore(l.getP2(),xmin,xmax,ymin,ymax);
                //below ymax;
            }

            if(((sp2 & (1 << 2)) == 4)){
                // beyond xmax
                int ny = Line.XInterpolation(xmax,l);
                l.getP2().setX(xmax);
                l.getP2().setY(ny);
            }
            else if (((sp2 & (1<<3)) == 8)){
                // before xmin
                int ny = Line.XInterpolation(xmin,l);
                l.getP2().setX(xmin);
                l.getP2().setY(ny);
            }
            res.add(l);


        }

        return res;




    }

   

    private int getPointScore(Point p1, int xmin, int xmax, int ymin, int ymax){
        int score =0;
        if(p1.getX() < xmin){
            score |= 8;
        }
        else if(p1.getX() > xmax){
            score |=4;
        }

        if(p1.getY() < ymin){
            score |=1;
        }
        else if(p1.getY() > ymax){
            score |=2;
        }

        return score;
    }
}
