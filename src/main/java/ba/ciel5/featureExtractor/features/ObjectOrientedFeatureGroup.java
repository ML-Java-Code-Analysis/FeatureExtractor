/**
 * Created on 16.04.2016.
 * Feature group of object oriented features
 * @author tobias.meier
 */
package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import ba.ciel5.featureExtractor.utils.ListUtil;
import org.eclipse.jdt.core.dom.*;

import java.util.*;
import java.util.stream.Collectors;

public class ObjectOrientedFeatureGroup implements IFeatureGroup {

    public Map<String, Double> extract(CompilationUnit ast, char[] code) {

        // TODO not yet implemented
        // TODO very hard to implement wihtout scan the whole source-code for every commit...
        //double afferentCouplings = 0;
        //double numberOfChildren = 0;

        //if we have more than just one class in a file we save the features in a pair list for every class
        List<Integer> weightedMethodsPerClass = new ArrayList<Integer>();

        //TODO this shit doesn't work like this. full source code scan needed
        //List<Integer> depthOfInheritanceTree = new ArrayList<Integer>();

        List<Integer> couplingBetweenObjects = new ArrayList<Integer>();
        List<Integer> responseForClass = new ArrayList<Integer>();
        List<Integer> lackOfCohesionInMethods = new ArrayList<Integer>();
        List<Integer> numberOfPublicMethods = new ArrayList<Integer>();
        List<Integer> numberOfPublicVariables = new ArrayList<Integer>();

        List<TypeDeclaration> javaClasses = null;
        try {
            javaClasses = AbstractSyntaxTreeUtil.getClasses(ast);
        } catch (ClassCastException e) {
            String errorMessage = "Could not parse Source-Code with AST";
            throw new ClassCastException(errorMessage);
        }

        //the big loop --> for every java class
        for (TypeDeclaration javaClass : javaClasses) {

            int numberOfPublicVariablesInClass = 0;
            int numberOfPublicMethodsInClass = 0;
            int numberOfMethodsWithoutClassVariableUsage = 0;
            List<FieldDeclaration> classVariables = AbstractSyntaxTreeUtil.getClassVariables(javaClass);
            List<VariableDeclarationFragment> classVariableFragements = new ArrayList<>();

            for (FieldDeclaration classVariable : classVariables) {
                classVariableFragements.add((VariableDeclarationFragment) classVariable.fragments().get(0));
                if (Modifier.isPublic(classVariable.getModifiers())) {
                    numberOfPublicVariablesInClass++;
                }
            }


            List<MethodDeclaration> classMethods = AbstractSyntaxTreeUtil.getClassMethods(javaClass);

            int externalMethodCalls = 0;
            for (MethodDeclaration classMethod : classMethods) {
                List<MethodInvocation> methodCalls = AbstractSyntaxTreeUtil.getAllMethodCalls(classMethod);
                List<FieldAccess> fieldAccess = AbstractSyntaxTreeUtil.getFieldAccess(classMethod);

                // Save in methodCallNames all called methods that are not member of the class
                // TODO maybe there is a better more effienct way to finally get all methods call that are not member of the class
                List<String> methodCallNames = methodCalls
                        .stream()
                        .map(jmc -> jmc.getName().toString())
                        .collect(Collectors.toList());
                methodCallNames.removeIf(m ->
                        classMethods
                                .stream()
                                .map(cm -> cm.getName().toString())
                                .collect(Collectors.toList())
                                .contains(m));

                externalMethodCalls += methodCallNames.size();

                //Save in filedAccessNames all variable access that does not access a class variable
                // TODO maybe there is a better more effienct way to finally get all variable access that are not member of the class
                List<String> fieldAccessNames = fieldAccess
                        .stream()
                        .map(fa -> fa.getName().toString())
                        .collect(Collectors.toList());
                fieldAccessNames.removeIf(v ->
                        classVariableFragements
                                .stream()
                                .map(jv -> jv.getName().toString())
                                .collect(Collectors.toList())
                                .contains(v));
                // if a method does not access a class variable count numberOfMethodsWithoutClassVariableUsage
                if (fieldAccessNames.size() != 0)
                    numberOfMethodsWithoutClassVariableUsage++;

                if (Modifier.isPublic(classMethod.getModifiers()))
                    numberOfPublicMethodsInClass++;
            }


            weightedMethodsPerClass.add(javaClass.getMethods().length);
            //TODO DIT feature
            //depthOfInheritanceTree.add(getDepthofInheritance(javaClass.getClass()));
            couplingBetweenObjects.add(externalMethodCalls);
            responseForClass.add(externalMethodCalls + classMethods.size());
            lackOfCohesionInMethods.add(numberOfMethodsWithoutClassVariableUsage);
            numberOfPublicMethods.add(numberOfPublicMethodsInClass);
            numberOfPublicVariables.add(numberOfPublicVariablesInClass);
        }

        double weightedMethodsPerClassPerFile = ListUtil.sum(weightedMethodsPerClass);
        double depthOfInheritanceTreePerFile = 0;
        //TODO DIT feature
        //if (depthOfInheritanceTree.size() > 0)
            //depthOfInheritanceTreePerFile = ListUtil.sum(depthOfInheritanceTree) / depthOfInheritanceTree.size();
        double couplingBetweenObjectsPerFile = ListUtil.sum(couplingBetweenObjects);
        double responseForClassPerFile = ListUtil.sum(responseForClass);
        double lackOfCohesionInMethodsPerFile = ListUtil.sum(lackOfCohesionInMethods);
        double numberOfPublicMethodsPerFile = ListUtil.sum(numberOfPublicMethods);
        double numberOfPublicVariablesPerFile = ListUtil.sum(numberOfPublicVariables);

        Map<String, Double> map = new HashMap<String, Double>();
        map.put("WMC", weightedMethodsPerClassPerFile);
        //TODO DIT feature
        //map.put("DIT", depthOfInheritanceTreePerFile);
        map.put("CBO", couplingBetweenObjectsPerFile);
        map.put("RFC", responseForClassPerFile);
        map.put("LCOM", lackOfCohesionInMethodsPerFile);
        map.put("NPM", numberOfPublicMethodsPerFile);
        map.put("NPV", numberOfPublicVariablesPerFile);
        return map;
    }

    /**
     * Helper function to get depth of class inheritance to object class
     * TODO shit doesn't work...
     * @param javaClass class to get inheritance for
     * @return depth of inheritance as number
     */
    public int getDepthofInheritance(Class javaClass) {
        int inheritanceCounter = 0;
        while (javaClass.getSuperclass() != null) {
            javaClass = javaClass.getSuperclass();
            inheritanceCounter++;
        }
        return inheritanceCounter;
    }
}
