package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.Version;
import org.eclipse.jdt.core.dom.*;
import java.util.*;
import static java.util.Arrays.asList;

/**
 * Created on 13.05.2016.
 *
 * @author tobias.meier
 */
public class CodeComplexityFeatureGroup implements IFeatureGroup {

    @Override
    public Map<String, Double> extract(List<Commit> commits, Version version, CompilationUnit ast, char[] code) {
        Map<String, Double> map = new HashMap<String, Double>();
        Map<String, Integer> operands = new HashMap<String, Integer>();
        Map<String, Integer> operators = new HashMap<String, Integer>();
        final Double[] mcCabeComplexity = {0.0};
        final Double[] methodCount = {0.0};
        final Double[] classCount = {0.0};
        String codeString = String.valueOf(code);

        List<String> statements = new ArrayList<String>();
        List<String> nodeType = new ArrayList<String>();
        final List<Integer> otherStatements = new ArrayList<Integer>(
                asList(
                        ASTNode.ANONYMOUS_CLASS_DECLARATION,
                        ASTNode.ASSERT_STATEMENT,
                        ASTNode.BREAK_STATEMENT,
                        ASTNode.CLASS_INSTANCE_CREATION,
                        ASTNode.CONSTRUCTOR_INVOCATION,
                        ASTNode.CONTINUE_STATEMENT,
                        ASTNode.EMPTY_STATEMENT,
                        ASTNode.LABELED_STATEMENT,
                        ASTNode.MARKER_ANNOTATION,
                        ASTNode.NORMAL_ANNOTATION,
                        ASTNode.PACKAGE_DECLARATION,
                        ASTNode.RETURN_STATEMENT,
                        ASTNode.THROW_STATEMENT
                )
        );

        final List<Integer> expressions = new ArrayList<Integer>(
                asList(
                        ASTNode.INSTANCEOF_EXPRESSION,
                        ASTNode.THIS_EXPRESSION,
                        ASTNode.MODIFIER
                )
        );

        final List<Integer> controlStatements = new ArrayList<Integer>(
                asList(
                        ASTNode.CATCH_CLAUSE,
                        ASTNode.DO_STATEMENT,
                        ASTNode.FOR_STATEMENT,
                        ASTNode.ENHANCED_FOR_STATEMENT,
                        ASTNode.IF_STATEMENT,
                        ASTNode.SWITCH_STATEMENT,
                        ASTNode.SYNCHRONIZED_STATEMENT,
                        ASTNode.TRY_STATEMENT,
                        ASTNode.WHILE_STATEMENT
                )
        );
        final List<Integer> mcCabeControlStatements = new ArrayList<Integer>(
                asList(
                        ASTNode.CATCH_CLAUSE,
                        ASTNode.DO_STATEMENT,
                        ASTNode.FOR_STATEMENT,
                        ASTNode.ENHANCED_FOR_STATEMENT,
                        ASTNode.IF_STATEMENT,
                        ASTNode.SWITCH_CASE,
                        ASTNode.WHILE_STATEMENT,
                        ASTNode.CONDITIONAL_EXPRESSION,
                        ASTNode.EXPRESSION_STATEMENT,
                        ASTNode.VARIABLE_DECLARATION_FRAGMENT
                )
        );

        final List<Integer> types = new ArrayList<Integer>(
                asList(
                        ASTNode.PRIMITIVE_TYPE,
                        ASTNode.SIMPLE_TYPE,
                        ASTNode.PRIMITIVE_TYPE,
                        ASTNode.PARAMETERIZED_TYPE,
                        ASTNode.QUALIFIED_TYPE,
                        ASTNode.WILDCARD_TYPE
                )
        );

        final List<Integer> literals = new ArrayList<Integer>(
                asList(
                        ASTNode.BOOLEAN_LITERAL,
                        ASTNode.CHARACTER_LITERAL,
                        ASTNode.NULL_LITERAL,
                        ASTNode.STRING_LITERAL,
                        ASTNode.TYPE_LITERAL
                )
        );

        // AST vistor for getting all operands
        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public void preVisit(ASTNode node) {

                //varibale names
                if (node.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
                    VariableDeclaration v = (VariableDeclaration) node;
                    hashMapPutPlusOne(operands, v.getName().toString());
                }

                //literals
                if (literals.contains(node.getNodeType()))
                    hashMapPutPlusOne(operands, node.toString());

                //types
                if (types.contains(node.getNodeType()))
                    hashMapPutPlusOne(operators, node.toString());

                //expressions
                if (expressions.contains(node.getNodeType()))
                    hashMapPutPlusOne(operators, node.toString());

                //statements
                if (otherStatements.contains(node.getNodeType()))
                    hashMapPutPlusOne(operators, node.toString());

                //control statements
                if (controlStatements.contains(node.getNodeType()))
                    hashMapPutPlusOne(operators, handleControlStructures(node));

                // methods
                if (node.getNodeType() == ASTNode.METHOD_DECLARATION) {
                    hashMapPutPlusOne(operators, ((MethodDeclaration) node).getName().toString());
                    methodCount[0]++;
                    mcCabeComplexity[0]++;
                }

                // classes
                if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
                    hashMapPutPlusOne(operators, ((TypeDeclaration) node).getName().toString());
                    classCount[0]++;
                    mcCabeComplexity[0]++;
                }

                //imports
                if (node.getNodeType() == ASTNode.IMPORT_DECLARATION) {
                    ImportDeclaration declaration = (ImportDeclaration) node;
                    hashMapPutPlusOne(operators, declaration.getClass().getSimpleName());
                    hashMapPutPlusOne(operators, declaration.getName().toString());
                }

                //statements for mccabe metrics
                if (mcCabeControlStatements.contains(node.getNodeType())) {
                    mcCabeComplexity[0] += handleMcCabe(node);
                }

                super.preVisit(node);
            }

        };
        ast.accept(visitor);

        //collect everything that is hard to collect with the ast. for example all java operators
        String commentLessCodeString = removeCommentsFromSourceCode(codeString, code, ast);
        final List<String> javaOperatorSigns = new ArrayList<String>(
                asList("[", "(", "{", "*", "%", "/", "+", "-", "<", ">", "<=", ">=", "++", "--", "<<", ">>", ">>>",
                        "==", "!=", "&", "^", "|", "||", "&&", "?", ":", "=", "+=", "-=", "*=", "/=", "%=", "&=", "^="
                        , "|=", "<<=", ">>=", ">>>=", ".", ";", ","
                        //hard to catch with ast...
                        , "class", "interface", "extends", "implements"
                )
        );
        char[] commentLessCode = commentLessCodeString.toCharArray();
        // windowSize = longest string in javaSplitSigns list
        Integer windowSize;
        OptionalInt windowSizeOpt = javaOperatorSigns.stream().sorted().mapToInt(String::length).max();
        if (windowSizeOpt.isPresent())
            windowSize = windowSizeOpt.getAsInt();
        else
            windowSize = 0;

        //sliding window go through the code and watch for all javaOperatorSigns
        for (int i = 0; i < commentLessCode.length; i++) {
            StringBuilder actualWindow = new StringBuilder();
            for (int j = 0; j <= windowSize; j++) {
                if (i + j < commentLessCode.length) {
                    actualWindow.append(commentLessCode[i + j]);
                    if (javaOperatorSigns.contains(actualWindow.toString()))
                        hashMapPutPlusOne(operators, actualWindow.toString());
                }
            }
        }


        //generate Halstead metrics
        // Quellen: http://acjournal.in/files/documents/rnr-2015-7-39.pdf (Source)
        // Quellen: http://www.whiteboxtest.com/Halstead-software-science.php
        Double n1 = operators.size() / 1.0;
        Double n2 = operands.size() / 1.0;
        Double bigN1 = operators.entrySet().stream().mapToDouble(Map.Entry::getValue).sum();
        Double bigN2 = operands.entrySet().stream().mapToDouble(Map.Entry::getValue).sum();

        Double programVocabulary = n1 + n2;
        Double programLength = bigN1 + bigN2;
        Double leftExpression = 0.0;
        Double rightExpression = 0.0;
        if (n1 != 0)
            leftExpression = Math.log(n1) / Math.log(2);
        if (n2 != 0)
            rightExpression = Math.log(n2) / Math.log(2);
        Double calculatedProgramLength = n1 * leftExpression + n2 * rightExpression;
        Double expression = 0.0;
        if (programVocabulary != 0)
            expression = Math.log(programVocabulary) / Math.log(2);
        Double volume = programLength * expression;
        Double difficulty = 0.0;
        if (n2 > 0)
            difficulty = n1 / 2 * bigN2 / n2;
        Double effort = difficulty * volume;
        Double timeRequiredToProgram = effort / 18;
        Double numberOfDeliveredBugs = Math.pow(effort, (2.0 / 3.0)) / 3000;

        map.put("halsteadn1", n1);
        map.put("halsteadn2", n2);
        map.put("halsteadBigN1", bigN1);
        map.put("halsteadBigN2", bigN2);
        map.put("halsteadn", programVocabulary);
        map.put("halsteadBigN", programLength);
        map.put("halsteadNhat", calculatedProgramLength);
        map.put("halsteadV", volume);
        map.put("halsteadD", difficulty);
        map.put("halsteadE", effort);
        map.put("halsteadT", timeRequiredToProgram);
        map.put("halsteadB", numberOfDeliveredBugs);

        //generate McCabe Cyclomatic complexity number
        //Quellen: metrics plugin : http://sourceforge.net/projects/metrics2/
        //Quellen: http://www.literateprogramming.com/mccabe.pdf

        Double mccabePerMethod = 0.0;
        if (methodCount[0] != 0)
            mccabePerMethod = mcCabeComplexity[0] / methodCount[0];

        Double mccabePerClass = 0.0;
        if (classCount[0] != 0)
            mccabePerClass = mcCabeComplexity[0] / classCount[0];

        Double mccabePerClassPerMethod = 0.0;
        if ((classCount[0] != 0) && (methodCount[0] != 0))
            mccabePerClassPerMethod = mcCabeComplexity[0] / classCount[0] / methodCount[0];

        map.put("mccabeTotal", mcCabeComplexity[0]);
        map.put("mccabePerMethod", mccabePerMethod);
        map.put("mccabePerClass", mccabePerClass);
        map.put("mccabePerClassPerMethod", mccabePerClassPerMethod);

        return map;
    }

    /**
     * remove comments of a code string
     *
     * @param codeString code string
     * @param code       code as char array
     * @param ast        ast compilation unit
     * @return code as string without comments
     */
    private String removeCommentsFromSourceCode(String codeString, char[] code, CompilationUnit ast) {
        String commentLessCodeString = codeString;
        for (Object commentObj : ast.getCommentList()) {
            Comment comment = (Comment) commentObj;
            String commentString = "";
            int start = comment.getStartPosition();
            int length = comment.getLength();
            if (comment.isLineComment() == Boolean.TRUE) {
                for (int i = start; i < code.length; i++) {
                    commentString += code[i];
                    if (code[i] == '\n') {
                        break;
                    }
                }
            } else {
                for (int i = start; i < start + length; i++) {
                    commentString += code[i];
                }
            }
            commentLessCodeString = commentLessCodeString.replace(commentString, "");
        }
        return commentLessCodeString;
    }

    /**
     * put a key value pair to a map. If the key already exists add one to the value
     *
     * @param map map to put
     * @param key key to put
     */
    private void hashMapPutPlusOne(Map<String, Integer> map, String key) {
        Integer value = map.get(key);
        if (value == null)
            map.put(key, 1);
        else
            map.put(key, value + 1);
    }

    /**
     * Handle controlstructure to keep extract method skinny
     *
     * @param node ast node
     * @return string
     */
    private String handleControlStructures(ASTNode node) {

        if (node.getNodeType() == ASTNode.CATCH_CLAUSE) {
            CatchClause statement = (CatchClause) node;
            return statement.getClass().getSimpleName();

        } else if (node.getNodeType() == ASTNode.DO_STATEMENT) {
            DoStatement statement = (DoStatement) node;
            return statement.getClass().getSimpleName();

        } else if (node.getNodeType() == ASTNode.FOR_STATEMENT) {
            ForStatement statement = (ForStatement) node;
            return statement.getClass().getSimpleName();

        } else if (node.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT) {
            EnhancedForStatement statement = (EnhancedForStatement) node;
            return statement.getClass().getSimpleName();

        } else if (node.getNodeType() == ASTNode.IF_STATEMENT) {
            StringBuilder str = new StringBuilder();
            IfStatement statement = (IfStatement) node;
            str.append(statement.getClass().getSimpleName());
            if (statement.getElseStatement() != null)
                str.append(statement.getElseStatement().getClass().getSimpleName());
            return str.toString();

        } else if (node.getNodeType() == ASTNode.SWITCH_STATEMENT) {
            SwitchStatement statement = (SwitchStatement) node;
            return statement.getClass().getSimpleName();


        } else if (node.getNodeType() == ASTNode.SYNCHRONIZED_STATEMENT) {
            SynchronizedStatement statement = (SynchronizedStatement) node;
            return statement.getClass().getSimpleName();

        } else if (node.getNodeType() == ASTNode.TRY_STATEMENT) {
            TryStatement statement = (TryStatement) node;
            return statement.getClass().getSimpleName();

        }
        //while statement
        else {
            WhileStatement statement = (WhileStatement) node;
            return statement.getClass().getSimpleName();
        }
    }

    /**
     * Computes average McCabe's cyclomatic complexity. Code adapted from Eclipse
     * metrics plugin : http://sourceforge.net/projects/metrics2/
     *
     * @param node AStNode
     * @return complexity count
     */
    private Double handleMcCabe(ASTNode node) {
        Double cyclomatic = 0.0;
        if (node.getNodeType() == ASTNode.CATCH_CLAUSE) {
            cyclomatic++;
        } else if (node.getNodeType() == ASTNode.CONDITIONAL_EXPRESSION) {
            ConditionalExpression statement = (ConditionalExpression) node;
            if (statement.getExpression() != null)
                cyclomatic += inspectExpression(statement.getExpression().toString());
            cyclomatic++;
        } else if (node.getNodeType() == ASTNode.DO_STATEMENT) {
            DoStatement statement = (DoStatement) node;
            if (statement.getExpression() != null)
                cyclomatic += inspectExpression(statement.getExpression().toString());
            cyclomatic++;
        } else if (node.getNodeType() == ASTNode.FOR_STATEMENT) {
            ForStatement statement = (ForStatement) node;
            if (statement.getExpression() != null)
                cyclomatic += inspectExpression(statement.getExpression().toString());
            cyclomatic++;
        } else if (node.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT) {
            EnhancedForStatement statement = (EnhancedForStatement) node;
            if (statement.getExpression() != null)
                cyclomatic += inspectExpression(statement.getExpression().toString());
            cyclomatic++;
        } else if (node.getNodeType() == ASTNode.IF_STATEMENT) {
            IfStatement statement = (IfStatement) node;
            if (statement.getExpression() != null)
                cyclomatic += inspectExpression(statement.getExpression().toString());
            cyclomatic++;
        } else if (node.getNodeType() == ASTNode.SWITCH_CASE) {
            SwitchCase statement = (SwitchCase) node;
            if (!statement.isDefault())
                cyclomatic++;
        } else if (node.getNodeType() == ASTNode.WHILE_STATEMENT) {
            WhileStatement statement = (WhileStatement) node;
            if (statement.getExpression() != null)
                cyclomatic += inspectExpression(statement.getExpression().toString());
            cyclomatic++;
        } else if (node.getNodeType() == ASTNode.EXPRESSION_STATEMENT) {
            ExpressionStatement statement = (ExpressionStatement) node;
            if (statement.getExpression() != null)
                cyclomatic += inspectExpression(statement.getExpression().toString());
        } else if (node.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
            VariableDeclarationFragment statement = (VariableDeclarationFragment) node;
            if (statement.getInitializer() != null)
                cyclomatic += inspectExpression(statement.getInitializer().toString());
        }
        return cyclomatic;
    }

    /**
     * Count occurrences of && and || (conditional and or)
     *
     * @param ex expression as string
     * @return complexity
     */
    private Double inspectExpression(String ex) {
        Double cyclomatic = 0.0;
        if (ex != null) {
            char[] chars = ex.toCharArray();
            for (int i = 0; i < chars.length - 1; i++) {
                char next = chars[i];
                if ((next == '&' || next == '|') && (next == chars[i + 1])) {
                    cyclomatic++;
                }
            }
        }
        return cyclomatic;
    }
}
