/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.util;

import com.google.common.base.Preconditions;
import com.vaadin.server.VaadinService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DefaultResourceUtils implements ResourceUtils {
    private static Logger log = LoggerFactory.getLogger(DefaultResourceUtils.class);


    @Override
    public String applicationBasePath() {
        return applicationBaseDirectory().getAbsolutePath();

    }

    @Override
    public File applicationBaseDirectory() {
        if (VaadinService.getCurrent() != null) {
            File baseDir = VaadinService.getCurrent()
                                        .getBaseDirectory();
            if (baseDir != null) {
                log.info("Application base directory (from VaadinService) is {}", baseDir.getAbsolutePath());
            } else {
                log.error("Application base directory has not been set");
            }
            return baseDir;
        }
        throw new IllegalStateException("There is no current VaadinService");
    }


    @Override
    public File userTempDirectory() {
        return new File(userHomeDirectory(), "temp");
    }


    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    @Override
    public File userHomeDirectory() {
        return new File(System.getProperty("user.home"));
    }

    @Override
    public File configurationDirectory() {
        return webInfDirectory();
    }

    @Override
    public File webInfDirectory() {
        return new File(applicationBaseDirectory(), "WEB-INF");
    }

    @Override
    public File resourcePath(String fileName) {
        Preconditions.checkNotNull(fileName);
        File baseDir;
        if (VaadinService.getCurrent() != null) {
            baseDir = applicationBaseDirectory();
        } else {
            baseDir = new File("src/main/resources");
        }
        return new File(baseDir, fileName);
    }
}
