/**
 * Created on 05.04.2016.
 *
 * @author ymeke
 */

package ba.ciel5.featureExtractor;

import ba.ciel5.featureExtractor.features.IFeatureGroup;
import ba.ciel5.featureExtractor.model.FeatureValue;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.model.Repository;
import ba.ciel5.featureExtractor.utils.AbstractSyntaxTreeUtil;
import ba.ciel5.featureExtractor.utils.HibernateUtil;
import javafx.util.Pair;
import org.apache.commons.cli.ParseException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.hibernate.HibernateError;
import org.reflections.Reflections;
import ba.ciel5.featureExtractor.repository.Git;
import ba.ciel5.featureExtractor.utils.Config;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FeatureExtractor {
    private static Logger logger;
    private static Git git;

    public static void main(String[] args) {
        logger = Logger.getLogger("main");
        logger.log(Level.INFO, "Starting IFeature Extractor.");

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

        // Get Repository from DB
        List<Repository> repositories = null;
        try {
            repositories = HibernateUtil.complexQuery("FROM Repository WHERE name = :name", new ArrayList(Arrays.asList(new Pair("name", cfg.getRepositoryName()))));
        } catch (HibernateError e) {
            logger.log(Level.SEVERE, "DB Query failed", e);
        }
        if (repositories.size() != 1) {
            logger.log(Level.SEVERE, "No repository found or more than one found with name " + cfg.getRepositoryName());
            exit();
        }
        Repository repository = repositories.get(0);
        String repositoryPath = repository.getUrl();

        // Get all versions (except deleted ones)
        List<Version> versions = null;
        try {
            versions = HibernateUtil.complexQuery("SELECT version FROM Commit as c INNER JOIN c.versions AS version WHERE c.repositoryId = :repositoryId AND version.deleted = FALSE", new ArrayList(Arrays.asList(new Pair("repositoryId", Integer.parseInt(repository.getId())))));
        } catch (HibernateError e) {
            logger.log(Level.SEVERE, "DB Query failed", e);
        }

        // Repository intialisieren
        try {
            git = new Git(repositoryPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Repository " + repositoryPath + " could not be read.", e);
            exit();
        }

        List<IFeatureGroup> featureGroups = getFeatureGroups();

        int log_interval = 1, i = 1;
        if (versions.size() > 1000)
            log_interval = 100;

        for (Version version : versions) {
            String path = version.getPath();
            String commitId = version.getCommitId();

            if (i % log_interval == 0) {
                double prc = (double) i / versions.size() * 100.0;
                logger.log(Level.INFO, Math.round(prc * 100.0) / 100.0 + "% - processed versions: " + i);
            }
            i++;

            try {
                char[] code = git.getSourceCode(path, commitId);
                CompilationUnit ast = AbstractSyntaxTreeUtil.parse(code);
                for (IFeatureGroup featureGroup : featureGroups) {
                    Map<String, Double> features = featureGroup.extract(version, ast, code);

                    Iterator<Map.Entry<String, Double>> it = features.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Double> feature = it.next();
                        String featureId = feature.getKey();
                        Double value = feature.getValue();
                        it.remove(); // avoids a ConcurrentModificationException

                        // TODO: uncomment here to write feature to DB
                        try {
                            FeatureValue.addOrUpdateFeatureValue(featureId, version.getId(), value);
                        } catch (HibernateError e) {
                            logger.log(Level.SEVERE, "Could not add Feature: " + featureId + " with value: " + value, e);
                        }
                    }
                }
            } catch (IOException e) {
                String msg = "There was a problem with the file " + path +
                        " from commit " + commitId + ". Skipping this one.";
                logger.log(Level.WARNING, msg, e);
            }
        }

        git.closeRepository();
        logger.log(Level.INFO, "ba.ciel5.featureExtractor.FeatureExtractor is done. See ya!");
        exit();
    }

    private static void exit() {
        logger.log(Level.WARNING, "Quitting program.");
        git.closeRepository();
        System.exit(0);
    }

    private static List<IFeatureGroup> getFeatureGroups() {
        List<IFeatureGroup> featureGroups = new ArrayList<IFeatureGroup>();
        Set<Class<? extends IFeatureGroup>> featureGroupClasses = getFeatureGroupClasses();
        for (Class<? extends IFeatureGroup> featureGroupClass : featureGroupClasses) {
            try {
                IFeatureGroup featureGroup = featureGroupClass.newInstance();
                featureGroups.add(featureGroup);
                logger.log(Level.INFO, "Instantiated IFeatureGroup " + featureGroupClass.getName());
            } catch (InstantiationException e) {
                String message = String.format("Could not instantiate IFeatureGroup %s. Message: %s",
                        featureGroupClass.getName(), e.getMessage());
                logger.log(Level.WARNING, message);
            } catch (IllegalAccessException e) {
                String message = String.format("Could not instantiate IFeatureGroup %s. Message: %s",
                        featureGroupClass.getName(), e.getMessage());
                logger.log(Level.WARNING, message);
            }
        }
        return featureGroups;
    }

    private static Set<Class<? extends IFeatureGroup>> getFeatureGroupClasses() {
        Reflections reflections = new Reflections("ba.ciel5.featureExtractor.features");
        return reflections.getSubTypesOf(IFeatureGroup.class);
    }

}
