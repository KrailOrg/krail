package uk.co.q3c.v7.testutil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A couple of helper methods to ensure that the correct directory is looked up for file related test operations. This
 * is useful mainly because of the difference between IDEA and Eclipse in their treatment of 'master' and 'sub' projects
 * ... IDEA uses a different project path to Eclipse (IDEA runs from the root project, 'v7') see
 * https://github.com/davidsowerby/v7/issues/253
 *
 * @author dsowerby
 */
public class TestResource {

    /**
     * Returns a File object representing the 'src/test/java' folder within the current module (IDEA) or sub-project
     * (Eclipse)
     *
     * @param moduleName the IDEA module (Eclipse sub-project) for which the test root is required
     * @return File object for the test root directory
     */
    public static File testJavaRootDir(String moduleName) {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        String baseDir = (s.endsWith("V7")) ? "src/test/java" : "V7/src/test/java";
        return new File(baseDir);
    }

    /**
     * Returns a File object representing the 'src/test/resources' folder within the current module (IDEA) or
     * sub-project (Eclipse)
     *
     * @param moduleName the IDEA module (Eclipse sub-project) for which the test root is required
     * @return File object for the test root directory
     */
    public static File testResourceRootDir(String moduleName) {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        String baseDir = (s.endsWith("V7")) ? "src/test/resources" : "V7/src/test/resources";
        return new File(baseDir);
    }
}
