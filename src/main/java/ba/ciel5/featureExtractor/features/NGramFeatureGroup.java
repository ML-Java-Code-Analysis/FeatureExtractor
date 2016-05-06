/**
 * Created by tobias.meier on 26.04.2016.
 */
package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.FeatureExtractor;
import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class NGramFeatureGroup implements IFeatureGroup {

    private final String SMALL = "Small";
    private final String MEDIUM = "Medium";
    private final String LARGE = "Large";

    private final Integer BLOCKSIZEMEDIUMTHRESHOLD = 10;
    private final Integer BLOCKSIZELARGETHRESHOLD = 25;

    private final Integer METHODCOUNTMEDIUMTHRESHOLD = 5;
    private final Integer METHODCOUNTLARGETHRESHOLD = 10;

    private final Integer AMOUNTOFPARAMETERMEDIUMTHRESHOLD = 3;
    private final Integer AMOUNTOFPARAMETERLARGETHRESHOLD = 6;

    private final String COMPLEX = "Complex";
    private final String SIMPLE = "Simple";

    private final Integer COMPLEXTHRESHOLD = 20;

    private final String CLASSNAME = "ClassDeclaration";
    private final String INTERFACENAME = "InterfaceDeclaration";
    private final String IMPLEMENTS = "ImplementExpression";
    private final String EXTENDS = "ExtendsExpression";
    private final String METHOD = "MethodDeclaration";


    @Override
    public Map<String, Double> extract(List<Commit> commits, Version version, CompilationUnit ast, char[] code) {
        Map<String, Double> map = new HashMap<String, Double>();
        List<String> flatCode = new ArrayList<String>();
        List<String> statements = new ArrayList<String>();

        final List<Integer> statementNodesToInclude = new ArrayList<Integer>(
                Arrays.asList(
                        ASTNode.ANONYMOUS_CLASS_DECLARATION,
                        ASTNode.ASSERT_STATEMENT,
                        ASTNode.BLOCK,
                        ASTNode.BLOCK_COMMENT,
                        ASTNode.BREAK_STATEMENT,
                        ASTNode.CATCH_CLAUSE,
                        ASTNode.CLASS_INSTANCE_CREATION,
                        ASTNode.CONSTRUCTOR_INVOCATION,
                        ASTNode.CONTINUE_STATEMENT,
                        ASTNode.DO_STATEMENT,
                        ASTNode.EMPTY_STATEMENT,
                        ASTNode.ENHANCED_FOR_STATEMENT,
                        ASTNode.EXPRESSION_STATEMENT,
                        ASTNode.FOR_STATEMENT,
                        ASTNode.IF_STATEMENT,
                        ASTNode.IMPORT_DECLARATION,
                        ASTNode.JAVADOC,
                        ASTNode.LABELED_STATEMENT,
                        ASTNode.LINE_COMMENT,
                        ASTNode.MARKER_ANNOTATION,
                        //ASTNode.METHOD_DECLARATION, //special handling
                        ASTNode.NORMAL_ANNOTATION,
                        ASTNode.PACKAGE_DECLARATION,
                        ASTNode.RETURN_STATEMENT,
                        ASTNode.SWITCH_STATEMENT,
                        ASTNode.SYNCHRONIZED_STATEMENT,
                        ASTNode.THROW_STATEMENT,
                        ASTNode.TRY_STATEMENT,
                        ASTNode.TYPE_DECLARATION_STATEMENT,
                        //ASTNode.TYPE_DECLARATION, //special handling
                        ASTNode.VARIABLE_DECLARATION_STATEMENT,
                        ASTNode.WHILE_STATEMENT
                )
        );
        List<String> controlStructures = new ArrayList<String>();
        final List<Integer> controlNodesToInclude = new ArrayList<Integer>(
                Arrays.asList(
                        ASTNode.ANONYMOUS_CLASS_DECLARATION,
                        //ASTNode.BLOCK, //special handling
                        ASTNode.BREAK_STATEMENT,
                        ASTNode.CATCH_CLAUSE,
                        ASTNode.CLASS_INSTANCE_CREATION,
                        ASTNode.CONTINUE_STATEMENT,
                        ASTNode.DO_STATEMENT,
                        ASTNode.FOR_STATEMENT,
                        ASTNode.IF_STATEMENT,
                        //ASTNode.METHOD_DECLARATION, //special handling
                        ASTNode.PACKAGE_DECLARATION,
                        ASTNode.RETURN_STATEMENT,
                        ASTNode.SWITCH_STATEMENT,
                        ASTNode.SYNCHRONIZED_STATEMENT,
                        ASTNode.THROW_STATEMENT,
                        ASTNode.TRY_STATEMENT,
                        //ASTNode.TYPE_DECLARATION, //special handling
                        ASTNode.WHILE_STATEMENT
                )
        );
        List<String> classesAndMethods = new ArrayList<String>();
        final List<Integer> classesAndMethodsToInclude = new ArrayList<Integer>(
                Arrays.asList(
                        //ASTNode.METHOD_DECLARATION, //special handling
                        ASTNode.PACKAGE_DECLARATION
                        //ASTNode.TYPE_DECLARATION //special handling
                )
        );
        final List<Integer> controlBlockToInclude = new ArrayList<Integer>(
                Arrays.asList(
                        ASTNode.CATCH_CLAUSE,
                        ASTNode.DO_STATEMENT,
                        ASTNode.FOR_STATEMENT,
                        ASTNode.IF_STATEMENT,
                        ASTNode.SWITCH_STATEMENT,
                        ASTNode.SYNCHRONIZED_STATEMENT,
                        ASTNode.TRY_STATEMENT,
                        ASTNode.WHILE_STATEMENT
                )
        );

        ASTVisitor visitor = new ASTVisitor() {

            @Override
            public void preVisit(ASTNode node) {

                //Compilation Unit - skip it
                if (node.getParent() == null)
                    super.preVisit(node);
                else {

                    //NGrams over whole AST
                    flatCode.add(node.getClass().getSimpleName());

                    //Block size
                    if (node.getNodeType() == ASTNode.BLOCK) {
                        String blockString = generateBlockString((Block) node);
                        controlStructures.add(blockString);
                    }

                    //Class or interface generate signature string
                    if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
                        String typeDeclarationString = generateTypeDeclarationString((TypeDeclaration) node);
                        statements.add(typeDeclarationString);
                        controlStructures.add(typeDeclarationString);
                        classesAndMethods.add(typeDeclarationString);
                    }

                    //Method Generate generate signature string
                    if (node.getNodeType() == ASTNode.METHOD_DECLARATION) {
                        String methodDeclarationString = generateMethodDeclarationString((MethodDeclaration) node);
                        statements.add(methodDeclarationString);
                        controlStructures.add(methodDeclarationString);
                        classesAndMethods.add(methodDeclarationString);
                    }

                    //NGrams over statements
                    if (statementNodesToInclude.contains(node.getNodeType()))
                        statements.add(node.getClass().getSimpleName());

                    //NGrams over control structure
                    if (controlNodesToInclude.contains(node.getNodeType()))
                        controlStructures.add(generateControlStatementString(node));

                    //NGrams over classes
                    if (classesAndMethodsToInclude.contains(node.getNodeType()))
                        classesAndMethods.add(node.getClass().getSimpleName());

                    super.preVisit(node);
                }
            }
        };
        ast.accept(visitor);

        Integer maxNGramSize = 5;
        if (FeatureExtractor.getCfg() == null)
            maxNGramSize = 5;
        else
            FeatureExtractor.getCfg().getMaxNGramSize();

        List<String> ngrams = generateNgramsUpto(flatCode, maxNGramSize);
        ngrams.addAll(generateNgramsUpto(statements, maxNGramSize));
        ngrams.addAll(generateNgramsUpto(controlStructures, maxNGramSize));
        ngrams.addAll(generateNgramsUpto(classesAndMethods, maxNGramSize));
        for (String ngram : ngrams) {
            Double value = 1.0;
            if (map.containsKey(ngram))
                value = map.get(ngram) + 1;

            map.put(ngram, value);
        }
        return map;
    }

    /**
     * @param flatCode    Code List
     * @param maxGramSize should be 1 at least
     * @return set of continuous word n-grams up to maxGramSize from the sentence
     */
    private List<String> generateNgramsUpto(List<String> flatCode, int maxGramSize) {

        List<String> ngrams = new ArrayList<String>();
        int ngramSize = 0;
        StringBuilder sb = null;

        //sentence becomes ngrams
        for (ListIterator<String> it = flatCode.listIterator(); it.hasNext(); ) {
            String word = (String) it.next();

            //1- add the word itself
            sb = new StringBuilder(word);
            ngrams.add(word);
            ngramSize = 1;
            it.previous();

            //2- insert prevs of the word and add those too
            while (it.hasPrevious() && ngramSize < maxGramSize) {
                sb.insert(0, '-');
                sb.insert(0, it.previous());
                ngrams.add(sb.toString());
                ngramSize++;
            }

            //go back to initial position
            while (ngramSize > 0) {
                ngramSize--;
                it.next();
            }
        }
        return ngrams;
    }

    /**
     * Generate string for block with size
     * @param node block
     * @return Block<Size>
     */
    private String generateBlockString(Block node) {
        StringBuffer blockString = new StringBuffer();

        blockString.append(node.getClass().getSimpleName());

        if (node.statements().size() < BLOCKSIZEMEDIUMTHRESHOLD)
            blockString.append(SMALL);
        else if (node.statements().size() > BLOCKSIZELARGETHRESHOLD)
            blockString.append(LARGE);
        else
            blockString.append(MEDIUM);

        return blockString.toString();
    }

    /**
     * get complexity of a control statement (count statements in it)
     * @param node ASTNode
     * @return Statement<Complexity>
     */
    private String generateControlStatementString(ASTNode node) {
        StringBuffer controlStatementString = new StringBuffer();

        controlStatementString.append(node.getClass().getSimpleName());

        if (node.getNodeType() == ASTNode.CATCH_CLAUSE) {
            Integer statementSize = ((CatchClause) node).getBody().statements().size();
            if (statementSize > COMPLEXTHRESHOLD)
                controlStatementString.append(COMPLEX);
            else
                controlStatementString.append(SIMPLE);
        } else if (node.getNodeType() == ASTNode.DO_STATEMENT) {
            if (((DoStatement) node).getBody().getNodeType() == ASTNode.EXPRESSION_STATEMENT)
                controlStatementString.append(SIMPLE);
            else if (((DoStatement) node).getBody().getNodeType() == ASTNode.BLOCK) {
                Integer statementSize = ((Block) ((DoStatement) node).getBody()).statements().size();
                if (statementSize > COMPLEXTHRESHOLD)
                    controlStatementString.append(COMPLEX);
                else
                    controlStatementString.append(SIMPLE);
            }
        } else if (node.getNodeType() == ASTNode.FOR_STATEMENT) {
            if (((ForStatement) node).getBody().getNodeType() == ASTNode.EXPRESSION_STATEMENT)
                controlStatementString.append(SIMPLE);
            else if (((ForStatement) node).getBody().getNodeType() == ASTNode.BLOCK) {
                Integer statementSize = ((Block) ((ForStatement) node).getBody()).statements().size();
                if (statementSize > COMPLEXTHRESHOLD)
                    controlStatementString.append(COMPLEX);
                else
                    controlStatementString.append(SIMPLE);
            }
        } else if (node.getNodeType() == ASTNode.IF_STATEMENT) {
            if (((IfStatement) node).getThenStatement().getNodeType() == ASTNode.EXPRESSION_STATEMENT) {
                if (((IfStatement) node).getElseStatement() != null) {
                    if (((IfStatement) node).getElseStatement().getNodeType() == ASTNode.EXPRESSION_STATEMENT)
                        controlStatementString.append(SIMPLE);
                    else if (((IfStatement) node).getElseStatement().getNodeType() == ASTNode.BLOCK) {
                        Integer statementElseSize = ((Block) ((IfStatement) node).getElseStatement()).statements().size();
                        if (statementElseSize > COMPLEXTHRESHOLD)
                            controlStatementString.append(COMPLEX);
                        else
                            controlStatementString.append(SIMPLE);
                    }
                }
            } else if (((IfStatement) node).getThenStatement().getNodeType() == ASTNode.BLOCK) {
                Integer statementThenSize = ((Block) ((IfStatement) node).getThenStatement()).statements().size();
                Integer statementElseSize = 0;
                if (((IfStatement) node).getElseStatement() != null) {
                    if (((IfStatement) node).getElseStatement().getNodeType() == ASTNode.EXPRESSION_STATEMENT)
                        statementElseSize = 1;
                    else if (((IfStatement) node).getElseStatement().getNodeType() == ASTNode.BLOCK)
                        statementElseSize = ((Block) ((IfStatement) node).getElseStatement()).statements().size();
                }
                if ((statementThenSize + statementElseSize) > COMPLEXTHRESHOLD)
                    controlStatementString.append(COMPLEX);
                else
                    controlStatementString.append(SIMPLE);
            }
        } else if (node.getNodeType() == ASTNode.SWITCH_STATEMENT) {
            Integer statementSize = ((SwitchStatement) node).statements().size();
            if (statementSize > COMPLEXTHRESHOLD)
                controlStatementString.append(COMPLEX);
            else
                controlStatementString.append(SIMPLE);
        } else if (node.getNodeType() == ASTNode.SYNCHRONIZED_STATEMENT) {
            Integer statementSize = ((SynchronizedStatement) node).getBody().statements().size();
            if (statementSize > COMPLEXTHRESHOLD)
                controlStatementString.append(COMPLEX);
            else
                controlStatementString.append(SIMPLE);
        } else if (node.getNodeType() == ASTNode.TRY_STATEMENT) {
            Integer statementSize = ((TryStatement) node).getBody().statements().size();
            if (statementSize > COMPLEXTHRESHOLD)
                controlStatementString.append(COMPLEX);
            else
                controlStatementString.append(SIMPLE);
        } else if (node.getNodeType() == ASTNode.WHILE_STATEMENT) {
            if (((WhileStatement) node).getBody().getNodeType() == ASTNode.EXPRESSION_STATEMENT)
                controlStatementString.append(SIMPLE);
            else if (((WhileStatement) node).getBody().getNodeType() == ASTNode.BLOCK)  {
                Integer statementSize = ((Block) ((WhileStatement) node).getBody()).statements().size();
                if (statementSize > COMPLEXTHRESHOLD)
                    controlStatementString.append(COMPLEX);
                else
                    controlStatementString.append(SIMPLE);
            }
        }
        return controlStatementString.toString();
    }

    /**
     * generate string for classes and interfaces
     * @param typeDeclaration class or interface
     * @return class or interface string
     */
    private String generateTypeDeclarationString(TypeDeclaration typeDeclaration) {
        StringBuffer typeString = new StringBuffer();

        Integer methodCount = typeDeclaration.getMethods().length;

        if (typeDeclaration.isInterface())
            typeString.append(INTERFACENAME);
        else
            typeString.append(CLASSNAME);

        typeDeclaration.modifiers().forEach(m -> typeString.append(m.getClass().getSimpleName()));

        if ( methodCount > METHODCOUNTMEDIUMTHRESHOLD )
            typeString.append(MEDIUM);
        else if ( methodCount > METHODCOUNTLARGETHRESHOLD )
            typeString.append(LARGE);
        else
            typeString.append(SMALL);

        AbstractSyntaxTreeUtil.getSuperInterfaces(typeDeclaration).forEach(i -> typeString.append(IMPLEMENTS));
        if (AbstractSyntaxTreeUtil.getSuperClass(typeDeclaration) != null) {
            typeString.append(EXTENDS);
        }
        return typeString.toString();
    }

    /**
     * get method declaration string
     * @param methodDeclaration method
     * @return method string
     */
    private String generateMethodDeclarationString(MethodDeclaration methodDeclaration) {
        StringBuffer methodString = new StringBuffer();

        Integer statementCount = 0;
        if ( methodDeclaration.getBody() != null )
            statementCount = methodDeclaration.getBody().statements().size();

        methodString.append(METHOD);

        methodDeclaration.modifiers().forEach(m -> methodString.append(m.getClass().getSimpleName()));

        if ( statementCount > BLOCKSIZEMEDIUMTHRESHOLD )
            methodString.append(MEDIUM);
        else if ( statementCount > BLOCKSIZELARGETHRESHOLD )
            methodString.append(LARGE);
        else
            methodString.append(SMALL);

        if (methodDeclaration.getReturnType2() != null)
            methodString.append(methodDeclaration.getReturnType2().toString());

        final Integer[] amountOfParameters = { 0 };
        methodDeclaration.parameters().forEach(p -> amountOfParameters[0]++);
        if ( amountOfParameters[0] > AMOUNTOFPARAMETERMEDIUMTHRESHOLD )
            methodString.append(MEDIUM);
        else if ( amountOfParameters[0] > AMOUNTOFPARAMETERLARGETHRESHOLD )
            methodString.append(LARGE);
        else
            methodString.append(SMALL);

        return methodString.toString();
    }
}
