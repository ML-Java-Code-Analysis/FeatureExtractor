package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.features.IFeatureGroup;
import ba.ciel5.featureExtractor.model.Commit;
import ba.ciel5.featureExtractor.model.Issue;
import ba.ciel5.featureExtractor.model.Version;
import ba.ciel5.featureExtractor.utils.Average;
import ba.ciel5.featureExtractor.utils.HibernateUtil;
import javafx.util.Pair;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.hibernate.HibernateError;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private enum FileChangeType {
        ADDED, DELETED, RENAMED, CHANGED
    }

    @Override
    public Map<String, Double> extract(List<Commit> commits, Version version, CompilationUnit ast, char[] code) {

        logger = Logger.getLogger("main");

        Map<String, Double> map = new HashMap<String, Double>();
        List<Integer> days = new ArrayList<Integer>(Arrays.asList(1, 7, 30, 90, 180, 365, 730));

        List<Commit> commitResult = commits.stream().filter( c ->
                c.getId().equals(version.getCommitId())
        ).collect(Collectors.toList());
        Commit commit = commitResult.get(0);

        List<Commit> olderCommits = getAllOlderCommits(commit, getSortedCommitsByTimestamp(commits));

        map.put("DBLC", getDaysBetweenLastCommit(commit, olderCommits));

        // process every feature for at list all days in day array
        for (Integer day : days) {
            map.put("NOAL" + day.toString() + "D", getNumberOfAddedLinesForDays(day, commit, olderCommits));
            map.put("NODL" + day.toString() + "D", getNumberOfDeletedLinesForDays(day, commit, olderCommits));
            map.put("NOA" + day.toString() + "D", getNumberOfAuthorsForDays(day, commit, olderCommits));
            map.put("NOB" + day.toString() + "D", getNumberOfIssuesForDays(day, commit, olderCommits, IssueType.BUG.toString()));
            map.put("NOE" + day.toString() + "D", getNumberOfIssuesForDays(day, commit, olderCommits, IssueType.ENHANCEMENT.toString()));
            putMinMaxMedMeanToMap(map, getDaysBetweenOlderCommits(day, commit, olderCommits), "DBOC", day);

            //do for every file change type
            Map<String, List<Double>> numberOfFiles = getNumberOfFilesForOlderCommits(day, commit, olderCommits);
            Stream.of(FileChangeType.values()).forEach(
                    t -> putMinMaxMedMeanToMap(map, numberOfFiles.get(t.toString()), "NOF" + t.toString().charAt(0) + "FOC", day)
            );
            Stream.of(FileChangeType.values()).forEach(
                    t -> numberOfFiles.get(t.toString()).forEach(
                            cs -> map.put("NO" + t.toString().charAt(0) + "F" + day.toString() + "D", cs)
                    )
            );
        }

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
     * Fill map with Min Max Med and Mean of a list of doubles
     *
     * @param map     to fill
     * @param list    list with doubles
     * @param feature feature name for the name in the map
     * @param day     days for the name in in the map
     */
    private void putMinMaxMedMeanToMap(Map<String, Double> map, List<Double> list, String feature, Integer day) {
        if (list.size() == 0) {
            map.put("Min" + feature + day.toString() + "D", 0.0);
            map.put("Max" + feature + day.toString() + "D", 0.0);
            map.put("Med" + feature + day.toString() + "D", 0.0);
            map.put("Mean" + feature + day.toString() + "D", 0.0);
            return;
        }
        map.put("Min" + feature + day.toString() + "D", list.stream().mapToDouble(d -> d).min().getAsDouble());
        map.put("Max" + feature + day.toString() + "D", list.stream().mapToDouble(d -> d).max().getAsDouble());
        map.put("Med" + feature + day.toString() + "D", Average.getMedianFromDoubles(list));
        map.put("Mean" + feature + day.toString() + "D", list.stream().mapToDouble(d -> d).average().getAsDouble());
    }

    /**
     * Get number of days between commit and a list of commits
     *
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private Double getDaysBetweenLastCommit(Commit commit, List<Commit> olderCommits) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0.0;

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
    private Double getNumberOfAddedLinesForDays(int days, Commit commit, List<Commit> olderCommits) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0.0;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);

        //Sum all added lines over all versions of all commits and return it
        Double numberOfAddedLines = 0.0;
        numberOfAddedLines += commitsInRange
                .stream()
                .mapToLong(
                        c -> c.getVersions()
                                .stream()
                                .mapToInt(v -> v.getLinesAdded())
                                .sum())
                .sum();
        return numberOfAddedLines;
    }

    /**
     * Get number of deleted lines for the last n days
     *
     * @param days
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private Double getNumberOfDeletedLinesForDays(int days, Commit commit, List<Commit> olderCommits) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0.0;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);

        //Sum all added lines over all versions of all commits and return it
        Double numberOfDeletedLines = 0.0;
        numberOfDeletedLines += commitsInRange
                .stream()
                .mapToLong(
                        c -> c.getVersions()
                                .stream()
                                .mapToInt(v -> v.getLinesDeleted())
                                .sum())
                .sum();
        return numberOfDeletedLines;
    }

    /**
     * Get number of authors for the last n days
     *
     * @param days
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private Double getNumberOfAuthorsForDays(int days, Commit commit, List<Commit> olderCommits) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0.0;

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

        Double numberOfAuthors = 0.0;
        numberOfAuthors += authors.size();

        return numberOfAuthors;
    }

    /**
     * Get number of issues for the last n days
     *
     * @param days
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return
     */
    private Double getNumberOfIssuesForDays(int days, Commit commit, List<Commit> olderCommits, String type) {

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return 0.0;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);
        Double issueCount = 0.0;

        for (Commit c : commitsInRange) {
            List<Issue> issues = (List<Issue>) commit.getIssues();
            for (Issue i : issues)
                if (i.getType().equals(type))
                    issueCount++;
        }

        return issueCount;
    }

    /**
     * Get number of days between older commits
     *
     * @param days
     * @param commit
     * @param olderCommits commits that are older than commit sorted
     * @return a sorted list of days between the commit and all older commits
     */
    private List<Double> getDaysBetweenOlderCommits(int days, Commit commit, List<Commit> olderCommits) {
        List<Double> daysBetweenOlderCommits = new ArrayList<Double>();
        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return daysBetweenOlderCommits;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);

        commitsInRange.forEach(
                c -> daysBetweenOlderCommits.add(
                        (commit.getTimestamp().getTime() - c.getTimestamp().getTime()) / 1000.0 / 3600.0 / 24.0
                )
        );

        Collections.sort(daysBetweenOlderCommits);

        return daysBetweenOlderCommits;
    }

    /**
     * get added, deleted, renamed and changed files for a time period
     *
     * @param days         time period
     * @param commit       actual commit
     * @param olderCommits older commit
     * @return returns a map with lists
     */
    private Map<String, List<Double>> getNumberOfFilesForOlderCommits(int days, Commit commit, List<Commit> olderCommits) {
        Map<String, List<Double>> map = new HashMap<String, List<Double>>();

        // create hashmap for all file change types
        Stream.of(FileChangeType.values()).forEach(
                t -> map.put(t.toString(), new ArrayList<Double>())
        );

        //There is no commit before the actual
        if (olderCommits.size() == 0)
            return map;

        //Get date threshold
        Long dateThreshold = subtractDaysToDate(commit.getTimestamp(), days);

        //Get commits that are newer than the threshold
        List<Commit> commitsInRange = getCommitsInRange(dateThreshold, olderCommits);

        commitsInRange.forEach(
                c -> {
                    map.get(FileChangeType.ADDED.toString()).add(c.getAddedFilesCount() / 1.0);
                    map.get(FileChangeType.DELETED.toString()).add(c.getDeletedFilesCount() / 1.0);
                    map.get(FileChangeType.RENAMED.toString()).add(c.getRenamedFilesCount() / 1.0);
                    map.get(FileChangeType.CHANGED.toString()).add(c.getChangedFilesCount() / 1.0);
                }
        );

        //sort all lists in map
        Stream.of(FileChangeType.values()).forEach(
                t -> Collections.sort(map.get(t.toString()))
        );

        return map;
    }
}
