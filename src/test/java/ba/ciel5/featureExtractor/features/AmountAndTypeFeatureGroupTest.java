package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created on 14.05.2016.
 *
 * @author tobias.meier
 */
public class AmountAndTypeFeatureGroupTest {

    @org.junit.Test
    public void extract() throws Exception {
        AmountAndTypeFeatureGroup feature = new AmountAndTypeFeatureGroup();
        char[] code = TestClass1.getTestCode().toCharArray();
        CompilationUnit ast = AbstractSyntaxTreeUtil.parse(code);
        Map<String, Double> result = feature.extract(null, null, ast, code);

        assertEquals(2.0, result.get("AOClasses"), 0.0);
        assertEquals(0.0, result.get("AOAbstractClasses"), 0.0);
        assertEquals(0.0, result.get("AOEnums"), 0.0);
        assertEquals(1.0, result.get("AOInterfaces"), 0.0);

        assertEquals(9.0, result.get("AOMethods"), 0.0);

        assertEquals(3.0, result.get("AOLocals"), 0.0);

    }
}
