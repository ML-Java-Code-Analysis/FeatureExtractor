/**
 * Created on 15.04.2016.
 * Feature group with different name lengths in code
 * @author tobias.meier
 */
package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import ba.ciel5.featureExtractor.utils.Average;
import org.eclipse.jdt.core.dom.*;

import java.util.*;
import java.util.stream.Collectors;


public class LengthOfNamesFeatureGroup implements IFeatureGroup {

    @Override
    public Map<String, Double> extract(List<Commit> commits, Version version, CompilationUnit ast, char[] code) {

        List<List<Integer>> variableLengths = new ArrayList<List<Integer>>();
        List<List<Integer>> methodLengths = new ArrayList<List<Integer>>();
        List<Integer> classLengths = new ArrayList<Integer>();

        List<TypeDeclaration> javaClasses = null;
        try {
            javaClasses = AbstractSyntaxTreeUtil.getClasses(ast);
        } catch (ClassCastException e) {
            String errorMessage = "Could not parse Source-Code with AST";
            throw new ClassCastException(errorMessage);
        }


        //the big loop --> for every java class
        for (TypeDeclaration javaClass : javaClasses) {
            List<Integer> variableLengthsPerClass = new ArrayList<Integer>();
            List<Integer> methodLengthsPerClass;

            List<FieldDeclaration> classVariables = AbstractSyntaxTreeUtil.getClassVariables(javaClass);


            for (FieldDeclaration classVariable : classVariables) {
                List<VariableDeclarationFragment> variableFragments = classVariable.fragments();
                variableLengthsPerClass.add(variableFragments.get(0).getName().getLength());
            }

            List<MethodDeclaration> classMethods = AbstractSyntaxTreeUtil.getClassMethods(javaClass);

            // Add all method name lengths to the methodlengts array
            methodLengthsPerClass = classMethods
                                    .stream()
                                    .map(method -> method.getName().getLength())
                                    .collect(Collectors.toList());

            // For every mehtod
            // Add all
            classMethods.forEach(method -> variableLengthsPerClass.addAll(AbstractSyntaxTreeUtil.getVariableDeclarationFragments(method)
                                                                                    .stream()
                                                                                    .map(vars -> vars.getName().getLength())
                                                                                    .collect(Collectors.toList())));

            variableLengths.add(variableLengthsPerClass);

            methodLengths.add(methodLengthsPerClass);
            classLengths.add(javaClass.getName().getLength());
        }

        double minVaribaleNameLength = Integer.MAX_VALUE;
        double maxVaribaleNameLength = 0;
        double medVaribaleNameLength = 0;

        double minMethodNameLength = Integer.MAX_VALUE;
        double maxMethodNameLength = 0;
        double medMethodNameLength = 0;

        double minclassNameLength = Integer.MAX_VALUE;
        double maxclassNameLength = 0;
        double medclassNameLength = 0;

        List<Integer> allVariableLengths = new ArrayList<Integer>();
        variableLengths.forEach(vars -> allVariableLengths.addAll(vars));
        if ( allVariableLengths.size() > 0 ) {
            Collections.sort(allVariableLengths);
            minVaribaleNameLength = allVariableLengths.get(0);
            maxVaribaleNameLength = allVariableLengths.get(allVariableLengths.size() - 1);
            medVaribaleNameLength = Average.getMedianFromIntegers(allVariableLengths);
        }
        else
            minVaribaleNameLength=0;

        List<Integer> allMethodLengths = new ArrayList<Integer>();
        methodLengths.forEach(methods -> allMethodLengths.addAll(methods));
        if ( allMethodLengths.size() > 0 ) {
            Collections.sort(allMethodLengths);
            minMethodNameLength = allMethodLengths.get(0);
            maxMethodNameLength = allMethodLengths.get(allMethodLengths.size() - 1);
            medMethodNameLength = Average.getMedianFromIntegers(allMethodLengths);
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
