/**
 * Created on 05.04.2016.
 *
 * @author ymeke
 */

import features.Feature;
import model.Version;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.reflections.Reflections;
import repository.Git;
import utils.Config;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor {
    private static Logger logger;

    public static void main(String[] args) {
        logger = Logger.getLogger("main");
        logger.log(Level.INFO, "Starting Feature Extractor.");

        logger.log(Level.INFO, "Reading Arguments.");
        Config cfg = new Config();
        cfg.parse(args);
        logger.log(Level.INFO, "Reading Config Files.");
        //cfg.readConfigFile();

        // TODO Relevante Fileversionen aus DB holen
        List<Version> versions = new ArrayList<Version>();
        // TODO Repository Path aus DB holen
        String repositoryPath = "C:\\Users\\ymeke\\Documents\\Studium\\BA\\Test_Repositories\\LED-Cube-Prototyper";

        // Repository intialisieren
        Git git = null;
        try {
            git = new Git(repositoryPath);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: Exit program
            return;
        }
        // TODO Für jede Fileversion
        List<Feature> features = getFeatures();
        for (Version version : versions) {
            String path = version.getFile().getPath();
            String commitId = version.getCommitId();

            try {
                char[] code = git.getSourceCode(path, commitId);
                System.out.println(code);
                CompilationUnit ast = parse(code);
                for (Feature feature : features) {
                    double value = feature.extract(ast, code);
                    String featureId = feature.getFeatureId();

                    //FeatureValue.addOrUpdateFeatureValue(featureId, version.getId(), value);
                }

            } catch (FileNotFoundException e) {
                logger.log(Level.INFO, "File " + path + " was not found. Skipping this one.");
            }

        }

        //  - File/Source-Code aus git holen (repository-package)
        try {
            char[] code = git.getSourceCode("Controller.py", "31b6d396ba14fcb0e61650937f5d1754c10958bf");
            System.out.println(code);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        git.closeRepository();

    }

    private static CompilationUnit parse(char[] code) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(code);
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null);
    }

    private static List<Feature> getFeatures() {
        List<Feature> features = new ArrayList<Feature>();
        for (Class<? extends Feature> featureClass : getFeatureClasses()) {
            Feature feature = null;
            try {
                feature = featureClass.newInstance();
                features.add(feature);
                logger.log(Level.INFO, "Instantiated Feature " + featureClass.getName());
            } catch (InstantiationException e) {
                String message = String.format("Could not instantiate Feature %s. Message%s",
                        featureClass.getName(), e.getMessage());
                logger.log(Level.WARNING, message);
            } catch (IllegalAccessException e) {
                String message = String.format("Could not instantiate Feature %s. Message%s",
                        featureClass.getName(), e.getMessage());
                logger.log(Level.WARNING, message);
            }
        }
        return features;
    }

    private static Set<Class<? extends Feature>> getFeatureClasses() {
        Reflections reflections = new Reflections("features");
        return reflections.getSubTypesOf(Feature.class);
    }

}
