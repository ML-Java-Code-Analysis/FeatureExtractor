package ba.ciel5.featureExtractor.features;

import com.sun.org.apache.xpath.internal.operations.Variable;
import org.eclipse.jdt.core.dom.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created on 08.05.2016.
 *
 * @author tobias.meier
 */
public class OutputSyntaxTree {
    private static final String SOURCE_PATH = "src\\main\\java\\MyClass.java";

    public static void main(String[] args) {
        char[] code = TestClass1.getTestCode().toCharArray();
        System.out.println(code);

        CompilationUnit ast = parse(code);

        // Print imports
        printSeparator();
        System.out.println("Imports:");
        for (Object importElement : ast.imports()) {
            if (importElement instanceof ImportDeclaration) {
                ImportDeclaration importDeclaration = (ImportDeclaration) importElement;
                System.out.println("\t- " + importDeclaration.getName());
            }
        }

        printSeparator();
        System.out.println("Comments:");
        for (Object comment : ast.getCommentList()) {
            try {
                Comment commentNode = (Comment) comment;
                String prefix = null;
                if (commentNode.isBlockComment()) {
                    prefix = "[Block]";
                } else if (commentNode.isDocComment()) {
                    prefix = "[Doc]";
                } else if (commentNode.isLineComment()) {
                    prefix = "[Line]";
                }

                // a bit cumbersome, but this seems to be the way to read the actual comment
                String commentString = "";
                int start = commentNode.getStartPosition();
                int length = commentNode.getLength();
                for (int i = start; i < start + length; i++) {
                    commentString += code[i];
                }

                System.out.println("\t- " + prefix + " " + commentString);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        // Print class
        for (Object type : ast.types()) {
            try {
                TypeDeclaration typeNode = (TypeDeclaration) type;

                printSeparator();
                System.out.println("Class: " + typeNode.getName() + "\n");

                System.out.println("\t- Fields: ");
                for (FieldDeclaration field : typeNode.getFields()) {
                    System.out.println("\t\t- " + field.getType() + " " + field.fragments());
                }

                System.out.println("\t- Methods: ");
                for (MethodDeclaration method : typeNode.getMethods()) {
                    String modifiersString = "";
                    for (Object modifier : method.modifiers()) {
                        modifiersString += ((Modifier) modifier).getKeyword().toString();
                    }
                    Object returnType = method.getReturnType2();
                    if (method.isConstructor()) {
                        returnType = typeNode.getName();
                    }
                    System.out.println("\t\t- [" + modifiersString + "] " + method.getName() + " -> " + returnType);

                    // Visitor-Pattern this mofo method
                    printSmallSeparator();
                    ASTVisitor visitor = new ASTVisitor() {
                        private String getNodeTypeString(ASTNode node) {
                            return " (" + node.getNodeType() + ", " + node.getClass().getSimpleName() + ")";
                        }

                        private int getDepth(ASTNode node) {
                            return getDepth(node, 0);
                        }

                        private int getDepth(ASTNode node, int depth) {
                            if(node.getParent() == null) {
                                return depth;
                            }
                            return getDepth(node.getParent(), ++depth);
                        }

                        private String getNodeIndent(ASTNode node) {
                            String indent = "";
                            int depth = getDepth(node);
                            for (int i = 0; i < depth; i++) {
                                indent += "\t";
                            }
                            return indent;
                        }

                        @Override
                        public void preVisit(ASTNode node) {
                            System.out.println(getNodeIndent(node) + "|- preVisit " + node + " " + getNodeTypeString(node));
                            super.preVisit(node);
                        }
                        /*
                        @Override
                        public void postVisit(ASTNode node) {
                            System.out.println("postVisit " + getNodeTypeString(node));
                            super.postVisit(node);
                        }

                        @Override
                        public boolean visit(VariableDeclarationExpression node) {
                            System.out.println("VariableDeclarationExpression: " + node);
                            return super.visit(node);
                        }

                        @Override
                        public boolean visit(ExpressionStatement node) {
                            System.out.println("ExpressionStatement: " + node);
                            return super.visit(node);
                        }*/
                    };
                    method.accept(visitor);

                    printSmallSeparator();
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
        ASTVisitor simpleVisitor = new ASTVisitor() {
            private String getNodeTypeString(ASTNode node) {
                return " (" + node.getNodeType() + ", " + node.getClass().getSimpleName() + ")";
            }

            public void preVisit(ASTNode node) {
                System.out.println(node + " " + getNodeTypeString(node));
                super.preVisit(node);
            }

        };
        ast.accept(simpleVisitor);
    }

    public static CompilationUnit parse(char[] code) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(code);
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null);
    }

    public static char[] readSourceFile(String path) {
        char[] code = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String codeString = "";
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                codeString += currentLine + "\n";
            }

            code = codeString.toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }

    private static void printSeparator() {
        System.out.println("\n---------------------------------------------------------------");
    }

    private static void printSmallSeparator() {
        System.out.println("\n- - - - - - - - - - - - - - ");
    }
}
