package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.File;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.utils.HibernateUtil;
import javafx.util.Pair;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.hibernate.HibernateError;
import sun.nio.cs.HistoricallyNamedCharset;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created on 22.04.2016.
 *
 * @author tobias.meier
 */
public class ChangeRateFeatureGroup implements IFeatureGroup {

    private static Logger logger;

    public Map<String, Double> extract(Version version, CompilationUnit ast, char[] code) {

        logger = Logger.getLogger("main");

        double daysBetweenLastCommit=0;

        double numberOfAddedLinesLastDay=0;
        double numberOfAddedLinesLastWeek=0;
        double numberOfAddedLinesLastMonth=0;
        double numberOfAddedLinesLastThreeMonths=0;
        double numberOfAddedLinesLastSixMonths=0;

        double numberOfDeletedLinesLastDay=0;
        double numberOfDeletedLinesLastWeek=0;
        double numberOfDeletedLinesLastMonth=0;
        double numberOfDeletedLinesLastThreeMonths=0;
        double numberOfDeletedLinesLastSixMonths=0;

        double numberOfAuthorsLastDay=0;
        double numberOfAuthorsLastWeek=0;
        double numberOfAuthorsLastMonth=0;
        double numberOfAuthorsLastThreeMonths=0;
        double numberOfAuthorsLastSixMonths=0;

        double numberOfBugsLastDay=0;
        double numberOfBugsLastWeek=0;
        double numberOfBugsLastMonth=0;
        double numberOfBugsLastThreeMonths=0;
        double numberOfBugsLastSixMonths=0;

        double numberOfEnhancementsLastDay=0;
        double numberOfEnhancementsLastWeek=0;
        double numberOfEnhancementsLastMonth=0;
        double numberOfEnhancementsLastThreeMonths=0;
        double numberOfEnhancementsLastSixMonths=0;

        // Get file which belongs to this version
        File file = HibernateUtil.fetchLazyContent(version.getFile());

        System.out.println(file.getId());

        // Get the commit which belongs to that file
        Commit commit = HibernateUtil.fetchLazyContent(version.getCommit());

        // Get all versions which belong to that file
        List<Version> versions = null;
        try {
            versions = HibernateUtil.complexQuery(
                            "FROM Version as v WHERE v.fileId = :fileId",
                    new ArrayList(Arrays.asList(new Pair("fileId",file.getId()))));
        } catch (HibernateError e) {
            logger.log(Level.SEVERE, "DB Query failed", e);
        }

        // Get all commits which belong to that file
        List<Commit> commits = versions.stream().map(v -> HibernateUtil.fetchLazyContent(v.getCommit())).collect(Collectors.toList());

        daysBetweenLastCommit = getDaysBetweenLastCommit(commit, commits);

        double numberOfChangedLinesLastDay=numberOfAddedLinesLastDay+numberOfDeletedLinesLastDay;
        double numberOfChangedLinesLastWeek=numberOfAddedLinesLastWeek+numberOfDeletedLinesLastWeek;
        double numberOfChangedLinesLastMonth=numberOfAddedLinesLastMonth+numberOfDeletedLinesLastMonth;
        double numberOfChangedLinesLastThreeMonths=numberOfAddedLinesLastThreeMonths+numberOfDeletedLinesLastThreeMonths;
        double numberOfChangedLinesLastSixMonths=numberOfAddedLinesLastSixMonths+numberOfDeletedLinesLastSixMonths;


        Map<String, Double> map = new HashMap<String, Double>();
        map.put("DBLC", daysBetweenLastCommit);
        return map;
    }

    private double getDaysBetweenLastCommit(Commit commit, List<Commit> commits) {

        //sort commit by timestamp
        List<Commit> sortedCommits = commits
                .stream()
                .sorted((c1, c2) -> c1.getTimestamp().compareTo(c2.getTimestamp()))
                .collect(Collectors.toList());

        //Filter commits. Delete all that are after the actual commit
        List<Long> dateDifference = sortedCommits
                .stream()
                .map(c -> commit.getTimestamp().getTime()-c.getTimestamp().getTime())
                .filter(d -> d > 0)
                .collect(Collectors.toList());

        //There is no commit before the actual
        if ( dateDifference.size() == 0 )
            return 0;

        return dateDifference.get(dateDifference.size()-1)/1000.0/3600.0/24.0;
    }
}
