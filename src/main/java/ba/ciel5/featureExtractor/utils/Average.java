package ba.ciel5.featureExtractor.utils;

import java.util.List;

/**
 * Created on 12.04.2016.
 * math help function
 * @author ymeke
 */
public class Average {

    /**
     * calculate median from a list
     * @param list integers
     * @return median
     */
    public static double getMedianFromIntegers(List<Integer> list) {
        int middle = list.size() / 2;
        if (list.size() % 2 == 0)
            return ((list.get(middle - 1) + list.get(middle)) / 2.0);
        else
            return list.get(middle);
    }

    /**
     * calculate median from a list
     * @param list doubles
     * @return median
     */
    public static double getMedianFromDoubles(List<Double> list) {
        int middle = list.size() / 2;
        if (list.size() % 2 == 0)
            return ((list.get(middle - 1) + list.get(middle)) / 2.0);
        else
            return list.get(middle);
    }
}
