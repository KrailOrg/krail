package util;

import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by David Sowerby on 16/11/14.
 */
public class FileTestUtil {

    /**
     * Make a line by line comparison of the text files provided at {@code file1} and {@code file2}.  If they are not
     * the same, the comparison stops at the first mis-match and returns a {@link TestResult} with pass=false.  Lines
     * given in {@code ignore} are not compared - this can be useful, for example, where a line contains a timestamp.
     * Even when a line is ignored for comparison, it must exist in both files for match to succeed.
     *
     * @param ignore
     *         optional lines to ignore
     * @param file1
     *         the first file used in comparison
     * @param file2
     *         the second file used in comparison
     *
     * @throws IOException
     */
    public static Optional<String> compare(File file1, File file2, Integer... ignore) throws IOException {
        List<String> list1 = FileUtils.readLines(file1);
        List<String> list2 = FileUtils.readLines(file2);
        List<Integer> ignores = Arrays.asList(ignore);
        boolean pass = true;

        int max = list1.size() < list2.size() ? list1.size() : list2.size();
        //
        for (int i = 0; i < max; i++) {
            if (!ignores.contains(i)) {
                String item1 = list1.get(i);
                String item2 = list2.get(i);

                if (!item1.equals(item2)) {
                    pass = false;
                    String comment = "line " + i + " is not the same. '" + item1 + "' ... '" + item2 + "'";
                    return Optional.of(comment);
                }
            }
        }

        if (list1.size() != list2.size()) {
            String comment = "Compared successfully up to line " + max + "but lists are of different size, lists 1 " +
                    "and 2 have " + list1.size() + " and " + list2.size() + " lines respectively";
            return Optional.of(comment);
        }
        return Optional.absent();
    }

    public static class TestResult {
        boolean pass;
        String comment;
    }
}
