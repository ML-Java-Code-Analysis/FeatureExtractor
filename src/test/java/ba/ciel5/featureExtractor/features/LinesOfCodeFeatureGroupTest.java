package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.CompilationUnit;

import static org.junit.Assert.assertEquals;

import java.util.Map;

/**
 * Created on 12.04.2016.
 *
 * @author ymeke
 */
public class LinesOfCodeFeatureGroupTest {

    @org.junit.Test
    public void extract() throws Exception {
        LinesOfCodeFeatureGroup feature = new LinesOfCodeFeatureGroup();
        char[] code = TestClass1.getTestCode().toCharArray();
        CompilationUnit ast = AbstractSyntaxTreeUtil.parse(code);
        Map<String, Double> result = feature.extract(null, null, ast, code);

        assertEquals(83.0, result.get("PLOC"), 0.0);
        assertEquals(58.0, result.get("SLOC"), 0.0);
        assertEquals(13.0, result.get("BLOC"), 0.0);
        assertEquals(13.0, result.get("CLOC"), 0.0);
        assertEquals(0.0, result.get("MINLINE"), 0.0);
        assertEquals(65.0, result.get("MAXLINE"), 0.0);
        assertEquals(24.0, result.get("MEDLINE"), 0.0);
    }

}