import repository.Git;
import utils.Config;
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


        Git git = new Git(1);
        git.getSourceCode("","d347d2939fb04d4634a398b6d331dab6acfd75f4");
        //  - File/Source-Code aus git holen (repository-package)
        //  - AST aus Code parsen
        //  - Alle Feature Extractors drüberlaufen lassen
    }

}
