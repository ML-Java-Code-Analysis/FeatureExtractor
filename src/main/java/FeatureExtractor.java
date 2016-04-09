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
        // Hier kommt der Start-Code rein

        // CLI Parameter und Config aus .cfg File einlesen
        // Relevante Fileversionen aus DB holen
        // Für jede Fileversion:


        Config cfg = new Config();
        cfg.parse(args);
        cfg.readConfigFile();


        Git git = new Git("P:\\Studium\\FS2016\\BA\\GitHubProjects\\LED-Cube-Prototyper");
        try {
            char[] code = git.getSourceCode("Controller.py","31b6d396ba14fcb0e61650937f5d1754c10958bf");
            System.out.println(code);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //  - File/Source-Code aus git holen (repository-package)
        //  - AST aus Code parsen
        //  - Alle Feature Extractors drüberlaufen lassen
    }

}
