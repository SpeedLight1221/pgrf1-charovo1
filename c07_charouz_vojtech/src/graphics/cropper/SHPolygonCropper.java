package graphics.cropper;

import model.Line;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class SHPolygonCropper implements PolygonCropper {

    @Override
    public Polygon cropPolygon(Polygon toCrop, Polygon cropBy) {

        Polygon.OrderPolygon(toCrop);
        boolean isClockwise = cropBy.isClockWise();

        Polygon result = new Polygon((toCrop.getLines()));
        List<Line> current;

        for(Line l : toCrop.getLines()){
            System.out.println("TC " + l.getP1() + " ----> " + l.getP2());
        }

        for(Line cl : cropBy.getLines()){
            Point cA = cl.getP1();
            Point cB = cl.getP2();




            current = new ArrayList<>(result.getLines());
            result.Lines.clear();

//todo certain polygons end up "webbed" also once had a nulllptr exeption, not yet reproduced though
            for(Line l: current){


                double prod1 = Point.crossProduct(cA,cB,l.getP1());
                double prod2 = Point.crossProduct(cA,cB,l.getP2());

                if(!isClockwise){
                    prod1 *= -1;
                    prod2 *= -1;
                }

                System.out.println("prod 1 " + prod1+ " at: " + l.getP1().getY());
                System.out.println("prod 2 " + prod2 + " at: " + l.getP2().getY());

                if(prod1 < 0 && prod2 < 0){// both endpoints are outside, line gets ignored
                    continue;
                }
                else if (prod1 < 0 || prod2 < 0) {
                    Point cutDirectionVector = cl.getDirectionVector();
                    Point lDirectionVector = l.getDirectionVector();



                    int den = (cutDirectionVector.getX()*lDirectionVector.getY()) - (cutDirectionVector.getY()*lDirectionVector.getX());
                    if(den == 0){
                        result.Lines.add(l);
                        continue;
                    }

                    int x1 = l.getP1().getX();
                    int x3 = cl.getP1().getX();
                    int y1 = l.getP1().getY();
                    int y3 = cl.getP1().getY();



                    double t = (double) (((x3 - x1) * lDirectionVector.getY()) - ((y3 - y1) * lDirectionVector.getX())) /den;

                    t *= -1;


                    int intX = (int)(Math.round(cl.getP1().getX() + t* cutDirectionVector.getX()));
                    int intY = (int)(Math.round(cl.getP1().getY() + t* cutDirectionVector.getY()));
                    System.out.println("cur " + t + " x " + intX + " y " + intY);

                    Line n;
                    if(prod1 < 0){
                        n = new Line(new Point(intX,intY),l.getP2(),l.getColor());

                    }
                    else
                    {
                        n = new Line(l.getP1(),new Point(intX,intY),l.getColor());
                    }
                    result.Lines.add(n);


                }
                else
                {
                    result.Lines.add(l);
                }




            }



            result.addMissing();


        }
        toCrop.Lines = result.Lines;
        return toCrop;


    }



    //region unused
    private void findIslands(Polygon res){


        class ILine{
            public ILine(int i, Line l){
                Island=i;
                line =l;
            }

            public int Island;
            public Line line;
        }

        List<Line> Lines = res.getLines();

        List<ILine> islands = new ArrayList<>();
        int lastIs = 0;
        boolean[] checked = new boolean[Lines.size()];

        linloop:
        for(Line l : Lines){
            int ind = Lines.indexOf(l);
            if (checked[ind] == true) continue;// if this line was already processed, skip

            //check if it doesnt connect to an alrady existing island

            for(ILine il : islands){
                if(il.line.getP1().pointsEqual(l.getP2())){
                    islands.add(new ILine(il.Island,l));
                    checked[ind] = true;
                    continue linloop;
                }
            }

            //if not, add it to a new island and find all connecting

            islands.add(new ILine(lastIs,l));
            checked[ind] = true;
            Line last = l;
            while (true){
                final Line el = last;
                Line n = Lines.stream().filter(x-> x.getP1().pointsEqual(el.getP2())).findFirst().orElse(null);

                if(n == null){
                    break;
                }

                islands.add(new ILine(lastIs,n));
                checked[Lines.indexOf(n)] = true;
                last = n;

                for(boolean b : checked){
                    if(b == false){
                        continue;
                    }
                }
                break;





            }


            lastIs++;
        }



    }
    //endregion
}
