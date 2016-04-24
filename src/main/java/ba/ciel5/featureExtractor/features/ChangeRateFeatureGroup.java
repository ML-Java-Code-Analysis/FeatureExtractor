package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.File;
import ba.ciel5.featureExtractor.model.Issue;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.utils.HibernateUtil;
import javafx.util.Pair;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.hibernate.HibernateError;

import java.util.*;
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

    private enum IssueType {
        BUG, ENHANCEMENT, OTHER
    }

    public Map<String, Double> extract(Version version, CompilationUnit ast, char[] code) {

        logger = Logger.getLogger("main");

        double daysBetweenLastCommit = 0;

        double numberOfAddedLinesLastDay = 0;
        double numberOfAddedLinesLastWeek = 0;
        double numberOfAddedLinesLastMonth = 0;
        double numberOfAddedLinesLastThreeMonths = 0;
        double numberOfAddedLinesLastSixMonths = 0;
        double numberOfAddedLinesLastTwelfMonths = 0;
        double numberOfAddedLinesLastTwentyFourMonths = 0;

        double numberOfDeletedLinesLastDay = 0;
        double numberOfDeletedLinesLastWeek = 0;
        double numberOfDeletedLinesLastMonth = 0;
        double numberOfDeletedLinesLastThreeMonths = 0;
        double numberOfDeletedLinesLastSixMonths = 0;
        double numberOfDeletedLinesLastTwelfMonths = 0;
        double numberOfDeletedLinesLastTwentyFourMonths = 0;

        double numberOfAuthorsLastDay = 0;
        double numberOfAuthorsLastWeek = 0;
        double numberOfAuthorsLastMonth = 0;
        double numberOfAuthorsLastThreeMonths = 0;
        double numberOfAuthorsLastSixMonths = 0;
        double numberOfAuthorsLastTwelfMonths = 0;
        double numberOfAuthorsLastTwentyFourMonths = 0;

        double numberOfBugsLastDay = 0;
        double numberOfBugsLastWeek = 0;
        double numberOfBugsLastMonth = 0;
        double numberOfBugsLastThreeMonths = 0;
        double numberOfBugsLastSixMonths = 0;
        double numberOfBugsLastTwelfMonths = 0;
        double numberOfBugsLastTwentyFourMonths = 0;

        double numberOfEnhancementsLastDay = 0;
        double numberOfEnhancementsLastWeek = 0;
        double numberOfEnhancementsLastMonth = 0;
        double numberOfEnhancementsLastThreeMonths = 0;
        double numberOfEnhancementsLastSixMonths = 0;
        double numberOfEnhancementsLastTwelfMonths = 0;
        double numberOfEnhancementsLastTwentyFourMonths = 0;

        // Get file which belongs to this version
        File file = HibernateUtil.fetchLazyContent(version.getFile());

        // Get the commit which belongs to that file
        Commit commit = HibernateUtil.fetchLazyContent(version.getCommit());

        // Get all versions which belong to that file
        List<Version> versions = null;
        try {
            versions = HibernateUtil.complexQuery(
                    "FROM Version as v WHERE v.fileId = :fileId",
                    new ArrayList(Arrays.asList(new Pair("fileId", file.getId()))));
        } catch (HibernateError e) {
            logger.log(Level.SEVERE, "DB Query failed", e);
        }

        // Get all commits which belong to that file
        List<Commit> commits = versions.stream().map(v -> HibernateUtil.fetchLazyContent(v.getCommit())).collect(Collectors.toList());

        //Fetch all versions (Lazy fetching)
        commits.forEach(c -> HibernateUtil.fetchLazyContent(c, c.getVersions()));

        //Fetch all issues (Lazy fetching)
        commits.forEach(c -> HibernateUtil.fetchLazyContent(c, c.getIssues()));

        List<Commit> sortedCommits = getSortedCommitsByTimestamp(commits);
        List<Commit> olderCommits = getAllOlderCommits(commit, sortedCommits);

        //System.out.println(file.getId());
        //System.out.println("commit: " + commit.getMessage());

        daysBetweenLastCommit = getDaysBetweenLastCommit(commit, olderCommits);

        numberOfAddedLinesLastDay = getNumberOfAddedLinesForDays(1, commit, olderCommits);
        numberOfAddedLinesLastWeek = getNumberOfAddedLinesForDays(7, commit, olderCommits);
        numberOfAddedLinesLastMonth = getNumberOfAddedLinesForDays(30, commit, olderCommits);
        numberOfAddedLinesLastThreeMonths = getNumberOfAddedLinesForDays(90, commit, olderCommits);
        numberOfAddedLinesLastSixMonths = getNumberOfAddedLinesForDays(180, commit, olderCommits);
        numberOfAddedLinesLastTwelfMonths = getNumberOfAddedLinesForDays(365, commit, olderCommits);
        numberOfAddedLinesLastTwentyFourMonths = getNumberOfAddedLinesForDays(730, commit, olderCommits);

        numberOfDeletedLinesLastDay = getNumberOfDeletedLinesForDays(1, commit, olderCommits);
        numberOfDeletedLinesLastWeek = getNumberOfDeletedLinesForDays(7, commit, olderCommits);
        numberOfDeletedLinesLastMonth = getNumberOfDeletedLinesForDays(30, commit, olderCommits);
        numberOfDeletedLinesLastThreeMonths = getNumberOfDeletedLinesForDays(90, commit, olderCommits);
        numberOfDeletedLinesLastSixMonths = getNumberOfDeletedLinesForDays(180, commit, olderCommits);
        numberOfDeletedLinesLastTwelfMonths = getNumberOfDeletedLinesForDays(365, commit, olderCommits);
        numberOfDeletedLinesLastTwentyFourMonths = getNumberOfDeletedLinesForDays(730, commit, olderCommits);

        double numberOfChangedLinesLastDay = numberOfAddedLinesLastDay + numberOfDeletedLinesLastDay;
        double numberOfChangedLinesLastWeek = numberOfAddedLinesLastWeek + numberOfDeletedLinesLastWeek;
        double numberOfChangedLinesLastMonth = numberOfAddedLinesLastMonth + numberOfDeletedLinesLastMonth;
        double numberOfChangedLinesLastThreeMonths = numberOfAddedLinesLastThreeMonths + numberOfDeletedLinesLastThreeMonths;
        double numberOfChangedLinesLastSixMonths = numberOfAddedLinesLastSixMonths + numberOfDeletedLinesLastSixMonths;
        double numberOfChangedLinesLastTwelfMonths = numberOfAddedLinesLastTwelfMonths + numberOfDeletedLinesLastTwelfMonths;
        double numberOfChangedLinesLastTwentyFourMonths = numberOfAddedLinesLastTwentyFourMonths + numberOfDeletedLinesLastTwentyFourMonths;

        numberOfAuthorsLastDay = getNumberOfAuthorsForDays(1, commit, olderCommits);
        numberOfAuthorsLastWeek = getNumberOfAuthorsForDays(7, commit, olderCommits);
        numberOfAuthorsLastMonth = getNumberOfAuthorsForDays(30, commit, olderCommits);
        numberOfAuthorsLastThreeMonths = getNumberOfAuthorsForDays(90, commit, olderCommits);
        numberOfAuthorsLastSixMonths = getNumberOfAuthorsForDays(180, commit, olderCommits);
        numberOfAuthorsLastTwelfMonths = getNumberOfAuthorsForDays(365, commit, olderCommits);
        numberOfAuthorsLastTwentyFourMonths = getNumberOfAuthorsForDays(730, commit, olderCommits);

//        numberOfBugsLastDay = getNumberOfIssuesForDays(1, commit, olderCommits, IssueType.BUG.toString());
//        numberOfBugsLastWeek = getNumberOfIssuesForDays(7, commit, olderCommits, IssueType.BUG.toString());
//        numberOfBugsLastMonth = getNumberOfIssuesForDays(30, commit, olderCommits, IssueType.BUG.toString());
//        numberOfBugsLastThreeMonths = getNumberOfIssuesForDays(90, commit, olderCommits, IssueType.BUG.toString());
//        numberOfBugsLastSixMonths = getNumberOfIssuesForDays(180, commit, olderCommits, IssueType.BUG.toString());
//        numberOfBugsLastTwelfMonths = getNumberOfIssuesForDays(365, commit, olderCommits, IssueType.BUG.toString());
//        numberOfBugsLastTwentyFourMonths = getNumberOfIssuesForDays(730, commit, olderCommits, IssueType.BUG.toString());
//
//        numberOfEnhancementsLastDay = getNumberOfIssuesForDays(1, commit, olderCommits, IssueType.ENHANCEMENT.toString());
//        numberOfEnhancementsLastWeek = getNumberOfIssuesForDays(7, commit, olderCommits, IssueType.ENHANCEMENT.toString());
//        numberOfEnhancementsLastMonth = getNumberOfIssuesForDays(30, commit, olderCommits, IssueType.ENHANCEMENT.toString());
//        numberOfEnhancementsLastThreeMonths = getNumberOfIssuesForDays(90, commit, olderCommits, IssueType.ENHANCEMENT.toString());
//        numberOfEnhancementsLastSixMonths = getNumberOfIssuesForDays(180, commit, olderCommits, IssueType.ENHANCEMENT.toString());
//        numberOfEnhancementsLastTwelfMonths = getNumberOfIssuesForDays(365, commit, olderCommits, IssueType.ENHANCEMENT.toString());
//        numberOfEnhancementsLastTwentyFourMonths = getNumberOfIssuesForDays(730, commit, olderCommits, IssueType.ENHANCEMENT.toString());

        if ( numberOfEnhancementsLastTwentyFourMonths > 0 )
            System.out.println("Enhancement in 2 years: " + numberOfEnhancementsLastTwentyFourMonths);

        if ( numberOfBugsLastTwentyFourMonths > 0 )
            System.out.println("Bug in 2 years: " + numberOfBugsLastTwentyFourMonths);

        Map<String, Double> map = new HashMap<String, Double>();
        map.put("DBLC", daysBetweenLastCommit);
        map.put("NALLD", numberOfAddedLinesLastDay);
        map.put("NALL7D", numberOfAddedLinesLastWeek);
        map.put("NALL30D", numberOfAddedLinesLastMonth);
        map.put("NALL90D", numberOfAddedLinesLastThreeMonths);
        map.put("NALL180D", numberOfAddedLinesLastSixMonths);
        map.put("NALL365D", numberOfAddedLinesLastTwelfMonths);
        map.put("NALL730D", numberOfAddedLinesLastTwentyFourMonths);
        map.put("NDLLD", numberOfDeletedLinesLastDay);
        map.put("NDLL7D", numberOfDeletedLinesLastWeek);
        map.put("NDLL30D", numberOfDeletedLinesLastMonth);
        map.put("NDLL90D", numberOfDeletedLinesLastThreeMonths);
        map.put("NDLL180D", numberOfDeletedLinesLastSixMonths);
        map.put("NDLL365D", numberOfDeletedLinesLastTwelfMonths);
        map.put("NDLL730D", numberOfDeletedLinesLastTwentyFourMonths);
        map.put("NCLLD", numberOfChangedLinesLastDay);
        map.put("NCLL7D", numberOfChangedLinesLastWeek);
        map.put("NCLL30D", numberOfChangedLinesLastMonth);
        map.put("NCLL90D", numberOfChangedLinesLastThreeMonths);
        map.put("NCLL180D", numberOfChangedLinesLastSixMonths);
        map.put("NCLL365D", numberOfChangedLinesLastTwelfMonths);
        map.put("NCLL730D", numberOfChangedLinesLastTwentyFourMonths);
        map.put("NALD", numberOfAuthorsLastDay);
        map.put("NAL7D", numberOfAuthorsLastWeek);
        map.put("NAL30D", numberOfAuthorsLastMonth);
        map.put("NAL90D", numberOfAuthorsLastThreeMonths);
        map.put("NAL180D", numberOfAuthorsLastSixMonths);
        map.put("NAL365D", numberOfAuthorsLastTwelfMonths);
        map.put("NAL730D", numberOfAuthorsLastTwentyFourMonths);
        map.put("NOBD", numberOfBugsLastDay);
        map.put("NOB7D", numberOfBugsLastWeek);
        map.put("NOB30D", numberOfBugsLastMonth);
        map.put("NOB90D", numberOfBugsLastThreeMonths);
        map.put("NOB180D", numberOfBugsLastSixMonths);
        map.put("NOB365D", numberOfBugsLastTwelfMonths);
        map.put("NOB730D", numberOfBugsLastTwentyFourMonths);
        map.put("NOED", numberOfEnhancementsLastDay);
        map.put("NOE7D", numberOfEnhancementsLastWeek);
        map.put("NOE30D", numberOfEnhancementsLastMonth);
        map.put("NOE90D", numberOfEnhancementsLastThreeMonths);
        map.put("NOE180D", numberOfEnhancementsLastSixMonths);
        map.put("NOE365D", numberOfEnhancementsLastTwelfMonths);
        map.put("NOE730D", numberOfEnhancementsLastTwentyFourMonths);

        return map;
    }

    /**
     * return commit list as sorted list
     *
     * @param commits commit list
     * @return sorted commit list
     */
    private List<Commit> getSortedCommitsByTimestamp(List<Commit> commits) {
        List<Commit> sortedCommits = commits
                .stream()
                .sorted((c1, c2) -> c1.getTimestamp().compareTo(c2.getTimestamp()))
                .collect(Collectors.toList());
        return sortedCommits;
    }

    /**
     * get a list of time difference in milliseconds between commit and every commit in list commits
     *
     * @param commit  reference commit
     * @param commits commit list
     * @return time difference list
     */
    private List<Long> getDateDifference(Commit commit, List<Commit> commits) {
        List<Long> dateDifference = commits
                .stream()
                .map(c -> commit.getTimestamp().getTime() - c.getTimestamp().getTime())
                .collect(Collectors.toList());
        return dateDifference;
    }

    /**
     * Filter commits.Get all commits that are older than commit
     *
     * @param commit        commit as reference
     * @param sortedCommits sorted commit list to compare
     * @return filtered commit list
     */
    private List<Commit> getAllOlderCommits(Commit commit, List<Commit> sortedCommits) {
        List<Commit> olderCommits = sortedCommits
                .stream()
                .filter(c -> commit.getTimestamp().getTime() > c.getTimestamp().getTime())
                .collect(Collectors.toList());
        return olderCommits;
    }

    /**
     * Get commits that are in time range (are newer than the threshold date
     *
     * @param dateThreshold
     * @param sortedCommits sorted commit list
     * @return
     */
    private List<Commit> getCommitsInRange(Long dateThreshold, List<Commit> sortedCommits) {
        List<Commit> commitsInRange = sortedCommits
                .stream()
                .filter(c -> c.getTimestamp().getTime() > dateThreshold)
                .collect(Collectors.toList());
        return commitsInRange;
    }

    /**
     * Grabs a date and subtract the number of days from it
     *
     * @param date
     * @param days
     * @return
     */
    private long subtractDaysToDate(Date date, int days) {
        Calendar calenderDate = Calendar.getInstance();
        calenderDate.setTime(date);
        calenderDate.add(Calendar.DATE, (-1 * days));
        return calenderDate.getTime().getTime();
    }

    /**
     * Get number of days between commit and a list of commits
     *
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private double getDaysBetweenLastCommit(Commit commit, List<Commit> olderCommits) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0;

        //Get the dateDifference
        List<Long> dateDifference = getDateDifference(commit, olderCommits);

        // /1000 --> to seconds --> /3600 to hours --> /24 --> to days
        return dateDifference.get(dateDifference.size() - 1) / 1000.0 / 3600.0 / 24.0;
    }

    /**
     * Get number of added lines for the last n days
     *
     * @param days
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private double getNumberOfAddedLinesForDays(int days, Commit commit, List<Commit> olderCommits) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);

        //Sum all added lines over all versions of all commits and return it
        return commitsInRange
                .stream()
                .mapToLong(
                        c -> c.getVersions()
                                .stream()
                                //This quers is insane
                                .map(v -> HibernateUtil.fetchLazyContent(v))
                                .mapToInt(v -> v.getLinesAdded())
                                .sum())
                .sum();
    }

    /**
     * Get number of deleted lines for the last n days
     *
     * @param days
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private double getNumberOfDeletedLinesForDays(int days, Commit commit, List<Commit> olderCommits) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);

        //Sum all added lines over all versions of all commits and return it
        return commitsInRange
                .stream()
                .mapToLong(
                        c -> c.getVersions()
                                .stream()
                                .map(v -> HibernateUtil.fetchLazyContent(v))
                                .mapToInt(v -> v.getLinesDeleted())
                                .sum())
                .sum();
    }

    /**
     * Get number of authors for the last n days
     *
     * @param days
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private double getNumberOfAuthorsForDays(int days, Commit commit, List<Commit> olderCommits) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);

        //Sum all added lines over all versions of all commits and return it
        Map<String, Integer> authors = new HashMap<String, Integer>();

        commitsInRange
                .forEach(
                        c -> authors.put(c.getAuthor(), 1)
                );

        return authors.size();
    }

    /**
     * Get number of issues for the last n days
     *
     * @param days
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private double getNumberOfIssuesForDays(int days, Commit commit, List<Commit> olderCommits, String type) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);
        double issueCount = 0;

        for (Commit c : commitsInRange)
            for (Issue i : commit.getIssues())
                if (i.getType().equals(type))
                    issueCount++;

        return issueCount;
    }
}
