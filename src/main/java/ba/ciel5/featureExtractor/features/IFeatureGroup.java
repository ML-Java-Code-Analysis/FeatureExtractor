/**
 * Created on 12.04.2016.
 *
 * @author ymeke
 */
package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.Version;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.List;
import java.util.Map;

public interface IFeatureGroup {
    public Map<String, Double> extract(List<Commit> commits, Version version, CompilationUnit ast, char[] code);
}
