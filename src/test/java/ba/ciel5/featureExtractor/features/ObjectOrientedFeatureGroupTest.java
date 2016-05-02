package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created on 16.04.2016.
 *
 * @author tobias.meier
 */
public class ObjectOrientedFeatureGroupTest {

    @org.junit.Test
    public void extract() throws Exception {
        ObjectOrientedFeatureGroup feature = new ObjectOrientedFeatureGroup();
        char[] code = TestClass1.getTestCode().toCharArray();
        CompilationUnit ast = AbstractSyntaxTreeUtil.parse(code);
        Map<String, Double> result = feature.extract(null, null, ast, code);

        assertEquals(8.0, result.get("WMC"), 0.0);
        assertEquals(6.0, result.get("CBO"), 0.0);
        assertEquals(14.0, result.get("RFC"), 0.0);
        assertEquals(1.0, result.get("LCOM"), 0.0);
        assertEquals(7.0, result.get("NPM"), 0.0);
        assertEquals(0.0, result.get("NPV"), 0.0);
    }
}
