package ba.ciel5.featureExtractor.utils;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12.04.2016.
 * Some usefull utils to work with ast
 * @author ymeke
 */
public class AbstractSyntaxTreeUtil {

    /**
     * Parses Java char code array to abstract syntax tree
     * @param code as char array
     * @return abstract syntra tree (Compilation unit)
     */
    public static CompilationUnit parse(char[] code) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(code);
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null);
    }

    /**
     * Get all imports
     * @param ast code
     * @return list of ImportDeclaration elements
     */
    public static List<ImportDeclaration> getImports(CompilationUnit ast)  {
        List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
        for (Object importElement : ast.imports()) {
            imports.add((ImportDeclaration) importElement);
        }
        return imports;
    }


    /**
     * Get all classes of ast code
     * @param ast javacode to search
     * @return List of classes
     * @throws ClassCastException
     */
    public static List<TypeDeclaration> getClasses(CompilationUnit ast) throws ClassCastException {
        List<TypeDeclaration> classes = new ArrayList<TypeDeclaration>();
        for (Object type : ast.types())
            if ( type instanceof TypeDeclaration )
                classes.add((TypeDeclaration) type);
        return classes;
    }

    /**
     * Get clases which a class extends
     * @param typeDeclaration
     * @return superClass
     */
    public static SimpleType getSuperClass(TypeDeclaration typeDeclaration) {
        SimpleType superClass = (SimpleType) typeDeclaration.getSuperclassType();
        return superClass;
    }

    /**
     * Get interfaces which a class implements
     * @param typeDeclaration
     * @return List of interfaces
     */
    public static List<SimpleType> getSuperInterfaces(TypeDeclaration typeDeclaration) {
        List<SimpleType> superClasses = new ArrayList<SimpleType>();
        for ( Object superClass : typeDeclaration.superInterfaceTypes() ) {
            if ( superClass instanceof SimpleType )
                superClasses.add((SimpleType) superClass);
        }
        return superClasses;
    }

    /**
     * Get all variables of an ast java class
     * @param javaClass to search
     * @return list of variables
     */
    public static List<FieldDeclaration> getClassVariables(TypeDeclaration javaClass) {
        List<FieldDeclaration> varibales = new ArrayList<FieldDeclaration>();
        for (FieldDeclaration field : javaClass.getFields()) {
            varibales.add(field);
        }
        return varibales;
    }

    /**
     * Get all methods of an ast java class
     * @param javaClass
     * @return a list of all methods
     */
    public static List<MethodDeclaration> getClassMethods(TypeDeclaration javaClass) {
        List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
        for (MethodDeclaration method : javaClass.getMethods()) {
            methods.add(method);
        }
        return methods;
    }

    /**
     * Get all method calls of a method
     * @param method to search
     * @return a list of all method calls
     */
    public static List<MethodInvocation> getAllMethodCalls(MethodDeclaration method) {
        List<MethodInvocation> methodInvocation = null;

        Block body = method.getBody();

        //Seems to be an abstract method without body
        if (body == null)
            return new ArrayList<MethodInvocation>();
        List<ASTNode> statements = body.statements();

        //filter ASTNode for MethodInvocation (method call)
        methodInvocation = filterASTTreeForNodeType(statements, 32);

        return methodInvocation;
    }

    /**
     * Get every access to a class variable (field)
     * @param method to search
     * @return a list of all access statements
     */
    public static List<FieldAccess> getFieldAccess(MethodDeclaration method) {
        List<FieldAccess> fieldAccess;

        Block body = method.getBody();
        //Seems to be an abstract method without body
        if (body == null)
            return new ArrayList<FieldAccess>();
        List<ASTNode> statements = body.statements();

        //filter ASTNode for FieldAccess (class variable access)
        fieldAccess = filterASTTreeForNodeType(statements, 22);

        return fieldAccess;
    }

    /**
     * Get all variable declaration fragements
     * @param method to search
     * @return a list of all statements
     */
    public static List<VariableDeclarationFragment> getVariableDeclarationFragments(MethodDeclaration method) {
        List<VariableDeclarationFragment> variableDeclarationFragements;

        Block body = method.getBody();
        //Seems to be an abstract method without body
        if (body == null)
            return new ArrayList<VariableDeclarationFragment>();
        List<ASTNode> statements = body.statements();

        //filter ASTNode for FieldAccess (class variable access)
        variableDeclarationFragements = filterASTTreeForNodeType(statements, 59);

        return variableDeclarationFragements;
    }

    /**
     * Get all Modifiers of TypeDefclaration
     * @param typeDeclaration
     * @return List of modifiers
     */
    public static List<Modifier> getAllModifiers(TypeDeclaration typeDeclaration) {
        List<Modifier> nodeModifiers = new ArrayList<Modifier>();
        for ( Object m : typeDeclaration.modifiers() )
            if ( m instanceof Modifier )
                nodeModifiers.add((Modifier) m);

        return nodeModifiers;
    }

    /**
     * Helper function to filter an AST node for a specific ast type (for example a method / a variable / ...)
     * @param statements ast nodes ( code statements )
     * @param nodeTypeNumber ast code number (for example 22 for all class variable access)
     * @param <T> a list of all ast nodes found
     * @return
     */
    private static <T> List<T> filterASTTreeForNodeType(List<ASTNode> statements, int nodeTypeNumber) {
        final List<T> items = new ArrayList<T>();

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public void preVisit(ASTNode node) {
                //if NodeType is type number
                if (node.getNodeType() == nodeTypeNumber) {
                    items.add((T) node);
                }
                super.preVisit(node);
            }
        };

        for (ASTNode statement : statements) {
            statement.accept(visitor);
        }
        return items;
    }
}
