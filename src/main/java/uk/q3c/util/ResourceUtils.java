/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.util;

import com.vaadin.server.VaadinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ResourceUtils {
    private static Logger log = LoggerFactory.getLogger(ResourceUtils.class);

    /**
     * Returns the base directory path for the application if there is a VaadinService is running, or throws a {@link
     * IllegalStateException} if no service is running
     *
     * @return
     */
    public static String applicationBasePath() {
        return applicationBaseDirectory().getAbsolutePath();

    }

    /**
     * Returns the base directory path for the application if there is a VaadinService is running, or throws a {@link
     * IllegalStateException} if no service is running
     *
     * @return
     */
    public static File applicationBaseDirectory() {
        if (VaadinService.getCurrent() != null) {
            File baseDir = VaadinService.getCurrent()
                                        .getBaseDirectory();
            if (baseDir != null) {
                log.info("Application base directory (from VaadinService) is {}", baseDir.getAbsolutePath());
            } else {
                log.warn("Application base directory has not been set");
            }
            return baseDir;
        }
        throw new IllegalStateException("There is no current VaadinService");
    }

    /**
     * a convenience method creating a {@link File} object referencing {user.home}/temp
     *
     * @return
     */
    public static File userTempDirectory() {
        return new File(userHomeDirectory(), "temp");
    }

    /**
     * a convenience method equivalent to creating a {@link File} object using the System property 'user.home'
     *
     * @return
     */
    public static File userHomeDirectory() {
        return new File(System.getProperty("user.home"));
    }

    public static File configurationDirectory() {
        return new File(applicationBaseDirectory(), "WEB-INF");

    }

    public static File resourcePath(String file) {
        File baseDir;
        if (VaadinService.getCurrent() != null) {
            baseDir = applicationBaseDirectory();
        }
        baseDir = new File("src/main/resources");
        File resourceFile = new File(baseDir, file);
        return resourceFile;
    }
}
