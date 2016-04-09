import repository.Git;
import utils.Config;

import java.io.FileNotFoundException;

/**
 * Created on 05.04.2016.
 *
 * @author ymeke
 */
public class FeatureExtractor {

    public static void main(String[] args) {

        Config cfg = new Config();
        //Arugment parsing
        cfg.parse(args);
        //Read config file
        cfg.readConfigFile();

        // TODO Relevante Fileversionen aus DB holen
        // TODO Repository Path auf DB holen
        // Repository intialisieren
        Git git = new Git("P:\\Studium\\FS2016\\BA\\GitHubProjects\\LED-Cube-Prototyper");
        // TODO Für jede Fileversion

        //  - File/Source-Code aus git holen (repository-package)
        try {
            char[] code = git.getSourceCode("Controller.py","31b6d396ba14fcb0e61650937f5d1754c10958bf");
            System.out.println(code);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        git.closeRepository();

        // TODO - AST aus Code parsen
        // TODO - Alle Feature Extractors drüberlaufen lassen
    }

}
