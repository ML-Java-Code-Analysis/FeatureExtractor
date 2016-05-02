package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created on 15.04.2016.
 *
 * @author tobias.meier
 */
public class LengthOfNamesFeatureGroupTest {

    @org.junit.Test
    public void extract() throws Exception {
        LengthOfNamesFeatureGroup feature = new LengthOfNamesFeatureGroup();
        char[] code = TestClass1.getTestCode().toCharArray();
        CompilationUnit ast = AbstractSyntaxTreeUtil.parse(code);
        Map<String, Double> result = feature.extract(null, null, ast, code);

        assertEquals(1.0, result.get("MINVAR"), 0.0);
        assertEquals(9.0, result.get("MAXVAR"), 0.0);
        assertEquals(4.5, result.get("MEDVAR"), 0.0);

        assertEquals(5.0, result.get("MINMET"), 0.0);
        assertEquals(9.0, result.get("MAXMET"), 0.0);
        assertEquals(7.0, result.get("MEDMET"), 0.0);

        assertEquals(7.0, result.get("MINCLS"), 0.0);
        assertEquals(11.0, result.get("MAXCLS"), 0.0);
        assertEquals(9.0, result.get("MEDCLS"), 0.0);
    }
}
