package model;

import java.util.ArrayList;
import java.util.List;

public class QSort {


    public static List<Double> sort(List<Double> list) {
        if (list.size() <= 1) return list;

        int m = list.size() / 2;
        List<Double> sorted = new ArrayList<>();
        sorted.add(list.get(m));
        int im = 0;

        for (int i = 0; i < list.size(); i++) {
            if (i == m) continue;

            if (list.get(i) <= list.get(m)) {
                sorted.add(im, list.get(i));
                im++;
            } else {
                sorted.add(im + 1, list.get(i));

            }

        }


        List<Double> res = new ArrayList<>();

        res.addAll(sort(sorted.subList(0, m)));
        res.addAll(sort(sorted.subList(m, sorted.size())));

        return res;


    }


}
