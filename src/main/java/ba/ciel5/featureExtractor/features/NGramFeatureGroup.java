/**
 * Created by tobias.meier on 26.04.2016.
 */
package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.model.Version;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.*;

public class NGramFeatureGroup implements IFeatureGroup {

    @Override
    public Map<String, Double> extract(Version version, CompilationUnit ast, char[] code) {

        Map<String, Double> map = new HashMap<String, Double>();
        //map.put("WMC", weightedMethodsPerClassPerFile);
        return map;
    }

    /**
     *
     * @param str should has at least one string
     * @param maxGramSize should be 1 at least
     * @return set of continuous word n-grams up to maxGramSize from the sentence
     */
    private List<String> generateNgramsUpto(String str, int maxGramSize) {

        List<String> sentence = Arrays.asList(str.split("[\\W+]"));

        List<String> ngrams = new ArrayList<String>();
        int ngramSize = 0;
        StringBuilder sb = null;

        //sentence becomes ngrams
        for (ListIterator<String> it = sentence.listIterator(); it.hasNext();) {
            String word = (String) it.next();

            //1- add the word itself
            sb = new StringBuilder(word);
            ngrams.add(word);
            ngramSize=1;
            it.previous();

            //2- insert prevs of the word and add those too
            while(it.hasPrevious() && ngramSize<maxGramSize){
                sb.insert(0,' ');
                sb.insert(0,it.previous());
                ngrams.add(sb.toString());
                ngramSize++;
            }

            //go back to initial position
            while(ngramSize>0){
                ngramSize--;
                it.next();
            }
        }
        return ngrams;
    }
}
