package ba.ciel5.featureExtractor.utils;

import java.util.List;

/**
 * Created on 12.04.2016.
 *
 * @author ymeke
 */
public class Average {

    public static double getMedianFromIntegers(List<Integer> list) {
        int middle = list.size() / 2;
        if (list.size() % 2 == 0)
            return (list.get(middle - 1) + list.get(middle) / 2.0);
        else
            return list.get(middle);
    }

    public static double getMedianFromDoubles(List<Double> list) {
        int middle = list.size() / 2;
        if (list.size() % 2 == 0)
            return (list.get(middle - 1) + list.get(middle) / 2.0);
        else
            return list.get(middle);
    }
}
