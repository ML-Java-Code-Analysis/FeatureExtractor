package ba.ciel5.featureExtractor.utils;

import java.util.List;

/**
 * Created on 16.04.2016.
 *
 * @author tobias.meier
 */
public class ListUtil {
    /**
     * Helper function to sum up a list
     * @param list with integers
     * @return sum of all list elements
     */
    public static int sum(List<Integer> list) {
        if (list == null || list.size() < 1)
            return 0;

        int sum = 0;
        for (Integer i : list)
            sum = sum + i;

        return sum;
    }
}
