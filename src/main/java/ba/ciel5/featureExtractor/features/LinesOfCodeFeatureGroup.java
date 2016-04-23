/**
 * Created on 12.04.2016.
 * Feature group of line of code
 * @author ymeke
 */

package ba.ciel5.featureExtractor.features;

import ba.ciel5.featureExtractor.model.Version;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import ba.ciel5.featureExtractor.utils.Average;

import java.util.*;


public class LinesOfCodeFeatureGroup implements IFeatureGroup {

    public Map<String, Double> extract(Version version, CompilationUnit ast, char[] code) {
        double physicalLinesCount = 0;
        double sourceLinesCount = 0;
        double blankLinesCount = 0;
        double commentLinesCount = 0;
        double minLineLength = Integer.MAX_VALUE;
        double maxLineLength = 0;
        double medLineLength = 0;

        List<Integer> lengths = new ArrayList<Integer>();
        String codeString = new String(code);
        for (String line : codeString.split("\n")) {
            physicalLinesCount++;
            if (line.trim().length() == 0)
                blankLinesCount++;

            lengths.add(line.length());
        }

        String commentLessCodeString = codeString;
        for (Object commentObj : ast.getCommentList()) {
            Comment comment = (Comment) commentObj;
            String commentString = "";
            int start = comment.getStartPosition();
            int length = comment.getLength();
            if (comment.isLineComment() == Boolean.TRUE) {
                for (int i = start; i < code.length; i++) {
                    commentString += code[i];
                    if (code[i] == '\n') {
                        break;
                    }
                }
            } else {
                for (int i = start; i < start + length; i++) {
                    commentString += code[i];
                }
            }
            commentLinesCount += countLines(commentString);
            commentLessCodeString = commentLessCodeString.replace(commentString, "");
        }

        for (String line : commentLessCodeString.split("\n")) {
            if (line.trim().length() != 0) {
                sourceLinesCount++;
            }
        }

        Collections.sort(lengths);
        if (lengths.size() > 0) {
            minLineLength = lengths.get(0);
            maxLineLength = lengths.get(lengths.size() - 1);
            medLineLength = Average.getMedianFromIntegers(lengths);
        } else
            minLineLength = 0;

        Map<String, Double> map = new HashMap<String, Double>();
        map.put("PLOC", physicalLinesCount);
        map.put("SLOC", sourceLinesCount);
        map.put("BLOC", blankLinesCount);
        map.put("CLOC", commentLinesCount);
        map.put("MINLINE", minLineLength);
        map.put("MAXLINE", maxLineLength);
        map.put("MEDLINE", medLineLength);
        return map;
    }

    private static int countLines(String string) {
        return string.split("\n").length;
    }
}
