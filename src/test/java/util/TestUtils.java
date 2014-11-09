/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Used mainly to overcome the differences between Intellij IDEA and Eclipse in their default project paths. For
 * example, when running a Krail test in Eclipse the current directory is V7, but in IDEA it is the parent directory
 * (the
 * master project) krail
 * <p/>
 * Created by dsowerby on 22/06/14.
 */
public class TestUtils {

    private static final boolean runningIDEA;

    static {
        Path path = Paths.get("");
        String s = path.toAbsolutePath()
                       .toString();
        runningIDEA = s.endsWith("v7");
    }

    public static File projectRootV7() {
        return resourcePath("V7");
    }

    private static File resourcePath(String projectName) {
        Path path = Paths.get("");
        File root = path.toAbsolutePath()
                        .toFile();
        if (runningIDEA) {
            root = new File(root, projectName);

        }
        return root;
    }
}
