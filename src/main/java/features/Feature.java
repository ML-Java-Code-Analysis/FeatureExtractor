package features;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Created on 05.04.2016.
 *
 * @author ymeke
 */
public interface Feature {

    // TODO: Evtl. code param weglassen, weil mans glaub auch aus ast holen kann --> prüfen
    /**
     * Extracts this feature from a Java code fragment
     *
     * @param ast The Abstract Syntax Tree
     * @param code The actual source code
     * @return A scalar value representing this feature for the given code
     */
    double extract(CompilationUnit ast, char[] code);

    /**
     * @return The ID for this feature.
     */
    String getFeatureId();
}
