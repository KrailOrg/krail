package util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Used mainly to overcome the differences between Intellij IDEA and Eclipse in their default project paths. For
 * example, when running a V7 test in Eclipse the current directory is V7, but in IDEA it is the parent directory (the
 * master project) v7
 * <p/>
 * Created by dsowerby on 22/06/14.
 */
public class TestUtils {

	private static final boolean runningIDEA;

	static {
		Path path = Paths.get("");
		String s = path.toAbsolutePath().toString();
		runningIDEA = s.endsWith("v7");
	}

	public static File projectRootV7() {
		return resourcePath("V7");
	}

	private static File resourcePath(String projectName) {
		Path path = Paths.get("");
		File root = path.toAbsolutePath().toFile();
		if (runningIDEA) {
			root = new File(root, projectName);

		}
		return root;
	}
}
