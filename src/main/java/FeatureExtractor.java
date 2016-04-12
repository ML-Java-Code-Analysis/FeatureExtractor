/**
 * Created on 05.04.2016.
 *
 * @author ymeke
 */

import features.Feature;
import model.Version;
import org.apache.commons.cli.ParseException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.reflections.Reflections;
import repository.Git;
import utils.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FeatureExtractor {
    private static Logger logger;
    private static Git git;

    public static void main(String[] args) {
        logger = Logger.getLogger("main");
        logger.log(Level.INFO, "Starting Feature Extractor.");

        logger.log(Level.INFO, "Reading Arguments.");
        Config cfg = null;
        try {
            cfg = new Config();
            cfg.parse(args);
        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Arguments could not be parsed.", e);
            exit();
        }
        logger.log(Level.INFO, "Reading Config Files.");
        try {
            cfg.readConfigFile();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Config File could not be read.", e);
            exit();
        }

        // TODO Relevante Fileversionen aus DB holen
        List<Version> versions = new ArrayList<Version>();
        // TODO Repository Path aus DB holen
        String repositoryPath = "C:\\Users\\ymeke\\Documents\\Studium\\BA\\Test_Repositories\\LED-Cube-Prototyper";

        // Repository intialisieren
        try {
            git = new Git(repositoryPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Repository " + repositoryPath + " could not be read.", e);
            exit();
        }

        List<Feature> features = getFeatures();
        for (Version version : versions) {
            String path = version.getFile().getPath();
            String commitId = version.getCommitId();

            try {
                char[] code = git.getSourceCode(path, commitId);
                CompilationUnit ast = parse(code);
                for (Feature feature : features) {
                    double value = feature.extract(ast, code);
                    String featureId = feature.getFeatureId();

                    //FeatureValue.addOrUpdateFeatureValue(featureId, version.getId(), value);
                }
            } catch (IOException e) {
                String msg = "There was a problem with the file " + path +
                        " from commit " + commitId + ". Skipping this one.";
                logger.log(Level.WARNING, msg, e);
            }
        }

        git.closeRepository();
        logger.log(Level.INFO, "FeatureExtractor is done. See ya!");
    }

    private static void exit(){
        logger.log(Level.WARNING, "Quitting program.");
        git.closeRepository();
        System.exit(0);
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
            try {
                Feature feature = featureClass.newInstance();
                features.add(feature);
                logger.log(Level.INFO, "Instantiated Feature " + featureClass.getName());
            } catch (InstantiationException e) {
                String message = String.format("Could not instantiate Feature %s. Message: %s",
                        featureClass.getName(), e.getMessage());
                logger.log(Level.WARNING, message);
            } catch (IllegalAccessException e) {
                String message = String.format("Could not instantiate Feature %s. Message: %s",
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
