package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created on 13.05.2016.
 *
 * @author tobias.meier
 */
public class CodeComplexityFeatureGroupTest {
    @org.junit.Test
    public void extract() throws Exception {
        CodeComplexityFeatureGroup feature = new CodeComplexityFeatureGroup();
        char[] code = TestClass1.getTestCode().toCharArray();
        CompilationUnit ast = AbstractSyntaxTreeUtil.parse(code);
        Map<String, Double> result = feature.extract(null, null, ast, code);

        assertEquals(47.0, result.get("halsteadn1"), 0.0);
        assertEquals(13.0, result.get("halsteadn2"), 0.0);
        assertEquals(226.0, result.get("halsteadBigN1"), 0.0);
        assertEquals(17.0, result.get("halsteadBigN2"), 0.0);

        assertEquals(18.0, result.get("mccabeTotal"), 0.0);
    }
}
