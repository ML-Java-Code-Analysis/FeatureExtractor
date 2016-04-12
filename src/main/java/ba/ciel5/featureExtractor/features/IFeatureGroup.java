/**
 * Created on 12.04.2016.
 *
 * @author ymeke
 */
package ba.ciel5.featureExtractor.features;

import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.Map;

public interface IFeatureGroup {
    public Map<String, Double> extract(CompilationUnit ast, char[] code);
}
