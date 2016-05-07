/**
 * Created by tobias.meier on 30.04.2016.
 */
package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.FeatureExtractor;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.ngramfeatures.NGramFeatureGroup;
import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class NGramFeatureGroupTest {
    @org.junit.Test
    public void extract() throws Exception {
        NGramFeatureGroup feature = new NGramFeatureGroup();
        char[] code = TestClass1.getTestCode().toCharArray();
        CompilationUnit ast = AbstractSyntaxTreeUtil.parse(code);
        Map<Integer,Map<Integer,Map<String, Integer>>> result = feature.extract(null, null, ast, code);

        assertEquals(13.0, result.get(1).get(1).get("21"), 0.0);
        assertEquals(21.0, result.get(1).get(1).get("22"), 0.0);
        assertEquals(19.0, result.get(1).get(2).get("22_52"), 0.0);
        assertEquals(11.0, result.get(1).get(3).get("52_42_42"), 0.0);

        Version version = new Version("testId");
        Map<Version, Map<Integer,Map<Integer,Map<String, Integer>>>> versionNGram = new HashMap<Version, Map<Integer,Map<Integer,Map<String, Integer>>>>();
        versionNGram.put(version, result);
        Map<Integer,Map<Integer,List<String>>> nGramHeadMap = FeatureExtractor.generateNGramHead(versionNGram);

        assertEquals(31.0, nGramHeadMap.get(1).get(1).size(), 0.0);
        assertEquals(40.0, nGramHeadMap.get(2).get(3).size(), 0.0);
        assertEquals(5.0, nGramHeadMap.get(4).get(1).size(), 0.0);
    }
}