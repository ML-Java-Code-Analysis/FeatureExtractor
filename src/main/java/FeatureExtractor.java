/**
 * Created on 05.04.2016.
 *
 * @author ymeke
 */

import model.Version;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import repository.Git;
import utils.Config;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("main");
        logger.log(Level.INFO, "Starting Feature Extractor.");

        logger.log(Level.INFO, "Reading Arguments.");
        Config cfg = new Config();
        cfg.parse(args);
        logger.log(Level.INFO, "Reading Config Files.");
        cfg.readConfigFile();

        // TODO Relevante Fileversionen aus DB holen
        List<Version> versions = new ArrayList<Version>();
        // TODO Repository Path aus DB holen
        String repositoryPath = "C:\\Users\\ymeke\\Documents\\Studium\\BA\\Test_Repositories\\LED-Cube-Prototyper";

        // Repository intialisieren
        Git git = null;
        try {
            git = new Git(repositoryPath);
        } catch (IOException e) {
            //TODO: Exit program
            e.printStackTrace();
        }
        // TODO Für jede Fileversion
        for (Version version : versions) {
            String path = version.getFile().getPath();
            String commitId = version.getCommitId();

            try {
                char[] code = git.getSourceCode(path, commitId);
                System.out.println(code);
            } catch (FileNotFoundException e) {
                logger.log(Level.INFO, "File " + path + " was not found. Skipping this one.");
                continue;
            }

            // TODO - AST aus Code parsen
            // TODO - Alle Feature Extractors drüberlaufen lassen
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

}
