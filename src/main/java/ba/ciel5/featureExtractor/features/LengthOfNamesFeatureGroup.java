/**
 * Created on 15.04.2016.
 * Feature group with different name lengths in code
 * @author tobias.meier
 */
package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.utils.Average;
import org.eclipse.jdt.core.dom.*;

import java.util.*;


public class LengthOfNamesFeatureGroup implements IFeatureGroup {

    public Map<String, Double> extract(CompilationUnit ast, char[] code) {

        final List<Integer> variableLengths = new ArrayList<Integer>();
        double minVaribaleNameLength = Integer.MAX_VALUE;
        double maxVaribaleNameLength = 0;
        double medVaribaleNameLength = 0;

        final List<Integer> methodLengths = new ArrayList<Integer>();
        double minMethodNameLength = Integer.MAX_VALUE;
        double maxMethodNameLength = 0;
        double medMethodNameLength = 0;

        final List<Integer> classLengths = new ArrayList<Integer>();
        double minclassNameLength = Integer.MAX_VALUE;
        double maxclassNameLength = 0;
        double medclassNameLength = 0;


        for (Object type : ast.types()) {
            try {
                TypeDeclaration typeNode = (TypeDeclaration) type;
                classLengths.add(typeNode.getName().getLength());

                //get class varibale names
                for (FieldDeclaration field : typeNode.getFields()) {

                    List<VariableDeclarationFragment> ff = field.fragments();
                    variableLengths.add(ff.get(0).getName().getLength());

                }

                //get class method names
                for (MethodDeclaration method : typeNode.getMethods()) {
                    methodLengths.add(method.getName().getLength());

                    ASTVisitor visitor = new ASTVisitor() {
                        @Override
                        public void preVisit(ASTNode node) {
                            //Search for simple names
                            if ( node.getNodeType() == 42) {
                                //Varibale Declatrion statement
                                if ( node.getParent().getNodeType() == 59 ) {
                                    variableLengths.add(node.getLength());
                                }
                            }
                            super.preVisit(node);
                        }
                    };
                    method.accept(visitor);
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(variableLengths);
        Collections.sort(methodLengths);

        if ( variableLengths.size() > 0 ) {
            minVaribaleNameLength = variableLengths.get(0);
            maxVaribaleNameLength = variableLengths.get(variableLengths.size() - 1);
            medVaribaleNameLength = Average.getMedianFromIntegers(variableLengths);
        }
        else
            minVaribaleNameLength=0;

        if ( methodLengths.size() > 0 ) {
            minMethodNameLength = methodLengths.get(0);
            maxMethodNameLength = methodLengths.get(methodLengths.size() - 1);
            medMethodNameLength = Average.getMedianFromIntegers(methodLengths);
        }
        else
            minMethodNameLength=0;

        if ( classLengths.size() > 0 ) {
            minclassNameLength = classLengths.get(0);
            maxclassNameLength = classLengths.get(classLengths.size() - 1);
            medclassNameLength = Average.getMedianFromIntegers(classLengths);
        }
        else
            minclassNameLength=0;

        Map<String, Double> map = new HashMap<String, Double>();
        map.put("MINVAR", minVaribaleNameLength);
        map.put("MAXVAR", maxVaribaleNameLength);
        map.put("MEDVAR", medVaribaleNameLength);
        map.put("MINMET", minMethodNameLength);
        map.put("MAXMET", maxMethodNameLength);
        map.put("MEDMET", medMethodNameLength);
        map.put("MINCLS", minclassNameLength);
        map.put("MAXCLS", maxclassNameLength);
        map.put("MEDCLS", medclassNameLength);
        return map;
    }
}
