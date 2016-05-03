/**
 * Created by tobias.meier on 26.04.2016.
 */
package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.FeatureExtractor;
import ba.ciel5.featureExtractor.features.IFeatureGroup;
import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class NGramFeatureGroup implements IFeatureGroup {

    @Override
    public Map<String, Double> extract(List<Commit> commits, Version version, CompilationUnit ast, char[] code) {
        Map<String, Double> map = new HashMap<String, Double>();
        List<String> flatCode = new ArrayList<String>();

        ASTVisitor visitor = new ASTVisitor() {

            @Override
            public void preVisit(ASTNode node) {

                //Compilation Unit - skip it
                if ( node.getParent() == null )
                    super.preVisit(node);
                else {
                    flatCode.add(node.getClass().getSimpleName());
                    super.preVisit(node);
                }
            }
        };
        ast.accept(visitor);
        List<String> ngrams = generateNgramsUpto(flatCode, FeatureExtractor.getCfg().getMaxNgramSize());
        for ( String ngram : ngrams ) {
            Double value=1.0;
            if ( map.containsKey(ngram) )
                value = map.get(ngram) + 1;

            map.put(ngram,value);
        }
        return map;
    }

    /**
     *
     * @param flatCode Code List
     * @param maxGramSize should be 1 at least
     * @return set of continuous word n-grams up to maxGramSize from the sentence
     */
    private List<String> generateNgramsUpto(List<String> flatCode, int maxGramSize) {

        List<String> ngrams = new ArrayList<String>();
        int ngramSize = 0;
        StringBuilder sb = null;

        //sentence becomes ngrams
        for (ListIterator<String> it = flatCode.listIterator(); it.hasNext();) {
            String word = (String) it.next();

            //1- add the word itself
            sb = new StringBuilder(word);
            ngrams.add(word);
            ngramSize=1;
            it.previous();

            //2- insert prevs of the word and add those too
            while(it.hasPrevious() && ngramSize<maxGramSize){
                sb.insert(0,'-');
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
