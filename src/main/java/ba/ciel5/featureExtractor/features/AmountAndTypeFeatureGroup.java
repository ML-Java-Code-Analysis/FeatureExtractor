package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 14.05.2016.
 *
 * @author tobias.meier
 */
public class AmountAndTypeFeatureGroup implements IFeatureGroup {

    private final String AMOUNTOFSTRING = "AO";

    @Override
    public Map<String, Double> extract(List<Commit> commits, Version version, CompilationUnit ast, char[] code) {
        Map<String, Double> map = new HashMap<String, Double>();

        final Double[] amountOfClasses = {0.0};
        initializeModifiers(map,"Classes");
        final Double[] amountOfAbstractClasses = {0.0};
        final Double[] amountOfEnums = {0.0};
        initializeModifiers(map,"Enums");
        final Double[] amountOfInterfaces = {0.0};
        initializeModifiers(map,"Interfaces");

        final Double[] amountOfMethods = {0.0};
        initializeModifiers(map,"Methods");

        final Double[] amountOfConstants = {0.0};
        initializeModifiers(map,"Constants");

        final Double[] amountOfFields = {0.0};
        initializeModifiers(map,"Fields");

        final Double[] amountOfImports = {0.0};
        final Double[] amountOfImplementedInterfaces = {0.0};

        final Double[] amountOfLocals = {0.0};

        // AST vistor for getting all operands
        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public void preVisit(ASTNode node) {

                //imports
                if (node.getNodeType() == ASTNode.IMPORT_DECLARATION)
                    amountOfImports[0]++;

                //classes and interfaces
                if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
                    TypeDeclaration typeDeclaration = (TypeDeclaration) node;
                    if (typeDeclaration.isInterface()) {
                        amountOfInterfaces[0]++;
                        processModifiers(map, typeDeclaration.getModifiers(), "Interfaces");
                    } else {
                        if ((typeDeclaration.getModifiers() & Modifier.ABSTRACT) != 0)
                            amountOfAbstractClasses[0]++;
                        else
                            amountOfClasses[0]++;
                        processModifiers(map, typeDeclaration.getModifiers(), "Classes");
                    }
                    amountOfImplementedInterfaces[0] += typeDeclaration.superInterfaceTypes().size();
                }

                //enums
                if (node.getNodeType() == ASTNode.ENUM_DECLARATION) {
                    EnumDeclaration enumDeclaration = (EnumDeclaration)node;
                    amountOfEnums[0]++;
                    processModifiers(map, enumDeclaration.getModifiers(), "Enums");
                }

                //methods
                if (node.getNodeType() == ASTNode.METHOD_DECLARATION) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration)node;
                    amountOfMethods[0]++;
                    processModifiers(map, methodDeclaration.getModifiers(), "Methods");
                }

                //field declaration and constants
                if ( node.getNodeType() == ASTNode.FIELD_DECLARATION) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
                    if (((fieldDeclaration.getModifiers() & Modifier.FINAL) != 0) &&
                            (((fieldDeclaration.getModifiers() & Modifier.STATIC) != 0))) {
                        amountOfConstants[0]++;
                        processModifiers(map, fieldDeclaration.getModifiers(), "Constants");
                    } else {
                        amountOfFields[0]++;
                        processModifiers(map, fieldDeclaration.getModifiers(), "Fields");
                    }
                }

                //locals
                if ( node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT)
                    amountOfLocals[0]++;

                super.preVisit(node);
            }

        };
        ast.accept(visitor);

        map.put(AMOUNTOFSTRING + "Classes", amountOfClasses[0]);
        map.put(AMOUNTOFSTRING + "AbstractClasses", amountOfAbstractClasses[0]);
        map.put(AMOUNTOFSTRING + "Enums", amountOfEnums[0]);
        map.put(AMOUNTOFSTRING + "Interfaces", amountOfInterfaces[0]);
        map.put(AMOUNTOFSTRING + "Methods", amountOfMethods[0]);
        map.put(AMOUNTOFSTRING + "Constants", amountOfConstants[0]);
        map.put(AMOUNTOFSTRING + "Fields", amountOfFields[0]);
        map.put(AMOUNTOFSTRING + "Imports", amountOfImports[0]);
        map.put(AMOUNTOFSTRING + "ImplementedInterfaces", amountOfImplementedInterfaces[0]);
        map.put(AMOUNTOFSTRING + "Locals", amountOfLocals[0]);

        return map;
    }

    /**
     * Initialize modifiers so that no feature is missing on the feature map
     * @param map feature map to add
     * @param Type for name of feature
     */
    private void initializeModifiers(Map<String, Double> map, String Type) {
        map.put(AMOUNTOFSTRING + "PRIV" + Type,0.0);
        map.put(AMOUNTOFSTRING + "PUB" + Type,0.0);
        map.put(AMOUNTOFSTRING + "PRO" + Type,0.0);
    }

    /**
     * if modifier (public, private or protected) exists add it to the feature map
     * @param map feature map to add
     * @param modifiers modifiers of the ast object
     * @param Type for name of feature
     */
    private void processModifiers(Map<String, Double> map, int modifiers, String Type) {
        if ((modifiers & Modifier.PRIVATE) != 0)
            hashMapPutPlusOne(map, AMOUNTOFSTRING + "PRIV" + Type);
        if ((modifiers & Modifier.PUBLIC) != 0)
            hashMapPutPlusOne(map, AMOUNTOFSTRING + "PUB" + Type);
        if ((modifiers & Modifier.PROTECTED) != 0)
            hashMapPutPlusOne(map, AMOUNTOFSTRING + "PRO" + Type);
    }

    /**
     * put a key value pair to a map. If the key already exists add one to the value
     *
     * @param map map to put
     * @param key key to put
     */
    private void hashMapPutPlusOne(Map<String, Double> map, String key) {
        Double value = map.get(key);
        if (value == null)
            map.put(key, 1.0);
        else
            map.put(key, value + 1);
    }
}
